package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

/**
 * @Author Koer
 * @Date 2020/2/21 22:50
 */
@Table("t_pet")
public class Pets extends BasePojo {

    @Id
    @Column
    private int id;

    @Column("uid")
    private int userId;

    @Column("pet_name")
    private String petName;

    @Column()
    @JsonField(ignore = true)
    private Byte[] pic;

    @Column("dt")
    private String description;

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
}
