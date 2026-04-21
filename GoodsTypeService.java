package com.qf.service;

import com.qf.domain.GoodsType;
import com.qf.domain.User;

import java.util.List;

public interface GoodsTypeService {

    List<GoodsType> getAllGoodType();
    List<GoodsType> getSearchtype(String flag, String name);
    int deleteGoodsType(Integer id);
    int addGoodsType(GoodsType goodsType);

}