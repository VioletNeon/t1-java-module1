package hw_1.suites;

import hw_1.annotations.*;

// Проверка @Order

public class SuiteSecond {
    @Test(name = "1")
    @Order(1)
    void testOrderOne() {
        System.out.println("testOrderOne");
    }

    @Test(name = "7")
    @Order(7)
    void testOrderSeven() {
        System.out.println("testOrderSeven");
    }

    @Test(name = "5")
    @Order
    void testOrderDefault() {
        System.out.println("testOrderDefault");
    }

    @Test(name = "disabled")
    @Order
    @Disabled
    void testDisableStatus() {
        System.out.println("!!! Не должен попасть в лог - testDisableStatus_2");
    }
}
