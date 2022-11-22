package unit.test.demo.exception;

public class DuplicateRequestException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DuplicateRequestException(String msg) {
        super(msg);
    }
}
