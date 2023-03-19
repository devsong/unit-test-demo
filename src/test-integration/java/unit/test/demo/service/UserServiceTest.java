package unit.test.demo.service;

import org.junit.jupiter.api.Test;
import unit.test.demo.IntegrationTestBase;
import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.entity.UserInfoEntity;
import unit.test.demo.util.ResourceParseUtil;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UserServiceTest extends IntegrationTestBase {
//    @Mock
//    PaymentClient paymentClient;
//
//    @BeforeEach
//    void beforeEach() {
//        ReflectionTestUtils.setField(userService, "paymentClient", paymentClient);
//    }

    @Test
    void should_success_when_query_user_info() throws IOException {
        // setUp or prepareData
        UserInfoEntity userInfoEntity = prepareUserInfoEntityData();

        // exercise
        UserInfoDto userInfo = userService.queryUserInfo(userInfoEntity.getUserId());

        // verify
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getUserId()).isEqualTo(userInfoEntity.getUserId());
        assertThat(userInfo.getUsername()).isEqualTo(userInfoEntity.getUsername());
        assertThat(userInfo.getBalance()).isNotNull();
    }

    @Test
    void should_fail_when_query_user_info_and_user_id_not_exist() throws Exception {
        Long notExistUserId = 123L;
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.queryUserInfo(notExistUserId));
    }

    @Test
    void should_success_when_create_user_and_username_not_exist() {
        String notExistUsername = "notexistuser";
        UserCreationDto user = userService.createUser(notExistUsername);
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isGreaterThan(0);
    }

    @Test
    void should_success_when_create_user_and_username_exist() throws IOException {
        UserInfoEntity userInfoEntity = prepareUserInfoEntityData();
        UserCreationDto user = userService.createUser(userInfoEntity.getUsername());
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(userInfoEntity.getUserId());
    }

    @Test
    void should_success_when_query_by_username_and_username_exist() throws Exception {
        UserInfoEntity userInfoEntity = prepareUserInfoEntityData();
        UserInfoDto userInfoDto = userService.queryByUsername(userInfoEntity.getUsername());
        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getUserId()).isEqualTo(userInfoEntity.getUserId());
    }

    @Test
    void should_fail_when_query_by_username_and_username_not_exist() throws Exception {
        String usernameNotExist = "usernameNotExist";
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.queryByUsername(usernameNotExist));
    }

    UserInfoEntity prepareUserInfoEntityData() throws IOException {
        UserInfoEntity userInfoEntity = ResourceParseUtil.parseObject(BASE_JSON_PATH + "user_info_entity.json", UserInfoEntity.class);
        userInfoRepository.save(userInfoEntity);
        return userInfoEntity;
    }
}
