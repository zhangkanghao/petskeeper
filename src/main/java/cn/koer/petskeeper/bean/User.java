package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * 2 * @Author: Koer
 * 3 * @Date: 2020/2/16 12:48
 * 4
 */
@Table("t_user")
public class User extends BasePojo{
    @Id()
    @Column("u_id")
    private int id;
    @Name
    @Column("u_name")
    private String name;
    @Column("u_pwd")
    private String password;
    @Column("u_salt")
    private String salt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
