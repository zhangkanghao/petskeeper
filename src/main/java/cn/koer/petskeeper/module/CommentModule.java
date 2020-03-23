package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.Comment;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.Chain;
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

import javax.servlet.http.HttpServletRequest;
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
@Filters(@By(type = CheckTokenFilter.class))
public class CommentModule extends BaseModule{

    @At
    public Object add(@Param("..")Comment comment, HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        comment.setFrom(userId);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        if(comment.getType()){
            dao.update(Comment.class, Chain.makeSpecial("comment","+1"), Cnd.where("id","=",comment.getTo()));
        }else{
            dao.update(Article.class, Chain.makeSpecial("comment","+1"), Cnd.where("id","=",comment.getTo()));
        }
        dao.insert(comment);
        return new NutMap().setv("ok",true).setv("data",comment);
    }

    @At
    public Object get(@Param("commentId")int id){
        return dao.fetch(Comment.class,id);
    }

    @At
    public Object edit(@Param("..")Comment comment,HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        if(comment.getFrom()!=userId){
            return new NutMap().setv("ok",false).setv("msg","非法操作");
        }
        comment.setUpdateTime(new Date());
        dao.update(comment);
        return new NutMap().setv("ok",true).setv("data",comment);
    }

    @At
    public Object remove(@Param("..")Comment comment,HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        if(comment.getFrom()!=userId){
            return new NutMap().setv("ok",false).setv("msg","非法操作");
        }
        dao.delete(comment);
        return new NutMap().setv("ok",true);
    }

    private SqlCallback getCallbackA(){
        return new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<JSONObject> list= new ArrayList<>();
                while(rs.next()){
                    JSONObject obj=new JSONObject();
                    obj.put("id",rs.getInt("id"));
                    obj.put("type",rs.getBoolean("type"));
                    obj.put("from",rs.getInt("from_id"));
                    obj.put("to",rs.getInt("to_id"));
                    obj.put("content",rs.getString("content"));
                    obj.put("praise",rs.getInt("praise"));
                    obj.put("comment",rs.getInt("comment"));
                    obj.put("nickname",rs.getString("nickname"));
                    obj.put("targetId",rs.getBoolean("targetId"));
                    obj.put("date",rs.getDate("ut"));
                    list.add(obj);
                }
                return list;
            }
        };
    }

    @At("/commentOfA")
    public Object queryByArticle(@Param("articleId")int aid, @Param("..")Pager pager){
        NutMap re=new NutMap();
        String s="SELECT id,type,from_id,to_id,content,t.praise,comment,nickname,a.targetId,t.ut from t_comment t " +
                "LEFT JOIN t_user_profile u ON t.from_id=u.uid " +
                "LEFT JOIN (SELECT targetId from t_praise WHERE userId = 4 AND type=1) a on t.id=a.targetId " +
                "where type=0 AND to_id =10 ORDER BY t.praise DESC";
        Sql sql= Sqls.create(s);
        sql.setParam("article_id",aid);
        pager.setRecordCount((int) Daos.queryCount(dao,s));
        sql.setPager(pager);
        sql.setCallback(getCallbackA());
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        List<Record> comments = sql.getList(Record.class);
        return re.setv("comments",comments).setv("pager",pager);
    }

    @At("/commentOfC")
    public Object queryByComment(@Param("commentId")int cid, @Param("..")Pager pager){
        NutMap re=new NutMap();
        String s="SELECT id,from_id,to_id,content,nickname from t_comment t LEFT JOIN t_user_profile u ON t.from_id=u.uid where type=1 AND to_id=@commentId";
        Sql sql= Sqls.create(s);
        sql.setParam("commentId",cid);
        pager.setRecordCount((int) Daos.queryCount(dao,s));
        sql.setPager(pager);
        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection connection, ResultSet rs, Sql sql) throws SQLException {
                List<JSONObject> list= new ArrayList<>();
                while(rs.next()){
                    JSONObject obj=new JSONObject();
                    obj.put("id",rs.getInt("id"));
                    obj.put("from",rs.getInt("from_id"));
                    obj.put("to",rs.getInt("to_id"));
                    obj.put("content",rs.getString("content"));
                    obj.put("nickname",rs.getString("nickname"));
                    list.add(obj);
                }
                return list;
            }
        });
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        List<Record> comments = sql.getList(Record.class);
        return re.setv("replys",comments).setv("pager",pager);

    }


}
