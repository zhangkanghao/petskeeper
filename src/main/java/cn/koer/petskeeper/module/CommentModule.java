package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.Comment;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Koer
 * @Date 2020/3/1 14:43
 */
@IocBean
@At("/comment")
@Filters(@By(type = CheckSession.class,args = {"ident","/"}))
public class CommentModule extends BaseModule{

    @At
    public Object post(@Param("..")Comment comment, @Attr(scope = Scope.SESSION,value = "ident")int me){
        if(comment.getType()){
            Comment target=dao.fetch(Comment.class,comment.getRoot());
            if(target==null||target.getStatus()==0){
                return new NutMap().setv("ok",false).setv("msg","评论对象不存在");
            }
        }else{
            Article article=dao.fetch(Article.class,comment.getRoot());
            if(article==null||"私密".equals(article.getReadType())||article.getStatus()==0){
                return new NutMap().setv("ok",false).setv("msg","文章不存在或禁止评论");
            }
        }
        comment.setFrom(me);
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        dao.insert(comment);
        return new NutMap().setv("ok",true).setv("data",comment);
    }

    @At
    public Object get(@Param("commentId")int id){
        return dao.fetch(Comment.class,id);
    }

    @At
    public Object edit(@Param("..")Comment comment,@Attr(scope = Scope.SESSION,value = "ident")int me){
        if(comment.getFrom()!=me){
            return new NutMap().setv("ok",false).setv("msg","非法操作");
        }
        comment.setUpdateTime(new Date());
        dao.update(comment);
        return new NutMap().setv("ok",true).setv("data",comment);
    }

    @At
    public Object remove(@Param("..")Comment comment,@Attr(scope = Scope.SESSION,value = "ident")int me){
        if(comment.getFrom()!=me){
            return new NutMap().setv("ok",false).setv("msg","非法操作");
        }
        comment.setStatus(0);
        comment.setUpdateTime(new Date());
        dao.update(comment);
        return new NutMap().setv("ok",true);
    }

    private SqlCallback getCallback(){
        return new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<JSONObject> list= new ArrayList<>();
                while(rs.next()){
                    JSONObject obj=new JSONObject();
                    obj.put("id",rs.getInt("id"));
                    obj.put("type",rs.getInt("type"));
                    obj.put("root",rs.getInt("root"));
                    obj.put("from",rs.getInt("from_id"));
                    obj.put("to",rs.getInt("tp_id"));
                    obj.put("content",rs.getString("content"));
                    obj.put("praise",rs.getInt("praise"));
                    obj.put("comment",rs.getInt("comment"));
                    obj.put("nickname",rs.getString("nickname"));
                    list.add(obj);
                }
                return list;
            }
        };
    }

    @At("/commentOfA")
    public Object queryByArticle(@Param("articleId")int aid, @Param("..")Pager pager){
        String s="SELECT id,type,root,from_id,to_id,content,t.praise,comment,nickname from t_comment t LEFT JOIN t_user_profile u ON t.from_id=u.uid where type=0 AND root=@article_id AND STATUS=1";
        Sql sql= Sqls.create(s);
        sql.setParam("article_id",aid);
        pager.setRecordCount((int) Daos.queryCount(dao,s));
        sql.setPager(pager);
        sql.setCallback(getCallback());
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        List<Record> comments = sql.getList(Record.class);
        return JSON.toJSON(comments);
    }

    @At("/commentOfC")
    public Object queryByComment(@Param("commentId")int cid, @Param("..")Pager pager){
        String s="SELECT id,type,root,from_id,to_id,content,t.praise,comment,nickname from t_comment t LEFT JOIN t_user_profile u ON t.from_id=u.uid where type=1 AND root=@comment_id AND STATUS=1";
        Sql sql= Sqls.create(s);
        sql.setParam("comment_id",cid);
        pager.setRecordCount((int) Daos.queryCount(dao,s));
        sql.setPager(pager);
        sql.setCallback(getCallback());
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        List<Record> comments = sql.getList(Record.class);
        return JSON.toJSON(comments);

    }


}
