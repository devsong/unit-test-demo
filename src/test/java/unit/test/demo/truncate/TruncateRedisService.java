package unit.test.demo.truncate;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TruncateRedisService implements TruncateService {
    private final RedissonClient redissonClient;

    @Override
    public void truncate() {
        redissonClient.getKeys().flushdb();
    }
}
