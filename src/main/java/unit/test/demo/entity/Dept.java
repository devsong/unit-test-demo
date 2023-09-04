package unit.test.demo.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "dept")
public class Dept extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Comment("部门名")
    private String deptName;

    @ManyToOne
    private Dept parent;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "parent")
    private Set<Dept> children;

    @ManyToMany
    private Set<Role> roles;

    @OneToMany(mappedBy = "dept")
    private Set<Member> members;

    @ManyToOne
    private Corp corp;
}
