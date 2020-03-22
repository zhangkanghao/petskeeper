package cn.koer.petskeeper.bean;

import cn.koer.petskeeper.filter.CheckTokenFilter;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;

import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/21 22:50
 */
@Table("t_pet")
public class Pet extends BasePojo {

    @Id
    @Column
    private int id;

    @Column("uid")
    private int userId;

    @Column("pet_name")
    private String petName;

    @Column
    private String gender;

    @Column
    private String type;

    @Column("birth")
    private Date birthTime;

    @Column("adoption")
    private Date adoptionTime;

    @Column
    private String sterilized;

    @Column()
    @JsonField(ignore = true)
    private byte[] pic;

    @Column("dt")
    private String description;

    @Column
    @Default("0")
    private int praise;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(Date birthTime) {
        this.birthTime = birthTime;
    }

    public Date getAdoptionTime() {
        return adoptionTime;
    }

    public void setAdoptionTime(Date adoptionTime) {
        this.adoptionTime = adoptionTime;
    }

    public String isSterilized() {
        return sterilized;
    }

    public void setSterilized(String sterilized) {
        this.sterilized = sterilized;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }
}
