package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author Koer
 */
@IocBean
@At("/user")
@Ok("json:{locked:'password',ignoreNull:true}")
@Fail("http:500")
@Filters(@By(type= CheckSession.class,args = {"ident","/"}))
public class UserModule extends BaseModule{


    @At("/")
    @Ok("jsp:jsp.list") // 真实路径是 /WEB-INF/jsp/user/list.jsp
    public void index() {
    }

    @At
    public int count() {
        return dao.count(User.class);
    }

    @At
    @Filters()
    public Object login(@Param("username") String username, @Param("password") String password, HttpSession session) {
        User user = dao.fetch(User.class, Cnd.where("u_name", "=", username).and("u_pwd", "=", password));
        if (user == null) {
            return false;
        } else {
            session.setAttribute("ident", user.getId());
            return true;
        }
    }

    @At
    @Ok(">>:/")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @At
    protected String checkUser(User user, boolean create) {
        if (user == null) {
            return "空对象";
        }
        if (create) {
            if (Strings.isBlank(user.getName()) || Strings.isBlank(user.getPassword()))
                return "用户名/密码不能为空";
        } else {
            if (Strings.isBlank(user.getPassword()))
                return "密码不能为空";
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
            if (user.getId() < 1)
                return "用户id非法";
        }
        if (user.getName() != null) {
            user.setName(user.getName().trim());
        }
        return null;
    }

    //注册
    @At
    public Object register(@Param("..") User user) {
        NutMap re = new NutMap();
        String msg = checkUser(user, true);
        if (msg != null) {
            return re.setv("ok", false).setv("msg", msg);
        }
        user.setCreateTime(new Date());
        user.getUpdateTime(new Date());
        user = dao.insert(user);
        return re.setv("ok", true).setv("data", user);
    }

    //更新
    @At
    public Object update(@Param("..") User user) {
        NutMap re = new NutMap();
        String msg = checkUser(user, false);
        if (msg != null) {
            return re.setv("ok", false).setv("msg", msg);
        }
        // 不允许更新用户名
        user.setName(null);
        //也不允许更新创建时间
        user.setCreateTime(null);
        // 设置正确的更新时间
        user.setUpdateTime(new Date());
        // 真正更新的其实只有password和salt
        dao.updateIgnoreNull(user);
        return re.setv("ok", true);
    }

    /**
     * 删除，@Attr是session.getAttribute()
     * @param id
     * @param me
     * @return
     */
    @At
    public Object delete(@Param("id") int id, @Attr("me") int me) {
        if (me == id || id < 1) {
            return new NutMap().setv("ok", false).setv("msg", "不能删除当前用户!!");
        }
        // 再严谨一些的话,需要判断是否为>0
        dao.delete(User.class, id);
        return new NutMap().setv("ok", true);
    }

    @At
    public Object query(@Param("name")String name, @Param("..") Pager pager) {
        Cnd cnd = Strings.isBlank(name)? null : Cnd.where("name", "like", "%"+name+"%");
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(User.class, cnd, pager));
        pager.setRecordCount(dao.count(User.class, cnd));
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }

}
