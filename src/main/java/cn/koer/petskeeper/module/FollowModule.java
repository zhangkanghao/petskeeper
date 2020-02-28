package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Follow;
import cn.koer.petskeeper.bean.UserProfile;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
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

import com.alibaba.fastjson.JSONObject;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;


/**
 * @Author Koer
 * @Date 2020/2/26 19:18
 */
@At("/follow")
@IocBean
@Filters(@By(type = CheckSession.class, args = {"ident", "/"}))
public class FollowModule extends BaseModule {

    private SqlCallback getCallback(){
        return new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<JSONObject> list= new ArrayList<>();
                while(rs.next()){
                    JSONObject obj=new JSONObject();
                    obj.put("userId",rs.getInt("uid"));
                    obj.put("nickname",rs.getString("nickname"));
                    obj.put("avatar",rs.getBlob("avatar"));
                    obj.put("description",rs.getString("dt"));
                    obj.put("praise",rs.getInt("praise"));
                    obj.put("follower",rs.getInt("follower"));
                    obj.put("id",rs.getInt("id"));
                    list.add(obj);
                }
                return list;
            }
        };
    }

    @At("/follower")
    public Object getFollower(@Param("userId") int userId,@Attr(scope = Scope.SESSION,value = "ident")int me) {
        Sql sql= Sqls.create("select uid,nickname,avatar,dt,praise,follower,id from t_user_profile u left join  (select * from t_follow where from_id=@ident_id) b on u.uid=b.to_id " +
                "where uid in (select from_id from t_follow where to_id =@to_id)");
        sql.setParam("ident_id",me);
        sql.setParam("to_id",userId);
        sql.setCallback(getCallback());
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        List<Record> users = sql.getList(Record.class);
        return new NutMap().setv("ok",true).setv("data",users);

    }

    @At("/following")
    public Object getFollowing(@Param("userId") int userId,@Attr(scope = Scope.SESSION,value = "ident")int me) {
        Sql sql= Sqls.create("select uid,nickname,avatar,dt,praise,follower,id from t_user_profile u left join  (select * from t_follow where from_id=@ident_id) b on u.uid=b.to_id " +
                "where uid in (select to_id from t_follow where from_id =@from_id)");
        sql.setParam("ident_id",me);
        sql.setParam("from_id",userId);
        sql.setCallback(getCallback());
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        List<Record> users = sql.getList(Record.class);
        return new NutMap().setv("ok",true).setv("data",users);
    }

    @At
    public Object add(@Param("userId") final int userId, @Attr(scope = Scope.SESSION, value = "ident") final int me) {
        final Follow follow=new Follow();
        follow.setFrom(me);
        follow.setTo(userId);
        follow.setCreateTime(new Date());
        follow.setUpdateTime(new Date());
        Trans.exec(new Atom() {
            @Override
            public void run() {
                dao.insert(follow);
                dao.update(UserProfile.class, Chain.makeSpecial("following","+1"),Cnd.where("uid","=",me));
                dao.update(UserProfile.class, Chain.makeSpecial("follower","+1"),Cnd.where("uid","=",userId));
            }
        });
        return new NutMap().setv("ok", true).setv("data",follow);
    }

    @At
    public Object remove(@Param("followId")int followId, @Param("userId") final int userId, @Attr(scope = Scope.SESSION, value = "ident") final int me) {
        NutMap re = new NutMap();
        final Follow follow = dao.fetch(Follow.class, followId);
        if (follow == null) {
            return re.setv("ok", false).setv("msg", "未关注用户");
        } else if (follow.getFrom() != me) {
            return re.setv("ok", false).setv("msg", "非法操作");
        }
        Trans.exec(new Atom() {
            @Override
            public void run() {
                dao.delete(follow);
                dao.update(UserProfile.class, Chain.makeSpecial("following","-1"),Cnd.where("uid","=",me));
                dao.update(UserProfile.class, Chain.makeSpecial("follower","-1"),Cnd.where("uid","=",userId));
            }
        });
        return new NutMap().setv("ok", true);
    }

    @At
    public Object recommend(@Param("..") Pager pager,@Attr(scope = Scope.SESSION,value = "ident")int me) {
        Sql sql=Sqls.create("select uid,nickname,avatar,dt,praise,follower from t_user_profile where (follower >@follower or praise>@praise) and uid not in (SELECT to_id from t_follow where from_id = @ident_id)");
        sql.setParam("ident_id",me);
        sql.setParam("follower",20);
        sql.setParam("praise",100);
        pager.setRecordCount((int)Daos.queryCount(dao,"select uid,nickname,avatar,dt,praise,follower from t_user_profile where (follower >@follower or praise>@praise) and uid not in (SELECT to_id from t_follow where from_id = @ident_id)"));
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(UserProfile.class));
        dao.execute(sql);
        List<UserProfile> users = sql.getList(UserProfile.class);
        return new NutMap().setv("ok",true).setv("data",users);
    }
}
