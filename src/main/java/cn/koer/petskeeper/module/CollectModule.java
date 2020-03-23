package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.Collect;
import cn.koer.petskeeper.bean.Praise;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @Author Koer
 * @Date 2020/2/29 16:43
 */
@IocBean
@At("/collect")
@Filters(@By(type = CheckTokenFilter.class))
public class CollectModule extends BaseModule{

    @At
    public Object get(HttpServletRequest req,@Param("articleId")int articleId){
        int userId= (int) req.getAttribute("uid");
        Collect collect=dao.fetch(Collect.class,Cnd.where("uid","=",userId).and("aid","=",articleId));
        if(collect==null){
            return new NutMap("ok",false);
        }
        return new NutMap().setv("ok",true);
    }


    @At
    public Object add(HttpServletRequest req,@Param("articleId") int articleId){
        int userId= (int) req.getAttribute("uid");
        Article article=dao.fetch(Article.class,articleId);
        Collect collect1=dao.fetch(Collect.class,Cnd.where("aid","=",articleId).and("uid","=",userId));
        if(collect1!=null){
            return new NutMap().setv("ok",false).setv("msg","已经点过赞了");
        }
        //文章不存在或者是未发布或是私密状态
        if(article==null||!article.isStatus()|| !article.isReadType()){
            return new NutMap().setv("ok",false).setv("msg","非法操作");
        }
        Collect collect=new Collect();
        collect.setUserId(userId);
        collect.setArticleId(articleId);
        collect.setCreateTime(new Date());
        collect.setUpdateTime(new Date());
        dao.insert(collect);
        return new NutMap().setv("ok",true).setv("data",collect);
    }

    @At
    public Object remove(HttpServletRequest req,@Param("articleId") int articleId){
        int userId= (int) req.getAttribute("uid");
        Collect collect=dao.fetch(Collect.class, Cnd.where("uid","=",userId).and("aid","=",articleId));
        if(collect==null){
            return new NutMap().setv("ok",false).setv("msg","还没有收藏该文章呢！");
        }
        dao.delete(collect);
        return new NutMap().setv("ok",true);
    }

    @At
    public Object query(@Attr(scope = Scope.SESSION,value = "ident")int userId, @Param("type")String type, @Param("pager")Pager pager){
        QueryResult rs=new QueryResult();
        List<Article> list=dao.query(Article.class,Cnd.wrap("id in (select aid from t_collect where uid="+userId+" and status=1)"+"and type='"+type+"'"),pager);
        pager.setRecordCount(dao.count(Article.class,Cnd.wrap("id in (select aid from t_collect where uid="+userId+" and status=1"+"and type='"+type+"'")));
        for (Article article:list) {
//            if("私密".equals(article.getReadType())){
//                article.setSubject("私密文章");
//                article.setContent("该文章已被作者设置为私密……");
//                article.setStatus(-1);
//            }
        }
        rs.setList(list);
        rs.setPager(pager);
        return rs;
    }

}
