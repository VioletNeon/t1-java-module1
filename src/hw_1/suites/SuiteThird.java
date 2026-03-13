package hw_1.suites;

import hw_1.annotations.*;

// Проверка @BeforeSuite, @AfterSuite с установкой на не статические методы

public class SuiteThird {
    @BeforeSuite
    void beforeSuite() {
        System.out.println("beforeSuite");
    }

    @AfterSuite
    void afterSuite() {
        System.out.println("afterSuite");
    }

    @Test(priority = 4)
    void testPriorityFour() {
        System.out.println("testPriorityFour");
    }

    @Test(priority = 3)
    void testPriorityThree() {
        System.out.println("testPriorityThree");
    }

    @Test
    void testPriorityDefault() {
        System.out.println("testPriorityDefault");
    }
}
