package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Pet;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.*;

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
            return re.setv("ok",false).setv("msg","该宠物不存在");
        }else if(pet.getUserId()!=me){
            return re.setv("ok",false).setv("msg","仅可以注销本人的宠物信息");
        }
        dao.delete(Pet.class,petId);
        return re.setv("ok",true);
    }

    @At
    public Object update(@Param("..")Pet pet,@Attr(scope = Scope.SESSION,value = "ident")int userId){
        NutMap re=new NutMap();
        pet.setUserId(userId);
        String msg=checkPet(pet);
        if(msg!=null){
            return re.setv("ok",false).setv("msg",msg);
        }
        pet.setUpdateTime(new Date());
        dao.update(pet);
        return re.setv("ok",true).setv("data",pet);
    }

    @At
    public Object get(@Param("petId")int petId){
        return dao.fetch(Pet.class,Cnd.where("id","=",petId));
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


}
