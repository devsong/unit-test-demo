package unit.test.demo.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unit.test.demo.config.properties.RedisConfigProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhisong.guan
 */
@Configuration
@RequiredArgsConstructor
//@Profile(value = "!test")
public class RedisConfig {
    private final RedisConfigProperties prop;
    private static final String REDIS_ADDRESS_FMT = "redis://%s:%s";

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format(REDIS_ADDRESS_FMT, prop.getHost(), prop.getPort()));
        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>();
        // 创建一个名称为"testMap"的缓存，过期时间ttl为24分钟，同时最长空闲时maxIdleTime为12分钟。
        config.put("unit-test-demo", new CacheConfig(30 * 60 * 1000, 15 * 60 * 1000));
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
