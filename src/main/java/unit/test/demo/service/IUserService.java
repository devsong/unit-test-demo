package unit.test.demo.service;

import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;

/**
 * @author zhisong.guan
 */
public interface IUserService {
    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    UserInfoDto queryUserInfo(Long userId);

    /**
     * 创建用户
     *
     * @param username
     * @param lockMode
     * @return
     */
    UserCreationDto createUser(String username, String lockMode);

    /**
     * 创建用户
     *
     * @param username
     * @return
     */
    UserCreationDto createUser(String username);

    /**
     * 查询用户
     *
     * @param username
     * @return
     */
    UserInfoDto queryByUsername(String username);
}
