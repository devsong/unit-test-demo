package unit.test.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.service.IUserService;

import java.util.concurrent.TimeUnit;

/**
 * @author zhisong.guan
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Slf4j
public class UserController {
    private static final Long MAX_USERID = (long) Integer.MAX_VALUE;
    private static final Long MIN_USERID = 1L;
    private final IUserService userService;

    @GetMapping(value = "/info/{userId}")
    public UserInfoDto queryUserInfo(@PathVariable(value = "userId") Long userId) {
        if (userId == null || userId < MIN_USERID || userId > MAX_USERID) {
            throw new IllegalArgumentException(String.format("userId %s out of range", userId));
        }
        return userService.queryUserInfo(userId);
    }

    @PostMapping
    public UserCreationDto createUser(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username can not be empty for createUser");
        }
        return userService.createUser(username);
    }

    @GetMapping(value = "/query-by-username", params = {"username"})
    public UserInfoDto queryByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("username can not be empty for queryByUsername");
        }
        return userService.queryByUsername(username);
    }

    @GetMapping("cost")
    @SneakyThrows
    public Object cost() {
        int time = RandomUtils.nextInt(30, 80);
        TimeUnit.MILLISECONDS.sleep(time);
        return "success";
    }

    @GetMapping("log_test")
    public Object logTest(String str) {
        log.info("info msg {}", str);
        log.error("error msg");
        return "success";
    }
}
