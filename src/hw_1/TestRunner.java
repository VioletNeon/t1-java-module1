package hw_1;

import hw_1.annotations.Test;
import hw_1.annotations.Disabled;
import hw_1.annotations.Order;
import hw_1.annotations.BeforeSuite;
import hw_1.annotations.AfterSuite;
import hw_1.annotations.BeforeEach;
import hw_1.annotations.AfterEach;
import hw_1.exceptions.BadTestClassError;
import hw_1.exceptions.TestAssertionError;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class TestRunner {
    public static Map<TestResult, List<TestDetails>> runTests(Class<?> testClass) {
        // Проверка возможности создания экземпляра тестового класса
        checkConstructor(testClass);

        // Сбор и валидация аннотаций
        Map<String, List<Method>> methods = getAnnotatedMethods(testClass);

        // Сортировка тестов
        List<Method> sortedTests = sortTestMethods(methods.get(Test.class.getSimpleName()));

        // Создание экземпляра класса
        Object testInstance = createInstance(testClass);

        // Подготовка мапы для сбора данных
        Map<TestResult, List<TestDetails>> results = new EnumMap<>(TestResult.class);
        for (TestResult result : TestResult.values()) {
            results.put(result, new ArrayList<>());
        }

        try {
            // @BeforeSuite
            executeMethods(methods.get(BeforeSuite.class.getSimpleName()), null);

            // Выполнение тестов
            for (Method testMethod : sortedTests) {
                TestDetails test = executeTest(
                        testMethod,
                        testInstance,
                        methods.get(BeforeEach.class.getSimpleName()),
                        methods.get(AfterEach.class.getSimpleName())
                );
                results.get(test.getResultType()).add(test);
            }

            // @AfterSuite
            executeMethods(methods.get(AfterSuite.class.getSimpleName()), null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении тестов", e);
        }

        return results;
    }

    public static Map<String, List<Method>> getAnnotatedMethods(Class<?> c) {
        Map<String, List<Method>> methods = new HashMap<>();

        for (Method method : c.getDeclaredMethods()) {
            var isStaticMethod = Modifier.isStatic(method.getModifiers());

            for (Annotation annotation : method.getDeclaredAnnotations()) {
                var annotationSimpleName = annotation.annotationType().getSimpleName();
                var annotationName = annotation.annotationType().getName();
                var isStaticAnnotation =
                        annotationName.equals(BeforeSuite.class.getName()) ||
                        annotationName.equals(AfterSuite.class.getName());
                var isNonStaticAnnotation =
                        annotationName.equals(Test.class.getName()) ||
                        annotationName.equals(AfterEach.class.getName()) ||
                        annotationName.equals(BeforeEach.class.getName());

                if (isNonStaticAnnotation && isStaticMethod) {
                    throw new BadTestClassError(annotationSimpleName + " метод не может быть статическим: " + method.getName());
                }

                if (isStaticAnnotation && !isStaticMethod) {
                    throw new BadTestClassError(annotationSimpleName + " метод должен быть статическим: " + method.getName());
                }

                methods
                    .computeIfAbsent(annotationSimpleName, k -> new ArrayList<>())
                    .add(method);
            }
        }

        return methods;
    }

    private static void checkConstructor(Class<?> c) {
        if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) {
            throw new BadTestClassError("Невозможно создать экземпляр класса: " + c.getName());
        }

        try {
            c.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BadTestClassError("Невозможно создать экземпляр класса: " + c.getName());
        }
    }

    private static Object createInstance(Class<?> c) {
        try {
            return c.getConstructor().newInstance();
        } catch (Exception e) {
            throw new BadTestClassError("Не удалось создать экземпляр класса: " + c.getName());
        }
    }

    private static List<Method> sortTestMethods(List<Method> methods) {
        return methods
                .stream()
                .sorted(
                   Comparator
                   .comparing((Method m) -> {
                       Order order = m.getAnnotation(Order.class);

                       return order != null ? order.value() : 5;
                   })
                   .thenComparing((Method m) -> m.getAnnotation(Test.class).priority(), Comparator.reverseOrder())
                   .thenComparing(Method::getName)
                )
                .collect(Collectors.toList());
    }

    private static void executeMethods(List<Method> methods, Object instance) {
        if (methods == null) return;

        for (Method method : methods) {
            try {
                method.setAccessible(true);

                if (instance == null) {
                    method.invoke(null);
                } else {
                    method.invoke(instance);
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при выполнении метода: " + method.getName(), e);
            }
        }
    }

    private static TestDetails executeTest(
        Method testMethod,
        Object instance,
        List<Method> beforeEach,
        List<Method> afterEach
    ) {
        TestDetails test;
        String testName = getTestName(testMethod);

        if (testMethod.isAnnotationPresent(Disabled.class)) {
            test = new TestDetails(TestResult.SKIPPED, testName);

            return test;
        }

        executeMethods(beforeEach, instance);

        try {
            testMethod.setAccessible(true);
            testMethod.invoke(instance);

            test = new TestDetails(TestResult.SUCCESS, testName);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if (cause instanceof TestAssertionError) {
                test = new TestDetails(TestResult.FAILED, testName, cause);
            } else {
                test = new TestDetails(TestResult.ERROR, testName, cause);
            }
        } catch (Exception e) {
            test = new TestDetails(TestResult.ERROR, testName, e);
        }

        executeMethods(afterEach, instance);

        return test;
    }

    private static String getTestName(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);

        return testAnnotation.name().isEmpty() ? method.getName() : testAnnotation.name();
    }
}