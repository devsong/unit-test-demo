package unit.test.demo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * date:  2023/8/30
 * author:guanzhisong
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BaseEntity {

    /**
     * 创建者
     */
    @Column(columnDefinition = "varchar(64) default null comment '创建人'")
    @CreatedBy
    protected String createBy;

    /**
     * 创建时间
     */
    @Column(columnDefinition = "datetime not null default current_timestamp  comment '创建时间'")
    @CreatedDate
    protected Date createdAt;

    @Column(columnDefinition = "varchar(64) default null comment '更新人'")
    @LastModifiedBy
    protected String updateBy;

    /**
     * 更新时间
     */
    @Column(columnDefinition = "timestamp not null default current_timestamp  comment '更新时间'")
    @LastModifiedDate
    protected Date updatedAt;
}
