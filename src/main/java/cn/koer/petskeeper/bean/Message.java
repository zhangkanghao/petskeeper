package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @Author Koer
 * @Date 2020/2/21 23:06
 */
@Table("t_message")
public class Message extends BasePojo {

    @Id
    @Column
    private int id;

    @Column("from_id")
    private int from;

    @Column("to_id")
    private int to;

    @Column
    private String subject;

    @Column
    private String content;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
