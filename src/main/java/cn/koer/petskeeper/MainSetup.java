package cn.koer.petskeeper;

import cn.koer.petskeeper.bean.User;
import cn.koer.petskeeper.service.UserService;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import redis.clients.jedis.Jedis;

import java.util.Date;

public class MainSetup implements Setup {
    @Override
    public void init(NutConfig nc) {

        Ioc ioc=nc.getIoc();
        Dao dao=ioc.get(Dao.class);
        // 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
        ioc.get(NutQuartzCronJobFactory.class);
        Daos.createTablesInPackage(dao,"cn.koer.petskeeper",false);
        Daos.migration(dao, User.class, true, false, false);
        if (dao.count(User.class) == 0) {
            UserService us = ioc.get(UserService.class);
            us.add("admin", "123456");
        }
    }

    @Override
    public void destroy(NutConfig nc) {

    }
}
