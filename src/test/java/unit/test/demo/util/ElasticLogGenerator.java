package unit.test.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * date:  2023/3/1
 * author:guanzhisong
 */
public class ElasticLogGenerator {
    private static final Logger PERF_LOG = LoggerFactory.getLogger("sysPerfLogger");
    static String[] CONTROLLER_ARR = {"controller0", "controller1", "controller2", "controller3", "controller4", "controller5",};
    static String[] METHOD_ARR = {"method0", "method1", "method2", "method3", "method4", "method5",};

    public static void main(String[] args) {
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleAtFixedRate(() -> {
            int rand = ThreadLocalRandom.current().nextInt(100);
            for (int i = 0; i < rand; i++) {
                SysPerfLogDto sysPerfLogDto = SysPerfLogDto.builder()
                        .traceId(UUID.randomUUID().toString().replaceAll("-", ""))
                        .spanId("span")
                        .product("product")
                        .groupName("group")
                        .app("spring-test-demo")
                        .clazz(CONTROLLER_ARR[rand % CONTROLLER_ARR.length])
                        .method(METHOD_ARR[rand % METHOD_ARR.length])
                        .code(rand % 2)
                        .instance("localhost")
                        .executeTimespan(rand % 50)
                        .build();

                PERF_LOG.info(JsonUtil.toJSONString(sysPerfLogDto));
            }
        }, 1, 10, TimeUnit.SECONDS);
    }
}
