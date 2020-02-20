package cn.koer.petskeeper.service;

/**
 * @Author Koer
 * @Date 2020/2/19 14:28
 */
public interface EmailService {
    /**
     *
     * @param to
     * @param subject
     * @param html
     * @return
     */
    boolean send(String to,String subject,String html);
}
