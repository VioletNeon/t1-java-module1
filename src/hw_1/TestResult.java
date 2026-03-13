package hw_1;

public enum TestResult {
    SUCCESS("тест выполнен успешно"),
    FAILED("условие теста провалено"),
    ERROR("тест упал с произвольным исключением"),
    SKIPPED("тест не исполнялся");

    private final String description;

    TestResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
