package cn.koer.petskeeper.bean;

import org.nutz.dao.entity.annotation.*;

/**
 * @Author Koer
 * @Date 2020/2/21 19:19
 */
@Table("t_article")
public class Article extends BasePojo{

    @Id
    @Column
    private int id;

    /**发帖人id*/
    @Column("uid")
    private int userId;

    @Column
    private String subject;

    /**文章/动态/视频*/
    @Column
    private String type;

    /**私密/公开*/
    @Column
    private boolean readType;

    @Column
    @ColDefine(type = ColType.TEXT)
    private String content;

    /**浏览数*/
    @Column
    @Default("0")
    private int visit;

    /**点赞数*/
    @Column
    @Default("0")
    private int praise;

    /**评论数*/
    @Column
    @Default("0")
    private int comment;

    /**匿名*/
    @Column
    private boolean annoymous;

    /**状态：草稿、发布*/
    @Column
    private boolean status;


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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReadType() {
        return readType;
    }

    public void setReadType(boolean readType) {
        this.readType = readType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
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

    public boolean isAnnoymous() {
        return annoymous;
    }

    public void setAnnoymous(boolean annoymous) {
        this.annoymous = annoymous;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
