package cn.koer.petskeeper.bean;

/**
 * @Author Koer
 * @Date 2020/2/20 20:16
 */
import java.util.List;

import org.nutz.dao.entity.annotation.*;

@Table("t_role")
public class Role extends BasePojo {

    @Id
    protected long id;
    @Name
    protected String name;
    @Column("al")
    protected String alias;
    @Column("dt")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String description;
    @ManyMany(from="role_id", relation="t_role_permission", target=Permission.class, to="permission_id")
    protected List<Permission> permissions;
}