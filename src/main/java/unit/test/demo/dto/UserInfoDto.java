package unit.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhisong.guan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto implements Serializable {
    private Long userId;
    private String username;
    private Long balance;
}
