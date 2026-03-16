package hw_1;

import hw_1.suites.*;

import java.util.List;
import java.util.Map;

public class HW1 {
    public static void main(String[] args) {
        runSuit_1();
        runSuit_2();
        // runSuit_3();
        // runSuit_4();
        // runSuit_5();
    }

    public static void runSuit_1() {
        Map<TestResult, List<TestDetails>> result_1 = TestRunner.runTests(SuiteFirst.class);
        System.out.println("-----------------------");
        System.out.println("Выполнение SuiteFirst");
        System.out.println(result_1);
    }

    public static void runSuit_2() {
        Map<TestResult, List<TestDetails>> result_2 = TestRunner.runTests(SuiteSecond.class);
        System.out.println("-----------------------");
        System.out.println("Выполнение SuiteSecond");
        System.out.println(result_2);
    }

    public static void runSuit_3() {
        Map<TestResult, List<TestDetails>> result_3 = TestRunner.runTests(SuiteThird.class);
        System.out.println("-----------------------");
        System.out.println("Выполнение SuiteThird");
        System.out.println(result_3);
    }

    public static void runSuit_4() {
        Map<TestResult, List<TestDetails>> result_4 = TestRunner.runTests(SuiteFourth.class);
        System.out.println("-----------------------");
        System.out.println("Выполнение SuiteFourth");
        System.out.println(result_4);
    }

    public static void runSuit_5() {
        Map<TestResult, List<TestDetails>> result_5 = TestRunner.runTests(SuiteFifth.class);
        System.out.println("-----------------------");
        System.out.println("Выполнение SuiteFifth");
        System.out.println(result_5);
    }
}
