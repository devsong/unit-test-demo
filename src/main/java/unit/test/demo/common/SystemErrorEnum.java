package unit.test.demo.common;

import lombok.Getter;

@Getter
public enum SystemErrorEnum {

    INTERNAL_SERVER_ERROR(Constants.SysMajorCode.MAJOR_5XX, Constants.SysMinorCode.START_UP_CODE, "demo system error"),

    ILLEGAL_ARGUMENT_ERROR(Constants.SysMajorCode.MAJOR_4XX, Constants.SysMinorCode.ILLEGAL_ARGUMENT, "illegal argument"),

    DUPLICATE_REQUEST_ERROR(Constants.SysMajorCode.MAJOR_4XX, Constants.SysMinorCode.DUPLICATE_REQUEST, "duplicate request"),

    USER_NOT_FOUND_ERROR(Constants.SysMajorCode.MAJOR_4XX, Constants.SysMinorCode.USER_NOT_FOUND, "user not found");

    private int major;
    private int minor;
    private String msg;

    SystemErrorEnum(int major, int minor, String msg) {
        this.major = major;
        this.minor = minor;
        this.msg = msg;
    }

    public int getErrorCode() {
        return this.major + minor;
    }
}
