package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;

/**
 * @Author Koer
 * @Date 2020/2/21 19:50
 */
@Table("t_comment")
public class Comment extends BasePojo{

    @Id
    @Column
    private int id;

    /**默认false为对文章评论，true是回复*/
    @Column
    @Default("false")
    private boolean type;

    /**回复的文章id或者是评论id*/
    @Column
    private int root;

    @Column("from_id")
    private int from;

    /**评论对象用户的id*/
    @Column("to_id")
    private int to;

    @Column
    private String content;

    @Column
    @Default("0")
    private int praise;

    @Column
    @Default("0")
    private int comment;

    @Column
    private int status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
