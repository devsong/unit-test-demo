package unit.test.demo.benchmark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import unit.test.demo.service.IUserService;

import java.util.concurrent.CountDownLatch;

@Data
@Slf4j
@AllArgsConstructor
public class CreateUserThreadWithLockMode implements Runnable {
    private final CountDownLatch startLatch;
    private final CountDownLatch stopLatch;
    private final String username;
    private final String lockMode;
    private final IUserService userService;

    @Override
    public void run() {
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            userService.createUser(username, lockMode);
        } catch (Exception e) {
            log.error("create user error", e);
        } finally {
            stopLatch.countDown();
        }
    }
}
