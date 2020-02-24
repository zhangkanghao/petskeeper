package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

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

    @Column("birth")
    private Date birthTime;

    @Column("adoption")
    private Date adoptionTime;

    @Column()
    @JsonField(ignore = true)
    private Byte[] pic;

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

    public Byte[] getPic() {
        return pic;
    }

    public void setPic(Byte[] pic) {
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
