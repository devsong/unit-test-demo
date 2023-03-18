package unit.test.demo.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unit.test.demo.common.Constants;
import unit.test.demo.config.properties.RedisConfigProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhisong.guan
 */
@Configuration
@RequiredArgsConstructor
@EnableCaching
// @Profile(value = "!test")
public class RedisConfig {
    private static final int TTL_TIME = 30 * 60 * 1000;
    private static final int MAX_IDLE_TIME = 15 * 60 * 1000;

    private static final String REDIS_ADDRESS_FMT = "redis://%s:%s";

    private final RedisConfigProperties prop;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format(REDIS_ADDRESS_FMT, prop.getHost(), prop.getPort()));
        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>();
        config.put(Constants.CACHE_NAME, new CacheConfig(TTL_TIME, MAX_IDLE_TIME));
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
