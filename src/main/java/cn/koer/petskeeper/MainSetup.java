package cn.koer.petskeeper;

import cn.koer.petskeeper.bean.User;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import java.util.Date;

public class MainSetup implements Setup {
    @Override
    public void init(NutConfig nc) {
        Ioc ioc=nc.getIoc();
        Dao dao=ioc.get(Dao.class);
        Daos.createTablesInPackage(dao,"cn.koer.petskeeper",false);
        if (dao.count(User.class) == 0) {
            User user = new User();
            user.setName("admin");
            user.setPassword("123456");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            dao.insert(user);
        }
    }

    @Override
    public void destroy(NutConfig nc) {

    }
}
