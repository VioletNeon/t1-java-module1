package hw_1;

public class TestDetails {
    private TestResult resultType;
    private String testName;
    private Throwable exception;

    public TestDetails(TestResult resultType, String testName) {
        this(resultType, testName, null);
    }

    TestDetails(TestResult resultType, String testName, Throwable exception) {
        this.resultType = resultType;
        this.testName = testName;
        this.exception = exception;
    }

    public TestResult getResultType() {
        return resultType;
    }

    public String getTestName() {
        return testName;
    }

    public Throwable getException() {
        return exception;
    }

    public void setResultType(TestResult resultType) {
        this.resultType = resultType;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Test{");
        sb.append("resultType=").append(resultType);
        sb.append(", testName='").append(testName).append("'");

        if (hasException()) {
            sb.append(", exception=").append(exception.getClass().getSimpleName())
                    .append(": ").append(exception.getMessage());
        } else {
            sb.append(", exception=null");
        }

        return sb.append("}").toString();
    }
}
