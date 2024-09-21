package unit.test.demo.aspect;

import org.springframework.core.Ordered;

public interface AopOrders {
    int HIGH = Ordered.HIGHEST_PRECEDENCE;

    int GATEWAY_ORDER = -1;

    int DISTRIBUTION_LOCK_ORDER = 1;

    int TX_ORDERED = 2;

    int LOW = Ordered.LOWEST_PRECEDENCE;
}
