package unit.test.demo.benchmark;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import unit.test.demo.IntegrationTestBase;
import unit.test.demo.common.LockMode;
import unit.test.demo.entity.UserInfoEntity;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class UserServiceBenchMarkTest extends IntegrationTestBase {
    private ExecutorService es = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new BasicThreadFactory.Builder().namingPattern("benchmark-%d").daemon(true).priority(Thread.NORM_PRIORITY).build(),
            new ThreadPoolExecutor.AbortPolicy());

    @ParameterizedTest
    @CsvSource({"2,w", "2,r", "2,none", "5,w", "5,r", "5,none", "10,w", "10,r", "10,none"})
    void should_generate_duplicate_username_when_parallel_request(int concurrentThread, String lockMode) throws Exception {
        // warm thread pool
        for (int i = 0; i < concurrentThread; i++) {
            es.submit(() -> System.out.println("warm thread pool"));
        }

        String username = "username";
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(concurrentThread);
        for (int i = 0; i < concurrentThread; i++) {
            es.submit(new CreateUserThreadWithLockMode(startLatch, stopLatch, username, lockMode, userService));
        }
        startLatch.countDown();
        stopLatch.await();
        List<UserInfoEntity> userInfoList = userInfoRepository.findListByUsername(username);
        System.out.println(String.format("user info list size %s", userInfoList.size()));
        if (StringUtils.equalsIgnoreCase(lockMode, LockMode.NONE.name())) {
            assertThat(userInfoList.size()).isGreaterThan(1);
        } else {
            assertThat(userInfoList.size()).isEqualTo(1);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10})
    void should_not_generate_duplicate_username_when_parallel_request_with_distribution_lock(int concurrentThread) throws Exception {
        // warm thread pool
        for (int i = 0; i < concurrentThread; i++) {
            es.submit(() -> System.out.println("warm thread pool"));
        }

        String username = "username";
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(concurrentThread);
        for (int i = 0; i < concurrentThread; i++) {
            es.submit(new CreateUserThreadWithDistributionLock(startLatch, stopLatch, username, userService));
        }
        startLatch.countDown();
        stopLatch.await();
        List<UserInfoEntity> userInfoList = userInfoRepository.findListByUsername(username);
        System.out.println(String.format("user info list size %s", userInfoList.size()));
        assertThat(userInfoList.size()).isEqualTo(1);
    }
}
