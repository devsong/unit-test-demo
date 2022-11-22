package unit.test.demo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhisong.guan
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisConfigProperties {
    private String host;
    private int port;
}
