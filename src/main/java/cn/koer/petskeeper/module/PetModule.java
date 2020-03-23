package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Pet;
import cn.koer.petskeeper.bean.UserProfile;
import cn.koer.petskeeper.filter.CheckTokenFilter;
import org.nutz.dao.Cnd;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/24 15:58
 */
@IocBean
@At("/pet")
@Filters(@By(type = CheckTokenFilter.class))
public class PetModule extends BaseModule {


    protected String checkPet(Pet pet) {
        if (pet == null) {
            return "空对象";
        }
        if(pet.getUserId()==0){
            return "请先登录";
        }
        if (Strings.isBlank(pet.getPetName())) {
            return "名称不能为空";
        }else if(pet.getPetName().trim().length()<2||pet.getPetName().trim().length()>10){
            return "昵称长度不符合规范";
        }
        pet.setPetName(pet.getPetName().trim());
        if(Strings.isBlank(pet.getGender())){
            pet.setGender(null);
        }
        if(Strings.isBlank(pet.getType())){
            pet.setType(null);
        }
        if(Strings.isBlank(pet.getDescription())){
            pet.setDescription(null);
        }
        if(Strings.isBlank(pet.isSterilized())){
            pet.setSterilized(null);
        }
        return null;
    }

    @At
    public Object get(@Param("petId")int petId){
        Pet pet=dao.fetch(Pet.class,Cnd.where("id","=",petId));
        return pet;
    }

    @At
    public Object add(@Param("..") Pet pet, HttpServletRequest req) {
        NutMap re = new NutMap();
        int userId= (int) req.getAttribute("uid");
        pet.setUserId(userId);
        String msg=checkPet(pet);
        if(msg!=null){
            return re.setv("ok",false).setv("msg",msg);
        }
        pet.setCreateTime(new Date());
        pet.setUpdateTime(new Date());
        dao.insert(pet);
        return re.setv("ok",true).setv("data",pet);

    }

    @AdaptBy(type = UploadAdaptor.class,args={"${app.root}/WEB-INF/tmp/pet_avatar", "8192", "utf-8", "20000", "1024000"})
    @At("/avatar")
    @POST
    public Object uploadAvatar(@Param("file") TempFile tf, AdaptorErrorContext err,@Param("petId")int petId){
        String msg=null;
        Pet pet = null;
        if(err!=null&&err.getAdaptorErr()!=null) {
            msg = "文件大小不符合规定";
        }else if(tf==null){
            msg="空文件";
        }else{
            pet= (Pet) get(petId);
            try (InputStream ins = tf.getInputStream()) {
                BufferedImage image = Images.read(ins);
                image = Images.zoomScale(image, 128, 128, Color.WHITE);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Images.writeJpeg(image, out, 0.8f);
                pet.setPic(out.toByteArray());
                dao.update(pet, "^pic$");
            } catch(DaoException e) {
                e.printStackTrace();
                msg = "系统错误";
            } catch (Throwable e) {
                msg = "图片格式错误";
            }
        }
        if(msg!=null){
            return new NutMap().setv("ok",false).setv("msg",msg);
        }
        return pet.getPic();
    }

    @Ok("raw:jpg")
    @At("/avatar")
    @Filters()
    @GET
    public Object readAvatar(@Param("petId")int petId,HttpServletRequest req){
        Pet pet= Daos.ext(dao, FieldFilter.create(Pet.class,"^pic$")).fetch(Pet.class, petId);
        if(pet==null||pet.getPic()==null){
            return new File(req.getServletContext().getRealPath("/rs/user_avatar/none.jpg"));
        }
        return pet.getPic();
    }

    @At
    public Object delete(@Param("petId")int petId,HttpServletRequest req){
        NutMap re=new NutMap();
        int userId= (int) req.getAttribute("uid");
        Pet pet=dao.fetch(Pet.class,petId);
        if(pet==null){
            return re.setv("ok",false).setv("msg","该宠物不存在");
        }else if(pet.getUserId()!=userId){
            return re.setv("ok",false).setv("msg","仅可以注销本人的宠物信息");
        }
        dao.delete(Pet.class,petId);
        return re.setv("ok",true);
    }

    @At
    public Object update(@Param("..")Pet pet,HttpServletRequest req){
        NutMap re=new NutMap();
        int userId= (int) req.getAttribute("uid");
        pet.setUserId(userId);
        String msg=checkPet(pet);
        if(msg!=null){
            return re.setv("ok",false).setv("msg",msg);
        }
        pet.setUpdateTime(new Date());
        dao.update(pet);
        return re.setv("ok",true).setv("data",pet);
    }



    /**
     * 我的宠物
     * @return list
     */
    @At
    public Object query(HttpServletRequest req){
        int userId= (int) req.getAttribute("uid");
        return dao.query(Pet.class,Cnd.where("uid","=",userId));
    }


}
