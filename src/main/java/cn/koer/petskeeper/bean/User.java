package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.*;

/**
 * 2 * @Author: Koer
 * 3 * @Date: 2020/2/16 12:48
 * 4
 */
@Table("t_user")
public class User extends BasePojo{
    @Id
    private int id;
    @Name
    @Column
    private String name;
    @Column
    private String password;
    @Column
    private String salt;
    @Column
    private String description;
    /**与userprofile一对一关联*/
    @One(target=UserProfile.class, field="id", key="userId")
    protected UserProfile profile;



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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }
}
