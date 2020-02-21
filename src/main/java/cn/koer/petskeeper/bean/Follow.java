package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @Author Koer
 * @Date 2020/2/21 23:01
 */
@Table("t_follow")
public class Follow extends BasePojo {

    @Id
    @Column
    private int id;

    @Column("from_id")
    private int from;

    @Column("to_id")
    private int to;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
