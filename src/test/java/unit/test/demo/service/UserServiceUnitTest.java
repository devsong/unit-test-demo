package unit.test.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unit.test.demo.client.PaymentClient;
import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.entity.UserInfoEntity;
import unit.test.demo.repository.UserInfoRepository;
import unit.test.demo.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {
    @Mock
    private UserInfoRepository userInfoRepository;
    // private UserInfoRepository userInfoRepository = mock(UserInfoRepository.class);

    @Mock
    private PaymentClient paymentClient;
    // private PaymentClient paymentClient = mock(PaymentClient.class);

    @InjectMocks
    private UserServiceImpl userService;
    // private  UserServiceImpl userService = new UserServiceImpl(userInfoRepository, paymentClient);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_success_when_query_user_info() {
        Long userId = 1L, balance = 2L;
        String username = userId + "test";
        UserInfoEntity userInfoEntity = new UserInfoEntity(userId, username);
        when(userInfoRepository.findById(userId)).thenReturn(Optional.of(userInfoEntity));
        when(paymentClient.getBalance(userId)).thenReturn(balance);

        UserInfoDto userInfo = userService.queryUserInfo(userId);
        // status verify
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getUserId()).isEqualTo(userId);
        assertThat(userInfo.getBalance()).isEqualTo(balance);

        // behavior verify
        verify(paymentClient).getBalance(userId);
    }

    @Test
    void should_throw_illegal_argument_exception_when_user_id_not_exist() {
        Long notExistUserId = 1L;
        when(userInfoRepository.findById(notExistUserId)).thenReturn(Optional.empty());
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.queryUserInfo(notExistUserId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sfgfdfgfd", "df dfds"})
    void should_success_when_create_user(String username) {
        UserInfoEntity entity = new UserInfoEntity();
        entity.setUsername(username);
        entity.setUserId(1L);
        when(userInfoRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userInfoRepository.save(any())).thenReturn(entity);

        UserCreationDto user = userService.createUser(username);
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(entity.getUserId());

        verify(userInfoRepository, times(1)).findByUsername(username);
        verify(userInfoRepository, times(1)).save(any());
    }

    @Test
    void should_success_when_query_by_username() {
        Long userId = 1L;
        String username = "testuser";
        Long defaultBalance = 0L;
        UserInfoEntity entity = new UserInfoEntity();
        entity.setUserId(userId);
        entity.setUsername(username);
        when(userInfoRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        UserInfoDto result = userService.queryByUsername(username);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getBalance()).isEqualTo(defaultBalance);

        verify(userInfoRepository, times(1)).findByUsername(username);
    }

    @Test
    void should_fail_when_query_by_username_and_username_is_not_exist() {
        String username = "notExistUser";
        when(userInfoRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.queryByUsername(username));
        verify(userInfoRepository, times(1)).findByUsername(username);
    }

    @Test
    void should_ceil() {
        System.out.println(500 / 3);
    }



    @ParameterizedTest
    @ValueSource(strings = {"","123.234.12.4:3424", "123.234.12.4:3424,", "123"})
    void should_split_first_ip(String ipList) {
        System.out.println(ipList.split(",")[0].split(":")[0]);
    }
}
