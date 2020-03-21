package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Article;
import cn.koer.petskeeper.bean.UserProfile;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nutz.dao.*;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.Daos;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    private SqlCallback getCallback(){
        return new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<JSONObject> list= new ArrayList<>();
                while(rs.next()){
                    JSONObject obj=new JSONObject();
                    obj.put("id",rs.getInt("id"));
                    obj.put("nickname",rs.getString("nickname"));
                    obj.put("content",rs.getString("content"));
                    obj.put("praise",rs.getInt("praise"));
                    obj.put("userId",rs.getInt("uid"));
                    obj.put("targetId",rs.getInt("targetId"));
                    list.add(obj);
                }
                return list;
            }
        };
    }

    @At("/")
    public Object homepageArticle(HttpServletRequest req,@Param("..") Pager pager) {
        int userId= (int) req.getAttribute("uid");
        Sql sql= Sqls.create("SELECT a.id,a.uid,content,a.praise,up.nickname,targetId FROM t_article a LEFT JOIN t_user_profile up on a.uid=up.uid LEFT JOIN (SELECT * from t_praise where userId=@userId AND type=0) p ON p.targetId=a.id WHERE to_days(a.ut)=to_days(NOW()) and a.`status`=1 ORDER BY a.ut ASC");
        sql.setParam("userId",userId);
        pager.setRecordCount((int) Daos.queryCount(dao,sql));
        sql.setPager(pager);
        sql.setCallback(getCallback());
        Entity<Record> entity = dao.getEntity(Record.class);
        sql.setEntity(entity);
        dao.execute(sql);
        System.out.println(pager.getPageCount()+","+pager.getRecordCount());
        List<Record> articles = sql.getList(Record.class);
        return articles;
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
