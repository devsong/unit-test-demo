package unit.test.demo.config;

import unit.test.demo.client.PaymentClient;
import unit.test.demo.client.impl.SimplePaymentClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
