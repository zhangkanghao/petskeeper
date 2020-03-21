package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.User;
import cn.koer.petskeeper.bean.UserProfile;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/26 13:02
 */
@At("/article")
@IocBean
@Filters(@By(type = CheckTokenFilter.class))
public class ArticleModule extends BaseModule{


    @At
    public Object get(@Param("articleId")int articleId){
        return dao.fetch(Article.class,articleId);
    }


    @At
    public Object publish(@Param("..")Article article,HttpServletRequest req){
        NutMap re=new NutMap();
        int userId= (int) req.getAttribute("uid");
        UserProfile profile=dao.fetch(UserProfile.class,userId);
        article.setUpdateTime(new Date());
        //新建的
        if(article.getId()==0){
            article.setCreateTime(new Date());
            article.setUserId(userId);
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



    @At("/addPic")
    @POST
    @AdaptBy(type = UploadAdaptor.class,args={"${app.root}/WEB-INF/tmp/release", "8192", "utf-8", "20000", "10485760"})
    public Object uploadPic(@Param("file") TempFile tf, HttpServletRequest req,AdaptorErrorContext err){
        NutMap re=new NutMap();
        String msg=null;
        String path = null;
        if(err!=null&&err.getAdaptorErr()!=null) {
            msg = "文件大小不符合规定";
        }else if(tf==null){
            msg="空文件";
        }else{
            try (InputStream ins = tf.getInputStream()) {
                BufferedImage image = Images.read(ins);
                image = Images.zoomScale(image, 512, 512, Color.WHITE);
                String basepath=req.getServletContext().getRealPath("articleImg/release/");
                File directory = new File(basepath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                path=R.UU16();
                Images.writeJpeg(image, new File(basepath+ path+".jpg"), 0.8f);
            } catch(DaoException e) {
                e.printStackTrace();
                msg = "系统错误";
            } catch (Throwable e) {
                msg = "图片格式错误";
            }
        }
        if(msg!=null){
            return re.setv("ok",false).setv("msg",msg);
        }
        return re.setv("ok",true).setv("path","/articleImg/release/"+path+".jpg");
    }
}
