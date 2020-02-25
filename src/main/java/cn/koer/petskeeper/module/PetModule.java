package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Pet;
import cn.koer.petskeeper.bean.User;
import cn.koer.petskeeper.bean.UserProfile;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/24 15:58
 */
@IocBean
@At("/pet")
public class PetModule extends BaseModule {



    @At("/")
    @Ok("jsp:jsp.pet.list")
    public void index(){
    }

    @At
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
    public Object add(@Param("..") Pet pet,@Attr(scope = Scope.SESSION,value = "ident")int userId) {
        NutMap re = new NutMap();
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

    @At
    public Object delete(@Param("petId")int petId, @Attr(scope = Scope.SESSION,value = "ident")int me){
        NutMap re=new NutMap();
        Pet pet=dao.fetch(Pet.class,petId);
        if(pet==null){
            return re.setv("ok","false").setv("msg","该宠物不存在");
        }else if(pet.getUserId()!=me){
            return re.setv("ok",false).setv("msg","仅可以注销本人的宠物信息");
        }
        dao.delete(Pet.class,petId);
        return re.setv("ok",true);
    }

    @At
    public Object update(@Param("..")Pet newpet,@Attr(scope = Scope.SESSION,value = "ident")int me){
        NutMap re=new NutMap();
        Pet pet=dao.fetch(Pet.class,newpet.getId());
        if (Strings.isBlank(newpet.getPetName().trim())){
            pet.setPetName(null);
        }else{
            pet.setPetName(newpet.getPetName());
        }
        if (Strings.isBlank(newpet.getDescription().trim())){
            pet.setDescription(null);
        }else{
            pet.setDescription(newpet.getDescription());
        }
        pet.setBirthTime(newpet.getBirthTime());
        pet.setAdoptionTime(newpet.getAdoptionTime());
        pet.setUpdateTime(new Date());

        dao.update(pet);
        return re.setv("ok",true).setv("data",pet);
    }

    /**
     * 我的宠物
     * @param userId
     * @param pager
     * @return
     */
    @At
    public Object query(@Attr(scope = Scope.SESSION,value = "ident")int userId, @Param("..")Pager pager){
        Cnd cnd=userId==0?null:Cnd.where("uid","=",userId);
        int count=dao.count(Pet.class,cnd);
        System.out.println(count);
        if (count==0){
            return new NutMap().setv("ok",false).setv("msg","宠物列表为空");
        }
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(Pet.class, cnd, pager));
        pager.setRecordCount(count);
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }


   /** @AdaptBy(type = UploadAdaptor.class,args={"${app.root}/WEB-INF/tmp/pet_avatar", "8192", "utf-8", "20000", "102400"})
    @At("/pic")
    @POST
    @Ok(">>:/pet/profile")
    public void uploadAvatar(@Param("file") TempFile tf, @Attr(scope = Scope.SESSION,value = "ident")int userId, AdaptorErrorContext err){
        String msg=null;
        if(err!=null&&err.getAdaptorErr()!=null) {
            msg = "文件大小不符合规定";
        }else if(tf==null){
            msg="空文件";
        }else{
            UserProfile profile=get(userId);
            try (InputStream ins = tf.getInputStream()) {
                BufferedImage image = Images.read(ins);
                image = Images.zoomScale(image, 128, 128, Color.WHITE);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Images.writeJpeg(image, out, 0.8f);
                profile.setAvatar(out.toByteArray());
                dao.update(profile, "^avatar$");
            } catch(DaoException e) {
                e.printStackTrace();
                msg = "系统错误";
            } catch (Throwable e) {
                msg = "图片格式错误";
            }
        }
        if(msg!=null){
            Mvcs.getHttpSession().setAttribute("upload-error-msg",msg);
        }
    }*/

    @Ok("raw:jpg")
    @At("/avatar")
    @GET
    public Object readAvatar(@Attr(scope=Scope.SESSION,value = "ident")int userId, HttpServletRequest req){
        UserProfile profile= Daos.ext(dao, FieldFilter.create(UserProfile.class,"^avatar$")).fetch(UserProfile.class,userId);
        if(profile==null||profile.getAvatar()==null){
            return new File(req.getServletContext().getRealPath("/rs/user_avatar/none.jpg"));
        }
        return profile.getAvatar();
    }

}
