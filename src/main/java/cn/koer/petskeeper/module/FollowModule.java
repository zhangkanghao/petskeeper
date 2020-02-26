package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Follow;
import cn.koer.petskeeper.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Param;

import java.util.Date;


/**
 * @Author Koer
 * @Date 2020/2/26 19:18
 */
@At("/follow")
@IocBean
public class FollowModule extends BaseModule{

    @At("/follower")
    public Object getFollower(@Param("userId")int userId){
        /**select * from t_user where exists(select from_id from t_follow where to_id ='%d')*/
        QueryResult rs=new QueryResult();
        Condition cnd=Cnd.wrap("exists (select from_id from t_follow where to_id ='%d') ", userId);
        rs.setList(dao.query(User.class, cnd,null));
        rs.setPager(null);
        return rs;
    }

    @At("/following")
    public Object getFollowing(@Param("userId")int userId){
        QueryResult rs=new QueryResult();
        Condition cnd=Cnd.wrap("exists(select to_id from t_follow where from_id='%d')",userId);
        rs.setList(dao.query(User.class,cnd,null));
        rs.setPager(null);
        return rs;
    }

    @At
    public Object add(@Param("userId")int userId, @Attr(scope = Scope.SESSION,value = "ident")int me){
        Follow follow=new Follow();
        follow.setFrom(me);
        follow.setTo(userId);
        follow.setCreateTime(new Date());
        follow.setUpdateTime(new Date());
        dao.insert(follow);
        return new NutMap().setv("ok",true);
    }

    @At
    public Object remove(@Param("followId")int followId, @Attr(scope = Scope.SESSION,value = "ident")int me){
        NutMap re=new NutMap();
        Follow follow=dao.fetch(Follow.class,followId);
        if(follow==null){
            return re.setv("ok",false).setv("msg","未关注用户");
        }else if(follow.getFrom()!=me){
            return re.setv("ok",false).setv("msg","非法操作");
        }
        dao.delete(follow);
        return new NutMap().setv("ok",true);
    }

    @At
    public Object recommend(@Param("..")Pager pager){
        QueryResult qr=new QueryResult();
        int num=20;
        qr.setList(dao.query(User.class,Cnd.wrap("exists (select 1 from t_user_profile where follow>'%d')",num),pager));
        pager.setRecordCount(dao.count(User.class,Cnd.wrap("exists (select 1 from t_user_profile where follow>'%d')",num)));
        qr.setPager(pager);
        return qr;
    }
}
