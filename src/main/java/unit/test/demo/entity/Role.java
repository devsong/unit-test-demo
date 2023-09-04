package unit.test.demo.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "role")
public class Role extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Comment("角色名")
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private List<Privilege> privileges;

    @ManyToMany(mappedBy = "roles")
    private List<Member> members;

    @ManyToMany(mappedBy = "roles")
    private List<Dept> depts;

}
