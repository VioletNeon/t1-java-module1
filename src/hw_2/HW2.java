package hw_2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HW2 {
    public static List<Integer> numberList = List.of(5, 2, 10, 9, 4, 3, 10, 1, 13);
    public static List<Employee> employeeList = List.of(
            new Employee("Владимир", 25, "Электрик"),
            new Employee("Пётр", 27, "Инженер"),
            new Employee("Александр", 30, "Инженер"),
            new Employee("Николай", 35, "Электрик"),
            new Employee("Иван", 36, "Инженер"),
            new Employee("Роман", 40, "Инженер")
    );
    public static List<String> wordList = List.of(
            "очень длинное словосочетание",
            "словосочетание",
            "супер длинное словосочетание в списке",
            "длинное словосочетание",
            "самое длинное словосочетание в списке",
            "наиболее длинное словосочетание"
    );

    public static void main(String[] args) {
        ex_1();
        ex_2();
        ex_3();
        ex_4();
        ex_5();
        ex_6();
        ex_7();
        ex_8();
    }

    public static void ex_1() {
        var result = numberList.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .min(Comparator.comparingInt((n) -> n))
                .orElse(-1);

        System.out.println("\n" + "1) 3-е наибольшее число в списке чисел " + numberList + " это " + result);
    }

    public static void ex_2() {
        var result = numberList.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .min(Comparator.comparingInt((n) -> n))
                .orElse(-1);

        System.out.println("\n" + "2) 3-е наибольшее 'уникальное' число в списке чисел " + numberList + " это " + result);
    }

    public static void ex_3() {
        var result = employeeList.stream()
                .filter(employee -> employee.title().equals("Инженер"))
                .sorted(Comparator.comparing(Employee::age).reversed())
                .map(Employee::name)
                .limit(3)
                .toList();

        System.out.println("\n" + "3) Список имен 3-х самых старших сотрудников с должностью 'Инженер', в порядке убывания возраста: " + result);
    }

    public static void ex_4() {
        var result = employeeList.stream()
                .filter(employee -> employee
                        .title()
                        .equals("Инженер")
                )
                .collect(Collectors.collectingAndThen(
                            Collectors.averagingDouble(Employee::age),
                            Math::round
                        )
                );

        System.out.println("\n" + "4) Средний возраст сотрудников с должностью 'Инженер': " + result);
    }

    public static void ex_5() {
        var result = wordList.stream()
                .max(Comparator.comparing(String::length))
                .orElse("");

        System.out.println("\n" + "5) Самое длинное словосочетание из списка: " + "'" + result + "'");
    }

    public static void ex_6() {
        String words = "ехал грека через реку видит грека в реке рак сунул в реку руку грека рак за руку греку цап";

        var result = Arrays.stream(words.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println("\n" + "6) Мапа с парой: слово - количество его повторений: " + result);
    }

    public static void ex_7() {
        System.out.println("\n" + "7) Строки из списка в порядке увеличения длины слова:");

        wordList.stream()
                .sorted(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()))
                .forEachOrdered(System.out::println);
    }

    public static void ex_8() {
        List<String> words = List.of("набор пяти слов с пробелом", "набор с одним длииииииииинным словом");

        var result = words.stream()
                .flatMap(word -> Arrays.stream(word.split(" ")))
                .max(Comparator.comparing(String::length))
                .orElse("");

        System.out.println("\n" + "8) Самое длинное слово из массива словосочетаний: " + result);
    }
}
