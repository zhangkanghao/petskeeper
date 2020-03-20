package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.UserProfile;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import cn.koer.petskeeper.util.Toolkit;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.util.Daos;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
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
@Filters(@By(type = CheckTokenFilter.class))
public class UserProfileModule extends BaseModule {

    public static final Log log= Logs.get();
    public static final int TOKEN_LENGTH=10;

    @At("/")
    @GET
    public UserProfile index(HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        return get(userId);
    }

    /**
     * 获取用户详情
     *
     * @return
     */
    @At
    public UserProfile get(int userId) {
//        UserProfile profile = Daos.ext(dao, FieldFilter.locked(UserProfile.class, "avatar")).fetch(UserProfile.class, userId);
        UserProfile profile=dao.fetch(UserProfile.class,userId);
        /**新用户  则新建详情页*/
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setNickname("新用户"+userId);
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
    public Object readAvatar(@Param("userId") int userId, HttpServletRequest req){
        UserProfile profile= Daos.ext(dao,FieldFilter.create(UserProfile.class,"^avatar$")).fetch(UserProfile.class,userId);
        if(profile==null||profile.getAvatar()==null){
            return new File(req.getServletContext().getRealPath("/rs/user_avatar/none.jpg"));
        }
        return profile.getAvatar();
    }

    /**
     * 邮箱验证
     */
    @At("/active/mail")
    @POST
    public Object activeMail(@Attr(scope=Scope.SESSION, value="ident")int userId, HttpServletRequest req) {
        NutMap re = new NutMap();
        UserProfile profile = get(userId);
        if (Strings.isBlank(profile.getEmail())) {
            return re.setv("ok", false).setv("msg", "你还没有填邮箱啊!");
        }
        String token = String.format("%s,%s,%s", userId, profile.getEmail(), System.currentTimeMillis());
        token = Toolkit._3DES_encode(emailKEY, token.getBytes());
        String url = req.getRequestURL() + "?token=" + token;
        String html = "<div>如果无法点击,请拷贝一下链接到浏览器中打开 验证链接:<a href=\"%s\"> %s</a></div>";
        html = String.format(html, url, url);
        try {
            boolean ok = emailService.send(profile.getEmail(), "验证邮件 by Petskeeper", html);
            if (!ok) {
                return re.setv("ok", false).setv("msg", "发送失败");
            }
        } catch (Throwable e) {
            log.debug("发送邮件失败", e);
            return re.setv("ok", false).setv("msg", "发送失败");
        }
        return re.setv("ok", true);
    }

    /**
     * 不需要先登录,为了简单起见,这里直接显示验证结果就好了,后面改成跳转页或者验证成功提示页
     * @param token
     * @param session
     * @return
     */
    @Filters
    @At("/active/mail")
    @GET
    @Ok("raw")
    public String activeMailCallback(@Param("token")String token, HttpSession session) {
        if (Strings.isBlank(token)) {
            return "请不要直接访问这个链接!!!";
        }
        if (token.length() < TOKEN_LENGTH) {
            return "非法token";
        }
        try {
            token = Toolkit._3DES_decode(emailKEY, Toolkit.hexstr2bytearray(token));
            if (token == null) {
                return "非法token";
            }
            String[] tmp = token.split(",", 3);
            if (tmp.length != 3 || tmp[0].length() == 0 || tmp[1].length() == 0 || tmp[2].length() == 0) {
                return "非法token";
            }
            long time = Long.parseLong(tmp[2]);
            if (System.currentTimeMillis() - time > 10*60*1000) {
                return "该验证链接已经超时";
            }
            int userId = Integer.parseInt(tmp[0]);
            Cnd cnd = Cnd.where("userId", "=", userId).and("email", "=", tmp[1]);
            int re = dao.update(UserProfile.class, Chain.make("emailChecked",true), cnd);
            if (re == 1) {
                return "验证成功";
            }
            return "验证失败!!请重新验证!!";
        } catch (Throwable e) {
            log.debug("检查token时出错", e);
            return "非法token";
        }
    }
}
