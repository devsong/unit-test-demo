package unit.test.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import unit.test.demo.entity.UserInfoEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author zhisong.guan
 */
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
    Optional<UserInfoEntity> findByUsername(String username);

    @Query(nativeQuery = true, value = "select * from user_info where username=:username for update")
    Optional<UserInfoEntity> findByUsernameInWLock(String username);

    @Query(nativeQuery = true, value = "select * from user_info where username=:username lock in share mode")
    Optional<UserInfoEntity> findByUsernameInRLock(String username);

    List<UserInfoEntity> findListByUsername(String username);
}
