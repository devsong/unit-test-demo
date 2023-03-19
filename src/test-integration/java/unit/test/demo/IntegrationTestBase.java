package unit.test.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import redis.embedded.RedisServer;
import unit.test.demo.client.PaymentClient;
import unit.test.demo.common.TestConstants;
import unit.test.demo.repository.UserInfoRepository;
import unit.test.demo.service.IUserService;
import unit.test.demo.truncate.TruncateService;
import unit.test.demo.util.ResourceParseUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoApplication.class)
@ActiveProfiles("test")
@Slf4j
public abstract class IntegrationTestBase {
    public static final String BASE_JSON_PATH = ResourceParseUtil.BASE_JSON_PATH;
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DEFAULT_DATE_TIME_FMT = "yyyy-MM-dd HH:mm:ss";
    private static RedisServer redisServer = new RedisServer(TestConstants.REDIS_EMBEDDED_PORT);

    static {
        redisServer.start();
    }

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

//    @Autowired
//    protected RedisConfigProperties prop;

    @Autowired
    protected IUserService userService;

    @Autowired
    protected UserInfoRepository userInfoRepository;

    @MockBean
    protected PaymentClient paymentClient;

    @BeforeEach
    protected void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        // 清除mysql redis数据
        truncate();
        Mockito.reset(paymentClient);
    }

    private void truncate() {
        Map<String, TruncateService> truncateServiceMap = context.getBeansOfType(TruncateService.class);
        for (Map.Entry<String, TruncateService> entry : truncateServiceMap.entrySet()) {
            String key = entry.getKey();
            TruncateService service = entry.getValue();
            try {
                service.truncate();
            } catch (Exception e) {
                log.error("truncate key {} do truncate resources error", key, e);
            }
        }
    }

    @BeforeAll
    public static void beforeAllSetUp() {
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FMT, Locale.CHINA));
    }
}
