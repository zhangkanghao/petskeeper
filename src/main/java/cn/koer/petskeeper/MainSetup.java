package cn.koer.petskeeper;

import cn.koer.petskeeper.bean.User;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import java.util.Date;

public class MainSetup implements Setup {
    @Override
    public void init(NutConfig nc) {

        Ioc ioc=nc.getIoc();
        Dao dao=ioc.get(Dao.class);
        // 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
        ioc.get(NutQuartzCronJobFactory.class);
        Daos.createTablesInPackage(dao,"cn.koer.petskeeper",false);
        if (dao.count(User.class) == 0) {
            User user = new User();
            user.setName("admin");
            user.setPassword("123456");
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            dao.insert(user);
        }
        /** 测试发送邮件
        try {
            HtmlEmail email = ioc.get(HtmlEmail.class);
            email.setSubject("测试NutzBook");
            email.setMsg("This is a test mail ... :-)" + System.currentTimeMillis());
            email.addTo("651812128@qq.com");
            email.buildMimeMessage();
            email.sendMimeMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void destroy(NutConfig nc) {

    }
}
