package unit.test.demo.service.impl;

import org.springframework.cache.annotation.Cacheable;
import unit.test.demo.aspect.DistributionLock;
import unit.test.demo.client.PaymentClient;
import unit.test.demo.common.Constants;
import unit.test.demo.common.LockMode;
import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.entity.UserInfoEntity;
import unit.test.demo.ms.UserInfoMS;
import unit.test.demo.repository.UserInfoRepository;
import unit.test.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * @author zhisong.guan
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserInfoRepository userInfoRepository;
    private final PaymentClient paymentClient;

    @Override
    @Cacheable(value = Constants.CACHE_NAME, key = "#userId")
    public UserInfoDto queryUserInfo(Long userId) {
        Optional<UserInfoEntity> userOptional = userInfoRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException(String.format("can not found user %s", userId));
        }
        log.info("load from db for user {}", userId);
        Long balance = paymentClient.getBalance(userId);
        return UserInfoMS.INSTANCE.toDto(userOptional.get(), balance);
    }

    @Override
    @DistributionLock(prefix = "create-user", key = "#username")
    @Transactional
    public UserCreationDto createUser(String username) {
        return createUser(username, LockMode.NONE.name());
    }

    @Override
    @Transactional
    public UserCreationDto createUser(String username, String lockMode) {
        log.info("current thread {},time {}", Thread.currentThread().getName(), System.currentTimeMillis());
        Optional<UserInfoEntity> existUser;
        LockMode mode = LockMode.getMode(lockMode);
        switch (mode) {
            case W:
                existUser = userInfoRepository.findByUsernameInWLock(username);
                break;
            case R:
                existUser = userInfoRepository.findByUsernameInRLock(username);
                break;
            default:
                existUser = userInfoRepository.findByUsername(username);
                break;
        }

        if (existUser.isPresent()) {
            return UserCreationDto.builder().userId(existUser.get().getUserId()).build();
        }
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setUsername(username);

        UserInfoEntity newUser = userInfoRepository.save(userInfoEntity);
        return UserCreationDto.builder().userId(newUser.getUserId()).build();
    }

    @Override
    public UserInfoDto queryByUsername(String username) {
        UserInfoEntity existUser = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(String.format("can not found user for username %s", username)));
        return UserInfoMS.INSTANCE.toDto(existUser, 0L);
    }
}
