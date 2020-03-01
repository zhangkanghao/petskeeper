package cn.koer.petskeeper.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

/**
 * @Author Koer
 * @Date 2020/3/1 14:43
 */
@IocBean
@At("/comment")
@Filters(@By(type = CheckSession.class,args = {"ident","/"}))
public class CommentModule extends BaseModule{


}
