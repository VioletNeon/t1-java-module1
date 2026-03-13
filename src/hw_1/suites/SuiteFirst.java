package hw_1.suites;

import hw_1.annotations.*;

// Проверка на @Test (priority и name), @Disabled, @BeforeSuite, @AfterSuite, @BeforeEach, @AfterEach.

public class SuiteFirst {
    @BeforeSuite
    static void beforeSuite() {
        System.out.println("beforeSuite");
    }

    @AfterSuite
    static void afterSuite() {
        System.out.println("afterSuite");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach");
    }

    @AfterEach
    void afterEach() {
        System.out.println("afterEach");
    }

    @Test(priority = 1, name = "1")
    void testPriorityOne() {
        System.out.println("testPriorityOne");
    }

    @Test(priority = 3, name = "3")
    void testPriorityThree() {
        System.out.println("testPriorityThree");
    }

    @Test
    void testPriorityDefault() {
        System.out.println("testPriorityDefault");
    }

    @Test(name = "disabled")
    @Disabled
    void testDisableStatus() {
        System.out.println("!!! Не должен попасть в лог - testDisableStatus");
    }
}
