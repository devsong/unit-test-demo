package unit.test.demo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MvcResult;
import unit.test.demo.IntegrationTestBase;
import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.entity.UserInfoEntity;
import unit.test.demo.util.ResourceParseUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends IntegrationTestBase {

    @Test
    void should_success_when_query_user_info() throws Exception {
        Long balance = 123L;
        // setUp(prepareData)
        UserInfoEntity userInfoEntity = prepareUserInfoEntityData();
        when(paymentClient.getBalance(userInfoEntity.getUserId())).thenReturn(balance);

        // exercise and verify
        mockMvc.perform(get("/user/info/{userId}", userInfoEntity.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userInfoEntity.getUserId()))
                .andExpect(jsonPath("$.username").value(userInfoEntity.getUsername()))
                .andExpect(jsonPath("$.balance").value(balance));
        // behavior verify
        verify(paymentClient).getBalance(userInfoEntity.getUserId());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 2147483648L})
    void should_fail_when_query_user_info_and_user_id_is_out_of_range(Long userId) throws Exception {
        mockMvc.perform(get("/user/info/{userId}", userId))
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @NullSource
    void should_fail_when_query_user_info_and_userId_is_null(Long userId) throws Exception {
        mockMvc.perform(get("/user/info/{userId}", userId))
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    void should_success_when_query_by_username_and_username_is_exist() throws Exception {
        UserInfoEntity userInfoEntity = prepareUserInfoEntityData();
        MvcResult result = mockMvc.perform(get("/user/query-by-username")
                        .param("username", userInfoEntity.getUsername())
                )
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(body).isNotNull();
        UserInfoDto userInfoDto = OBJECT_MAPPER.readValue(body, UserInfoDto.class);
        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getUserId()).isEqualTo(userInfoEntity.getUserId());
        assertThat(userInfoDto.getUsername()).isEqualTo(userInfoEntity.getUsername());
        assertThat(userInfoDto.getBalance()).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "notExistUser"})
    void should_fail_when_query_by_username_and_username_is_blank_or_username_is_not_exist(String username) throws Exception {
        mockMvc.perform(get("/user/query-by-username")
                        .param("username", username)
                )
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    @Test
    void should_success_when_create_user_and_username_is_not_null() throws Exception {
        String username = "tester";
        MvcResult result = mockMvc.perform(post("/user")
                        .param("username", username)
                )
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(body).isNotNull();
        UserCreationDto userCreationDto = OBJECT_MAPPER.readValue(body, UserCreationDto.class);
        assertThat(userCreationDto).isNotNull();
        assertThat(userCreationDto.getUserId()).isGreaterThan(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void should_fail_when_create_user_and_username_is_blank(String username) throws Exception {
        mockMvc.perform(post("/user")
                        .param("username", username)
                )
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @NullSource
    void should_fail_when_create_user_and_username_is_null(String username) throws Exception {
        mockMvc.perform(post("/user")
                        .param("username", username)
                )
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    UserInfoEntity prepareUserInfoEntityData() throws IOException {
        UserInfoEntity userInfoEntity = ResourceParseUtil.parseObject(BASE_JSON_PATH + "user_info_entity.json", UserInfoEntity.class);
        userInfoRepository.save(userInfoEntity);
        return userInfoEntity;
    }
}
