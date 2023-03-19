package unit.test.demo.ms;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import unit.test.demo.dto.UserInfoDto;
import unit.test.demo.entity.UserInfoEntity;

/**
 * @author zhisong.guan
 */
@Mapper
public interface UserInfoMS {
    UserInfoMS INSTANCE = Mappers.getMapper(UserInfoMS.class);

    UserInfoEntity fromDto(UserInfoDto userInfoDto);

    UserInfoDto toDto(UserInfoEntity userInfoEntity, Long balance);
}
