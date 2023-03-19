package unit.test.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unit.test.demo.client.PaymentClient;
import unit.test.demo.client.impl.SimplePaymentClient;

/**
 * @author zhisong.guan
 */
@Configuration
public class AppBeanConfig {

    @Bean
    @ConditionalOnMissingBean
    public PaymentClient paymentClient() {
        return new SimplePaymentClient();
    }
}
