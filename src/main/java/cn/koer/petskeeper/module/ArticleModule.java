package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;

import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/26 13:02
 */
@At("/article")
@IocBean
public class ArticleModule extends BaseModule{

    @At("/")
    @Ok("jsp:jsp.article.test")
    public void index(){
    }


    @At
    public Object get(@Param("articleId")int articleId){
        return dao.fetch(Article.class,articleId);
    }

    /**
     * 发布或者编辑到草稿箱  根据status来定
     * @param article
     * @return
     */
    @At
    public Object save(@Param("..")Article article,@Attr(scope = Scope.SESSION,value = "User") User user){
        NutMap re=new NutMap();
        if(article.getSubject().length()<5){
            return re.setv("ok",false).setv("msg","标题不能少于5个字");
        }
        if(!article.isAnnoymous()){
            article.setNickname(user.getName());
        }
        article.setUpdateTime(new Date());
        //新建的
        if(article.getId()==0){
            article.setCreateTime(new Date());
            article.setUserId(user.getId());
            dao.insert(article);
        }else {
            dao.update(article);
        }
        return re.setv("ok",true).setv("data",article);
    }

    @At
    public Object delete(@Param("articleId")int articleId,@Attr(scope = Scope.SESSION,value = "ident")int userId){
        Article article=dao.fetch(Article.class,articleId);
        if(article.getUserId()!=userId){
            return new NutMap().setv("ok",false).setv("msg","无操作权限");
        }
        dao.delete(article);
        return new NutMap().setv("ok",true);
    }

    /**
     * 查询已经发布的
     * @param userId
     * @param pager
     * @return
     */
    @At
    public Object queryPost(@Param("userId")int userId,@Param("..") Pager pager){
        Cnd cnd=Cnd.where("uid","=",userId).and("status","=","1");
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(Article.class, cnd, pager));
        pager.setRecordCount(dao.count(Article.class, cnd));
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }

    /**
     *  查询草稿箱
     */
    @At
    public Object querySave(@Param("userId")int userId,@Param("..") Pager pager){
        Cnd cnd=Cnd.where("uid","=",userId).and("status","=","0");
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(Article.class, cnd, pager));
        pager.setRecordCount(dao.count(Article.class, cnd));
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }

    /**
     * 首页上新动态，一天内的
     * @param pager
     * @return
     */
    @At
    public Object queryList(@Param("..") Pager pager){
        Date publishTime = new Date(System.currentTimeMillis() - 24*60*60*1000L);
        Condition cnd=Cnd.where("ut",">",publishTime).and("status","=","1").desc("ut");
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(Article.class, cnd, pager));
        pager.setRecordCount(dao.count(Article.class, cnd));
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }

}
