package unit.test.demo.client.impl;

import lombok.extern.slf4j.Slf4j;
import unit.test.demo.client.PaymentClient;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zhisong.guan
 */
@Slf4j
public class SimplePaymentClient implements PaymentClient {
    @Override
    public Long getBalance(Long userId) {
        log.info("payment client called");
        return ThreadLocalRandom.current().nextLong();
    }
}
