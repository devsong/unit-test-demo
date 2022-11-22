package unit.test.demo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author zhisong.guan
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("error_code")
    private int errorCode;

    private String message;

    private Object payload;

    public ErrorResponse(int errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorResponse(SystemErrorEnum systemErrorEnum) {
        this(systemErrorEnum.getErrorCode(), systemErrorEnum.getMsg());
    }

    public ErrorResponse(int errorCode, String message) {
        this(errorCode, message, null);
    }

    public ErrorResponse(int errorCode, Object payload) {
        this(errorCode, null, payload);
    }
}
