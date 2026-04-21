package com.qf.service.impl;


import com.qf.dao.GoodsTypeDao;
import com.qf.domain.GoodsType;
import com.qf.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public  class GoodsTypeServiceImpl implements GoodsTypeService {

    @Autowired
    private GoodsTypeDao goodsTypeDao;

    @Override
    public List<GoodsType> getAllGoodType() {
        return goodsTypeDao.getAllGoodsType();
    }

    @Override
    public List<GoodsType> getSearchtype(String flag, String name) {
        return goodsTypeDao.getSearchtype(flag, name);
    }

    @Override
    public int deleteGoodsType(Integer id) {
        return goodsTypeDao.deleteGoodsType(id);
    }

    @Override
    public int addGoodsType(GoodsType goodsType) {
        return goodsTypeDao.addGoodsType(goodsType);
    }

}


