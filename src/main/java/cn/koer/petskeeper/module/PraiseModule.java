package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.Comment;
import cn.koer.petskeeper.bean.Pet;
import cn.koer.petskeeper.bean.Praise;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/29 16:14
 */
@IocBean
@Filters(@By(type = CheckTokenFilter.class))
@At("/praise")
public class PraiseModule extends BaseModule {

    @At
    public Object get(@Param("type")int type, @Param("targetId") final int targetId, HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        Praise praise=dao.fetch(Praise.class,Cnd.where("type","=",type).and("userId","=",userId).and("targetId","=",targetId));
        if(praise==null){
            return new NutMap("ok",false);
        }
        return new NutMap().setv("ok",true);
    }

    @At
    public Object add(@Param("type")int type, @Param("targetId") final int targetId, HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        Praise praise1=dao.fetch(Praise.class,Cnd.where("type","=",type).and("userId","=",userId).and("targetId","=",targetId));
        if(praise1!=null){
            return new NutMap().setv("ok",false).setv("msg","已经点过赞了");
        }
        final Praise praise=new Praise();
        praise.setUserId(userId);
        praise.setType(type);
        praise.setTargetId(targetId);
        praise.setCreateTime(new Date());
        praise.setUpdateTime(new Date());
        if(type==0){
            //给文章点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Article.class, Chain.makeSpecial("praise","+1"), Cnd.where("id","=",targetId));
                    dao.insert(praise);
                }
            });
        } else if (type == 1) {
            //给评论点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Comment.class,Chain.makeSpecial("praise","+1"),Cnd.where("id","=",targetId));
                    dao.insert(praise);
                }
            });
        }else if(praise.getType()==2){
            //给宠物点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Pet.class,Chain.makeSpecial("praise","+1"),Cnd.where("id","=",targetId));
                    dao.insert(praise);
                }
            });
        }else{
            return new NutMap().setv("ok",false).setv("msg","未知错误");
        }
        return new NutMap().setv("ok",true).setv("data",praise);
    }

    @At
    public Object remove(@Param("type")int type, @Param("targetId") final int targetId, HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        final Praise praise=dao.fetch(Praise.class,Cnd.where("type","=",type).and("userId","=",userId).and("targetId","=",targetId));
        if (praise==null){
            return new NutMap().setv("ok",false).setv("msg","还没有点赞呢");
        }
        if(type==0){
            //给文章点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Article.class, Chain.makeSpecial("praise","-1"), Cnd.where("id","=",targetId));
                    dao.delete(praise);
                }
            });
        } else if (type == 1) {
            //给评论点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Comment.class,Chain.makeSpecial("praise","-1"),Cnd.where("id","=",targetId));
                    dao.delete(praise);
                }
            });
        }else if(praise.getType()==2){
            //给宠物点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Pet.class,Chain.makeSpecial("praise","-1"),Cnd.where("id","=",targetId));
                    dao.delete(praise);
                }
            });
        }else{
            return new NutMap().setv("ok",false).setv("msg","未知错误");
        }
        return new NutMap().setv("ok",true);
    }


}
