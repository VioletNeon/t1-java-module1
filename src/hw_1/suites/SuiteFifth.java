package hw_1.suites;

import hw_1.annotations.*;
import hw_1.exceptions.TestAssertionError;

// Проверка на TestAssertionError и Error.

public class SuiteFifth {
    @Test
    void testError() {
        System.out.println("testError");

        throw new RuntimeException("Просто какая-то ошибка");
    }

    @Test
    void testAssertionError() {
        throw new TestAssertionError("Тест не прошел потому что - потому что");
    }
}
