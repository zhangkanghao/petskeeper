package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.UserProfile;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Koer
 * @Date 2020/2/18 14:05
 */
@At("/user/profile")
@IocBean
/**检查session是否有ident*/
@Filters(@By(type = CheckSession.class, args = {"ident", "/"}))
public class UserProfileModule extends BaseModule {

    @At("/")
    @Ok("jsp:jsp.user.profile")
    @GET
    public UserProfile index(@Attr(scope = Scope.SESSION,value = "ident")int userId){
        return get(userId);
    }

    /**
     * 获取用户详情
     *
     * @param userId 写在session里的
     * @return
     */
    @At
    public UserProfile get(@Attr(scope = Scope.SESSION, value = "ident") int userId) {
        UserProfile profile = Daos.ext(dao, FieldFilter.locked(UserProfile.class, "avatar")).fetch(UserProfile.class, userId);
        /**新用户  则新建详情页*/
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setCreateTime(new Date());
            profile.setUpdateTime(new Date());
            dao.insert(profile);
        }
        return profile;
    }

    @At
    @AdaptBy(type = JsonAdaptor.class)
    @Ok("void")
    public void update(@Param("..") UserProfile profile, @Attr(scope = Scope.SESSION, value = "ident") int userId) {
        if (profile == null) {
            return;
        }
        /**
         * 重置userid 防止恶意修改
         * 不能通过此方法更改头像
         */
        profile.setUserId(userId);
        profile.setUpdateTime(new Date());
        profile.setAvatar(null);
        UserProfile old=get(userId);
        /**检查email相关的更新*/
        if(old.getEmail()==null){
            /**原先没有邮箱，就算设置了邮箱也是未check状态*/
            profile.setEmailChecked(false);
        }else {
            if(profile.getEmail()==null){
                profile.setEmail(old.getEmail());
                profile.setEmailChecked(old.isEmailChecked());
            }else if (!profile.getEmail().equals(old.getEmail())){
                profile.setEmailChecked(false);
            }else {
                profile.setEmailChecked(old.isEmailChecked());
            }
        }
        Daos.ext(dao,FieldFilter.create(UserProfile.class,null,"avatar",true)).update(profile);
    }

    @AdaptBy(type = UploadAdaptor.class,args={"${app.root}/WEB-INF/tmp/user_avatar", "8192", "utf-8", "20000", "102400"})
    @At("/avatar")
    @POST
    @Ok(">>:/user/profile")
    public void uploadAvatar(@Param("file")TempFile tf, @Attr(scope = Scope.SESSION,value = "ident")int userId, AdaptorErrorContext err){
        String msg=null;
        if(err!=null&&err.getAdaptorErr()!=null) {
            msg = "文件大小不符合规定";
        }else if(tf==null){
            msg="空文件";
        }else{
            UserProfile profile=get(userId);
            try (InputStream ins = tf.getInputStream()) {
                BufferedImage image = Images.read(ins);
                image = Images.zoomScale(image, 128, 128, Color.WHITE);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Images.writeJpeg(image, out, 0.8f);
                profile.setAvatar(out.toByteArray());
                dao.update(profile, "^avatar$");
            } catch(DaoException e) {
                e.printStackTrace();
                msg = "系统错误";
            } catch (Throwable e) {
                msg = "图片格式错误";
            }
        }
        if(msg!=null){
            Mvcs.getHttpSession().setAttribute("upload-error-msg",msg);
        }
    }

    @Ok("raw:jpg")
    @At("/avatar")
    @GET
    public Object readAvatar(@Attr(scope=Scope.SESSION,value = "ident")int userId, HttpServletRequest req){
        UserProfile profile= Daos.ext(dao,FieldFilter.create(UserProfile.class,"^avatar$")).fetch(UserProfile.class,userId);
        if(profile==null||profile.getAvatar()==null){
            return new File(req.getServletContext().getRealPath("/rs/user_avatar/none.jpg"));
        }
        return profile.getAvatar();
    }


}
