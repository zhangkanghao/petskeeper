package cn.koer.petskeeper.module;

import cn.koer.petskeeper.bean.Pet;
import cn.koer.petskeeper.bean.User;
import javafx.beans.binding.ObjectExpression;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Param;

import java.util.Date;

/**
 * @Author Koer
 * @Date 2020/2/24 15:58
 */
@IocBean
@At("/pet")
public class PetModule extends BaseModule {



    @At
    private Object add(@Param("..") Pet pet) {
        NutMap re = new NutMap();
        if (pet == null) {
            return re.setv("ok",false).setv("msg","空对象");
        }
        if (Strings.isBlank(pet.getPetName())) {
            return re.setv("ok",false).setv("msg","名称不能为空");
        }
        if(pet.getUserId()==0){
            return re.setv("ok",false).setv("msg","请重新登录");
        }
        pet.setCreateTime(new Date());
        pet.setUpdateTime(new Date());
        dao.insert(pet);
        return re.setv("ok",true).setv("data",pet);

    }

    @At
    private Object delete(@Param("petId")int petId, @Attr(scope = Scope.SESSION,value = "ident")int me){
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
    private Object update(@Param("..")Pet newpet,@Attr(scope = Scope.SESSION,value = "ident")int me){
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
    private Object query(@Attr(scope = Scope.SESSION,value = "ident")int userId, @Param("pager")Pager pager){
        Cnd cnd=userId==0?null:Cnd.where("uid","=",userId);
        QueryResult qr = new QueryResult();
        qr.setList(dao.query(Pet.class, cnd, pager));
        pager.setRecordCount(dao.count(Pet.class, cnd));
        qr.setPager(pager);
        //默认分页是第1页,每页20条
        return qr;
    }


}
