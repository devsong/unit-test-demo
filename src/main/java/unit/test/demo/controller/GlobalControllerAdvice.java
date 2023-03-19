package unit.test.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unit.test.demo.common.ErrorResponse;
import unit.test.demo.common.SystemErrorEnum;
import unit.test.demo.exception.DuplicateRequestException;

/**
 * @author zhisong.guan
 */
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Exception e) {
        log.error("system error", e);
        return new ErrorResponse(SystemErrorEnum.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(IllegalArgumentException e) {
        log.error("illegal argument", e);
        return new ErrorResponse(SystemErrorEnum.ILLEGAL_ARGUMENT_ERROR.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(DuplicateRequestException e) {
        log.error("duplicate request", e);
        return new ErrorResponse(SystemErrorEnum.DUPLICATE_REQUEST_ERROR.getErrorCode(), e.getMessage());
    }
}
