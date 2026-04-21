package com.qf.dao;

import org.apache.ibatis.annotations.*;
import com.qf.domain.Goods;

import java.util.List;

public interface GoodsDao {
    //获取所有商品
    @Select("select * from t_goods")
    @ResultType(Goods.class)
    List<Goods> getGoodsList();
    //查询商品类型
    @Select("<script>" +
            "select * from t_goods" +
            "<where>" +
            "<if test='pubdate!=null and pubdate.length()!=0'>" +
            " pubdate=#{pubdate} " +
            "</if>" +
            "<if test='name!=null and name.length()!=0'>" +
            " and name like concat('%',#{name},'%') " +
            "</if>" +
            "</where>" +
            "</script>")
    List<Goods> getSearchGoods(@Param("pubdate") String pubdate, @Param("name") String name);
    //删除商品类型
    @Delete("delete from t_goods where id=#{id}")
    int deleteGoods(@Param("id") Integer id);
    //增加商品类型
    @Insert("insert into t_goods(name,price,pubdate,typeName,intro,picture,flag,star) " +
            " values(#{name},#{price},#{pubdate},#{typeName},#{intro},#{picture},1,#{star})")
    int addGoods(Goods goods);
    
    //根据ID获取商品详情
    @Select("select * from t_goods where id=#{id}")
    @ResultType(Goods.class)
    Goods getGoodsById(@Param("id") int id);
    
    // 更新商品信息
    @Update("update t_goods set name=#{name}, price=#{price}, typeName=#{typeName}, intro=#{intro}, flag=#{flag}, star=#{star} where id=#{id}")
    int updateGoods(Goods goods);
}
