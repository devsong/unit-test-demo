package unit.test.demo.client;

/**
 * @author zhisong.guan
 */
public interface PaymentClient {
    /**
     * get user balance
     *
     * @param userId
     * @return
     */
    Long getBalance(Long userId);
}
