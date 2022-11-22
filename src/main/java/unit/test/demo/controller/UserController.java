package unit.test.demo.controller;

import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhisong.guan
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
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
}
