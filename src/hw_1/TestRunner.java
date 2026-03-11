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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TestRunner {
    public static Map<TestResult, List<TestDetails>> runTests(Class<?> testClass) {
        // Проверка возможности создания экземпляра тестового класса
        checkConstructor(testClass);

        // Сбор аннотаций
        List<Method> testMethods = getAnnotatedMethods(testClass, Test.class);
        List<Method> beforeEachMethods = getAnnotatedMethods(testClass, BeforeEach.class);
        List<Method> afterEachMethods = getAnnotatedMethods(testClass, AfterEach.class);
        List<Method> beforeSuiteMethods = getAnnotatedMethods(testClass, BeforeSuite.class);
        List<Method> afterSuiteMethods = getAnnotatedMethods(testClass, AfterSuite.class);

        // Проверка статических методов
        validateMethodTypes(
            testMethods,
            beforeEachMethods,
            afterEachMethods,
            beforeSuiteMethods,
            afterSuiteMethods
        );

        // Сортировка тестов
        List<Method> sortedTests = sortTestMethods(testMethods);

        // Создание экземпляра класса
        Object testInstance = createInstance(testClass);

        // Подготовка мапы для сбора данных
        Map<TestResult, List<TestDetails>> results = new EnumMap<>(TestResult.class);
        for (TestResult result : TestResult.values()) {
            results.put(result, new ArrayList<>());
        }

        try {
            // @BeforeSuite
            executeMethods(beforeSuiteMethods, null);

            // Выполнение тестов
            for (Method testMethod : sortedTests) {
                TestDetails test = executeTest(testMethod, testInstance, beforeEachMethods, afterEachMethods);
                results.get(test.getResultType()).add(test);
            }

            // @AfterSuite
            executeMethods(afterSuiteMethods, null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении тестов", e);
        }

        return results;
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

    private static List<Method> getAnnotatedMethods(Class<?> c, Class<? extends Annotation> annotation) {
        return Arrays.stream(c.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    private static void validateMethodTypes(
        List<Method> testMethods,
        List<Method> beforeEach,
        List<Method> afterEach,
        List<Method> beforeSuite,
        List<Method> afterSuite
    ) {
        // @Test, @BeforeEach, @AfterEach не должны быть статическими
        for (Method m : testMethods) {
            if (Modifier.isStatic(m.getModifiers())) {
                throw new BadTestClassError("@Test метод не может быть статическим: " + m.getName());
            }
        }

        for (Method m : beforeEach) {
            if (Modifier.isStatic(m.getModifiers())) {
                throw new BadTestClassError("@BeforeEach метод не может быть статическим: " + m.getName());
            }
        }

        for (Method m : afterEach) {
            if (Modifier.isStatic(m.getModifiers())) {
                throw new BadTestClassError("@AfterEach метод не может быть статическим: " + m.getName());
            }
        }

        // @BeforeSuite, @AfterSuite должны быть статическими
        for (Method m : beforeSuite) {
            if (!Modifier.isStatic(m.getModifiers())) {
                throw new BadTestClassError("@BeforeSuite метод должен быть статическим: " + m.getName());
            }
        }
        for (Method m : afterSuite) {
            if (!Modifier.isStatic(m.getModifiers())) {
                throw new BadTestClassError("@AfterSuite метод должен быть статическим: " + m.getName());
            }
        }
    }

    private static List<Method> sortTestMethods(List<Method> methods) {
        return methods
                .stream()
                .sorted(
                   Comparator
                   .comparing((Method m) -> m.getAnnotation(Test.class).priority(), Comparator.reverseOrder())
                   .thenComparing(m -> {
                       Order order = m.getAnnotation(Order.class);

                       return order != null ? order.value() : 5;
                   })
                   .thenComparing(Method::getName)
                )
                .collect(Collectors.toList());
    }

    private static void executeMethods(List<Method> methods, Object instance) {
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