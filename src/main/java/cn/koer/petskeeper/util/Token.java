package cn.koer.petskeeper.util;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Date;

public class Token {

    public static final Log log = Logs.get();

    public static String genToken(int userId,String username,String salt){
        String info=salt+","+String.valueOf(userId)+","+username+","+System.currentTimeMillis();
        return Lang.digest("SHA-256",info);
    }
}
