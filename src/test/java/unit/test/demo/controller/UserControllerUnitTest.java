package unit.test.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unit.test.demo.dto.UserCreationDto;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.service.IUserService;
import unit.test.demo.util.ResourceParseUtil;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerUnitTest {
    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalControllerAdvice())
                .build();
    }

    @Test
    void should_success_when_query_user_info() throws Exception {
        UserInfoDto userInfoDto = ResourceParseUtil.parseObject(ResourceParseUtil.BASE_JSON_PATH + "user_info_dto.json", UserInfoDto.class);
        Long userId = userInfoDto.getUserId();
        when(userService.queryUserInfo(userId)).thenReturn(userInfoDto);

        mockMvc.perform(get("/user/info/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userInfoDto.getUserId()))
                .andExpect(jsonPath("$.username").value(userInfoDto.getUsername()))
                .andExpect(jsonPath("$.balance").value(userInfoDto.getBalance()));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, Integer.MAX_VALUE})
    void should_success_when_query_user_info_and_user_id_is_equal_min_or_max_bound(Long userId) throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setUsername("mock username");
        when(userService.queryUserInfo(userId)).thenReturn(userInfoDto);

        MvcResult result = mockMvc.perform(get("/user/info/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        UserInfoDto resultDto = objectMapper.readValue(body, UserInfoDto.class);
        // status validate
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getUserId()).isEqualTo(resultDto.getUserId());

        // behavior validate
        verify(userService, times(1)).queryUserInfo(userId);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 2147483648L})
    void should_fail_when_query_user_info_and_user_id_out_of_range(Long userId) throws Exception {
        mockMvc.perform(get("/user/info/{userId}", userId))
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @NullSource
    void should_fail_when_query_user_info_and_user_id_is_null(Long userId) throws Exception {
        mockMvc.perform(get("/user/info/{userId}", userId))
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    void should_success_when_create_user() throws Exception {
        String username = "username";
        UserCreationDto userCreationDto = ResourceParseUtil.parseObject(ResourceParseUtil.BASE_JSON_PATH + "user_creation_dto.json", UserCreationDto.class);
        when(userService.createUser(username)).thenReturn(userCreationDto);

        MvcResult result = mockMvc.perform(post("/user")
                        .param("username", username)
                )
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        UserCreationDto resultDto = objectMapper.readValue(body, UserCreationDto.class);
        // status validate
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getUserId()).isEqualTo(userCreationDto.getUserId());

        // behavior validate
        verify(userService, times(1)).createUser(username);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void should_fail_when_create_user_and_username_is_invalid(String username) throws Exception {
        mockMvc.perform(post("/user")
                        .param("username", username)
                )
                .andExpect(status().is(BAD_REQUEST.value()));
    }

    @Test
    void should_success_when_query_by_username() throws Exception {
        UserInfoDto userInfoDto = ResourceParseUtil.parseObject(ResourceParseUtil.BASE_JSON_PATH + "user_info_dto.json", UserInfoDto.class);
        String username = userInfoDto.getUsername();
        when(userService.queryByUsername(anyString())).thenReturn(userInfoDto);

        MvcResult result = mockMvc.perform(get("/user/query-by-username")
                        .param("username", username)
                )
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        UserInfoDto resultDto = objectMapper.readValue(body, UserInfoDto.class);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getUserId()).isEqualTo(userInfoDto.getUserId());
        assertThat(resultDto.getUsername()).isEqualTo(userInfoDto.getUsername());
        assertThat(resultDto.getBalance()).isEqualTo(userInfoDto.getBalance());

        verify(userService, times(1)).queryByUsername(username);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void should_fail_when_query_by_username_and_username_is_invalid(String username) throws Exception {
        mockMvc.perform(get("/user/query-by-username")
                        .param("username", username)
                )
                .andExpect(status().is(BAD_REQUEST.value()));
    }
}
