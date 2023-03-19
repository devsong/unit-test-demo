package unit.test.demo.testdouble;

import org.junit.jupiter.api.Test;
import unit.test.demo.dto.UserInfoDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TestDoubleTest {

    @Test
    void test_for_dummy_object() {
        // dummy object
        UserInfoDto dummy = mock(UserInfoDto.class);
        List<UserInfoDto> userInfoDtos = new ArrayList<>();
        userInfoDtos.add(dummy);

        assertThat(userInfoDtos.size()).isEqualTo(1);
    }

    @Test
    void test_for_test_stub() {
        List<Integer> list = mock(List.class);
        when(list.get(0)).thenReturn(1);

        Integer o = list.get(0);
        assertThat(o).isEqualTo(1);
    }

    @Test
    void test_for_test_spy() {
        List<Integer> list = spy(ArrayList.class);

        // incorrect stub,will be throw IndexOutOfBoundsException because list is a real list(ArrayList) implementation
        // when(list.get(0)).thenReturn(2);

        list.add(1);
        // list.get(0);

        when(list.get(0)).thenReturn(2);
        // status verify
        assertThat(list.get(0)).isEqualTo(2);
        assertThat(list.size()).isEqualTo(1);

        // behavier verify
        verify(list, times(1)).get(0);
    }

    @Test
    void test_for_mock_object() {
        List<Integer> list = mock(List.class);
        list.add(1);

        // behavier verify
        verify(list, times(1)).add(1);
    }
}
