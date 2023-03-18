package unit.test.demo.common;

public class Constants {

    public static final String CACHE_NAME = "unit-test-demo";

    public static class SysMajorCode {
        public static final int MAJOR_5XX = 500000;
        public static final int MAJOR_4XX = 400000;
        public static final int MAJOR_3XX = 300000;
        public static final int MAJOR_2XX = 200000;
        public static final int MAJOR_1XX = 100000;
    }

    public static class SysMinorCode {
        public static final int START_UP_CODE = 0;
        public static final int ILLEGAL_ARGUMENT = 1;
        public static final int USER_NOT_FOUND = 2;
        public static final int DUPLICATE_REQUEST = 3;
    }
}
