package cn.koer.petskeeper.module;

/**
 * @Author Koer
 * @Date 2020/2/20 17:17
 */
import java.awt.image.BufferedImage;
import javax.servlet.http.HttpSession;

import cn.koer.petskeeper.util.Toolkit;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.gimpy.FishEyeGimpyRenderer;

@IocBean
@At("/captcha")
public class CaptchaModule {

    @At
    @Ok("raw:png")
    public BufferedImage next(HttpSession session, @Param("w") int w, @Param("h") int h) {
        //长或宽为0?重置为默认长宽.
        if (w * h < 1) {
            w = 200;
            h = 60;
        }
        Captcha captcha = new Captcha.Builder(w, h)
                .addText().addBackground(new GradiatedBackgroundProducer())
                .gimp(new FishEyeGimpyRenderer())
                .build();
        String text = captcha.getAnswer();
        session.setAttribute(Toolkit.captcha_attr, text);
        return captcha.getImage();
    }
}