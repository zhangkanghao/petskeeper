package cn.koer.petskeeper.service;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @Author Koer
 * @Date 2020/2/19 14:28
 */
@IocBean(name = "EmailService")
public class EmailServiceImpl implements EmailService {

    private static final Log log= Logs.get();

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Override
    public boolean send(String to, String subject, String html) {
        try {
            HtmlEmail email = ioc.get(HtmlEmail.class);
            email.setSubject(subject);
            email.setMsg(html);
            email.addTo(to);
            email.buildMimeMessage();
            email.sendMimeMessage();
            return true;
        } catch (EmailException e) {
            log.info("send email fail",e);
            return false;
        }
    }
}
