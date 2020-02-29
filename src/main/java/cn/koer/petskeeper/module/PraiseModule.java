package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.Comment;
import cn.koer.petskeeper.bean.Pet;
import cn.koer.petskeeper.bean.Praise;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/29 16:14
 */
@IocBean
@Filters(@By(type = CheckSession.class, args = {"ident", "/"}))
@At("/praise")
public class PraiseModule extends BaseModule {

    @At
    public Object add(@Param("..") final Praise praise, @Attr(scope = Scope.SESSION,value = "ident")int me){
        praise.setUserId(me);
        praise.setCreateTime(new Date());
        praise.setUpdateTime(new Date());
        if(praise.getType()==0){
            //给文章点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Article.class, Chain.makeSpecial("praise","+1"), Cnd.where("id","=",praise.getTargetId()));
                    dao.insert(praise);
                }
            });
        } else if (praise.getType() == 1) {
            //给评论点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Comment.class,Chain.makeSpecial("praise","+1"),Cnd.where("id","=",praise.getTargetId()));
                    dao.insert(praise);
                }
            });
        }else if(praise.getType()==2){
            //给宠物点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Pet.class,Chain.makeSpecial("praise","+1"),Cnd.where("id","=",praise.getTargetId()));
                    dao.insert(praise);
                }
            });
        }else{
            return new NutMap().setv("ok",false).setv("msg","未知错误");
        }
        return new NutMap().setv("ok",true).setv("data",praise);
    }

    @At
    public Object remove(@Param("..") final Praise praise, @Attr(scope = Scope.SESSION,value = "ident")int me){
        if(praise.getType()==0){
            //给文章点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Article.class, Chain.makeSpecial("praise","-1"), Cnd.where("id","=",praise.getTargetId()));
                    dao.delete(praise);
                }
            });
        } else if (praise.getType() == 1) {
            //给评论点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Comment.class,Chain.makeSpecial("praise","-1"),Cnd.where("id","=",praise.getTargetId()));
                    dao.delete(praise);
                }
            });
        }else if(praise.getType()==2){
            //给宠物点赞
            Trans.exec(new Atom() {
                @Override
                public void run() {
                    dao.update(Pet.class,Chain.makeSpecial("praise","-1"),Cnd.where("id","=",praise.getTargetId()));
                    dao.delete(praise);
                }
            });
        }else{
            return new NutMap().setv("ok",false).setv("msg","未知错误");
        }
        return new NutMap().setv("ok",true);
    }


}
