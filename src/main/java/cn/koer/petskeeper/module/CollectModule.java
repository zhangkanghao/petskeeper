package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.Collect;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import java.util.Date;
import java.util.List;

/**
 * @Author Koer
 * @Date 2020/2/29 16:43
 */
@IocBean
@At("/collect")
@Filters(@By(type = CheckSession.class,args = {"ident","/"}))
public class CollectModule extends BaseModule{

    @At
    public Object add(@Attr(scope = Scope.SESSION,value = "ident")int userId,@Param("articleId") int articleId){
        Article article=dao.fetch(Article.class,articleId);
        //文章不存在或者是未发布或是私密状态
        if(article==null||article.getStatus()==0|| "私密".equals(article.getReadType())){
            return new NutMap().setv("ok",false).setv("msg","非法操作");
        }
        Collect collect=dao.fetch(Collect.class, Cnd.where("uid","=",userId).and("aid","=",articleId));
        //已经收藏了
        if(collect!=null){
            if(collect.getStatus()==2) {
                return new NutMap().setv("ok", false).setv("msg", "已经收藏该文章");
            }else {
                collect.setStatus(1);
                collect.setUpdateTime(new Date());
                dao.update(collect);
                return new NutMap().setv("ok",true).setv("data",collect);
            }
        }
        collect=new Collect();
        collect.setUserId(userId);
        collect.setArticleId(articleId);
        collect.setStatus(2);
        collect.setCreateTime(new Date());
        collect.setUpdateTime(new Date());
        dao.insert(collect);
        return new NutMap().setv("ok",true).setv("data",collect);
    }

    @At
    public Object remove(@Attr(scope = Scope.SESSION,value = "ident")int userId,@Param("articleId") int articleId){
        Collect collect=dao.fetch(Collect.class, Cnd.where("uid","=",userId).and("aid","=",articleId));
        if(collect==null){
            return new NutMap().setv("ok",false).setv("msg","还没有收藏该文章呢！");
        }
        collect.setStatus(0);
        collect.setUpdateTime(new Date());
        dao.update(collect);
        return new NutMap().setv("ok",true);
    }

    @At
    public Object query(@Attr(scope = Scope.SESSION,value = "ident")int userId, @Param("type")String type, @Param("pager")Pager pager){
        QueryResult rs=new QueryResult();
        List<Article> list=dao.query(Article.class,Cnd.wrap("id in (select aid from t_collect where uid="+userId+" and status=1)"+"and type='"+type+"'"),pager);
        pager.setRecordCount(dao.count(Article.class,Cnd.wrap("id in (select aid from t_collect where uid="+userId+" and status=1"+"and type='"+type+"'")));
        for (Article article:list) {
            if("私密".equals(article.getReadType())){
                article.setSubject("私密文章");
                article.setContent("该文章已被作者设置为私密……");
                article.setStatus(-1);
            }
        }
        rs.setList(list);
        rs.setPager(pager);
        return rs;
    }

}
