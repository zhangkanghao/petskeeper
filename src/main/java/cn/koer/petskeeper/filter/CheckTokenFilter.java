package cn.koer.petskeeper.filter;

import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpServletRequest;


public class CheckTokenFilter implements ActionFilter {

    @Inject
    protected RedisService redisService;

    @Override
    public View match(ActionContext actionContext) {
        RedisService redisService=Mvcs.getIoc().get(RedisService.class);
        HttpServletRequest request= Mvcs.getReq();
        String token=request.getHeader("authorization");
        NutMap re=new NutMap();
        if(token==null||Strings.isBlank(token)){
            re.setv("ok",false).setv("errMsg","请先登录");
            return new UTF8JsonView().setData(re);
        }
        String uid=redisService.get(token);
        if(uid==null||Strings.isBlank(uid)){
            re.setv("errMsg","登录超时，请重新登录");
            return new UTF8JsonView().setData(re);
        }
        int userId=Integer.valueOf(uid);
        request.setAttribute("uid",userId);
        return null;
    }
}
