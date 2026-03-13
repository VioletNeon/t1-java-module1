package hw_1.suites;

import hw_1.annotations.*;

// Проверка на @BeforeEach, @AfterEach с установкой на статические методы

public class SuiteFourth {
    @BeforeEach
    static void beforeEach() {
        System.out.println("beforeEach");
    }

    @AfterEach
    static void afterEach() {
        System.out.println("afterEach");
    }

    @Test
    void testPriorityDefaultOne() {
        System.out.println("testPriorityDefault_1");
    }

    @Test
    void testPriorityDefaultTwo() {
        System.out.println("testPriorityDefault_2");
    }

    @Test
    void testPriorityDefaultThree() {
        System.out.println("testPriorityDefault_3");
    }
}
