package cn.koer.petskeeper;

import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@SetupBy(MainSetup.class)
@IocBy(type = ComboIocProvider.class, args = {"*js", "ioc/",
        "*anno", "cn.koer.petskeeper",
        "*tx", // 事务拦截 aop
        "*async"}) // 异步执行aop
@Modules(scanPackage = true)
@Ok("json:full")
@Fail("jsp:jsp.500")
@Localization(value="msg/", defaultLocalizationKey="zh-CN")
public class MainModule {

}
