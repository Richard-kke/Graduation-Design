package com.qf.dao;

import com.qf.domain.GoodsType;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface GoodsTypeDao {
    //获取所有商品类型
    @Select("select * from t_goodstype")
    @ResultType(GoodsType.class)
    List<GoodsType> getAllGoodsType();
    //查询商品类型
    @Select("<script>" +
            "select * from t_goodstype " +
            "<where>" +
            "<if test='flag!=null and flag.length()!=0'>" +
            " flag=#{flag} " +
            "</if>" +
            "<if test='name!=null and name.length()!=0'>" +
            " and name like concat('%',#{name},'%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<GoodsType> getSearchtype(@Param("flag") String flag, @Param("name") String name);
    //删除商品类型
    @Delete("delete from t_goodstype where id=#{id}")
    int deleteGoodsType(@Param("id") Integer id);
    //增加商品类型
    @Insert("insert into t_goodstype(name,level,parentName,flag) " +  " values(#{name},1,null,1)")
    int addGoodsType(GoodsType goodsType);

}
