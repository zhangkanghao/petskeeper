package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

/**
 * @Author: Koer
 * @Date: 2020/2/18 12:53
 */
@Table("t_user_profile")
public class UserProfile extends BasePojo{

    /**关联的用户id*/
    @Id(auto=false)
    @Column("uid")
    protected int userId;

    /**用户昵称*/
    @Column
    protected String nickname;

    /**用户邮箱*/
    @Column
    protected String email;

    /**邮箱是否已经验证过*/
    @Column("email_checked")
    protected boolean emailChecked;

    /**头像的byte数据*/
    @Column
    @JsonField(ignore=true)
    protected byte[] avatar;

    /**性别*/
    @Column
    protected String gender;

    /**自我介绍*/
    @Column("dt")
    protected String description;

    @Column("loc")
    protected String location;

    @Column
    protected int praise;

    @Column
    protected int follower;

    @Column
    protected int following;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailChecked() {
        return emailChecked;
    }

    public void setEmailChecked(boolean emailChecked) {
        this.emailChecked = emailChecked;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
}
