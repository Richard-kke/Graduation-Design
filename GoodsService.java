package com.qf.service;

import com.qf.domain.Goods;

import java.util.List;

public interface GoodsService {
    List<Goods> getGoodsList();

    List<Goods> getSearchGoods(String pubdate, String name);

    int deleteGoods(Integer id);
    int addGoods(Goods goods);
    
    // 根据ID获取商品详情
    Goods getGoodsById(int id);
    
    // 更新商品信息
    int updateGoods(Goods goods);

}
