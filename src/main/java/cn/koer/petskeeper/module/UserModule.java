package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.User;
import cn.koer.petskeeper.bean.UserProfile;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import cn.koer.petskeeper.service.UserService;
import cn.koer.petskeeper.util.Toolkit;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Koer
 */
@IocBean
@At("/user")
@Ok("json:{locked:'password',ignoreNull:true}")
@Fail("http:500")
@Filters(@By(type = CheckTokenFilter.class))
public class UserModule extends BaseModule {

    @Inject
    protected UserService userService;
    @Inject
    protected RedisService redisService;

    @At("/")
    @Ok("jsp:jsp.user.list")
    public void index() {
    }

    @At
    public int count() {
        return dao.count(User.class);
    }

    @At
    @Filters()
    @POST
    public Object login(@Param("username") String username, @Param("password") String password) {
        NutMap re = new NutMap();
        User user = dao.fetch(User.class, Cnd.where("name", "=", username));
        if (user==null||!user.getPassword().equals(Toolkit.passwordEncode(password,user.getSalt()))) {
            return re.setv("ok", false).setv("msg", "用户名或密码错误");
        } else {
            String token=Toolkit.genToken(user.getId(),username,user.getSalt());
            redisService.set(token,String.valueOf(user.getId()));
            return re.setv("ok", true).setv("auth",token);
        }
    }

    @At
    public void logout(HttpServletRequest request) {
        String token=request.getHeader("authorization");
        redisService.del(token);
    }

    protected String checkUser(User user, boolean create) {
        if (user == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(user.getName()) || Strings.isBlank(user.getPassword())) {
                return "用户名/密码不能为空";
            }
        } else {
            if (Strings.isBlank(user.getPassword())) {
                return "密码不能为空";
            }
        }
        String pwd = user.getPassword().trim();
        if (pwd.length() < 6 || pwd.length() > 18) {
            return "密码长度不符合规范";
        }
        user.setPassword(pwd);
        if (create) {
            int count = dao.count(User.class, Cnd.where("name", "=", user.getName()));
            if (count != 0) {
                return "用户名已被注册";
            }
        } else {
            if (user.getId() < 1) {
                return "用户id非法";
            }
        }
        if (user.getName() != null) {
            user.setName(user.getName().trim());
        }
        return null;
    }

    /**
     * 添加
     */
    @At
    @Filters()
    public Object add(@Param("..") User user) {
        NutMap re = new NutMap();
        String msg = checkUser(user, true);
        if (msg != null) {
            return re.setv("ok", false).setv("msg", msg);
        }
        user = userService.add(user.getName(), user.getPassword());
        return re.setv("ok", true).setv("data", user);
    }

    /**
     * 更新
     */
    @At
    public Object update(@Param("password") String password, @Attr("ident") int me) {
        if (Strings.isBlank(password) || password.length() < 6) {
            return new NutMap().setv("ok", false).setv("msg", "密码不符合要求");
        }
        userService.updatePassword(me, password);
        return new NutMap().setv("ok", true);
    }

    @At
    @Filters()
    public Object resetCode(@Param("username")String username) {
        NutMap re = new NutMap();
        User user=dao.fetch(User.class,username);
        if(user==null){
            return re.setv("ok", false).setv("msg", "用户不存在");
        }
        UserProfile profile=dao.fetch(UserProfile.class,user.getId());
        if (profile==null||!profile.isEmailChecked()){
            return re.setv("ok", false).setv("msg", "未绑定邮箱");
        }
        String verifyCode=Toolkit.rePwdCheck();
        String html = profile.getNickname()+",你好,这是你修改密码所需要的验证码："+ verifyCode+"<br>如果不是本人操作请忽视";
        try {
            boolean ok = emailService.send(profile.getEmail(), "验证邮件 by Petskeeper", html);
            if (!ok) {
                return re.setv("ok", false).setv("msg", "发送失败");
            }
        } catch (Throwable e) {
            return re.setv("ok", false).setv("msg", "发送失败");
        }
        user.setDescription(verifyCode);
        dao.update(user);
        return re.setv("ok", true);
    }

    @At
    @Filters()
    @POST
    public Object resetpwd(@Param("username")String username,@Param("password")String newpwd,@Param("verifyCode")String vCode) {
        NutMap re = new NutMap();
        User user=dao.fetch(User.class,username);
        if(user==null||user.getDescription()==null||Strings.isBlank(user.getDescription())){
            return re.setv("ok",false).setv("msg","用户不存在或未获取验证码");
        }
        if(!user.getDescription().equals(vCode)){
            return re.setv("ok",false).setv("msg","验证码错误");
        }
        userService.updatePassword(user.getId(),newpwd);
        return re.setv("ok",true).setv("msg","修改成功");
    }
    /**
     * 删除，@Attr是session.getAttribute()
     *
     * @param id
     * @param me
     * @return
     */
    @At
    @Aop(TransAop.READ_COMMITTED)
    public Object delete(@Param("id") int id, @Attr("ident") int me) {
        if (me == id || id < 1) {
            return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
        }
        /**再严谨一些的话,需要判断是否为>0*/
        dao.delete(User.class, id);
        dao.clear(UserProfile.class, Cnd.where("userId", "=", me));
        return new NutMap().setv("ok", true);
    }

    @At
    public Object query(@Param("name") String name, @Param("..") Pager pager) {
        Cnd cnd = Strings.isBlank(name) ? null : Cnd.where("name", "like", "%" + name + "%");
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(User.class, cnd, pager));
        pager.setRecordCount(dao.count(User.class, cnd));
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }

}
