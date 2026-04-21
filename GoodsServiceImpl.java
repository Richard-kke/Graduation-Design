package com.qf.service.impl;


import com.qf.dao.GoodsDao;
import com.qf.domain.Goods;
import com.qf.service.GoodsService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String GOODS_LIST_KEY = "goods:list";
    private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail:";
    private static final long CACHE_EXPIRE_TIME = 3600; // 缓存过期时间（秒）

    @Override
    public List<Goods> getGoodsList() {
        // 尝试从Redis缓存获取
        List<Goods> goodsList = (List<Goods>) redisTemplate.opsForValue().get(GOODS_LIST_KEY);
        if (goodsList != null) {
            return goodsList;
        }
        
        // 缓存未命中，从数据库获取
        goodsList = goodsDao.getGoodsList();
        
        // 存入Redis缓存
        redisTemplate.opsForValue().set(GOODS_LIST_KEY, goodsList, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return goodsList;
    }

    @Override
    public List<Goods> getSearchGoods(String pubdate, String name) {
        // 搜索结果不缓存，直接从数据库获取
        return goodsDao.getSearchGoods(pubdate,name);
    }

    @Override
    public int deleteGoods(@Param("id") Integer id) {
        int result = goodsDao.deleteGoods(id);
        if (result > 0) {
            // 清除缓存
            redisTemplate.delete(GOODS_LIST_KEY);
            redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + id);
        }
        return result;
    }

    @Override
    public int addGoods(Goods goods) {
        int result = goodsDao.addGoods(goods);
        if (result > 0) {
            // 清除缓存
            redisTemplate.delete(GOODS_LIST_KEY);
        }
        return result;
    }

    @Override
    public Goods getGoodsById(int goodsId) {
        // 尝试从Redis缓存获取
        String key = GOODS_DETAIL_KEY_PREFIX + goodsId;
        Goods goods = (Goods) redisTemplate.opsForValue().get(key);
        if (goods != null) {
            return goods;
        }
        
        // 缓存未命中，从数据库获取
        goods = goodsDao.getGoodsById(goodsId);
        
        // 存入Redis缓存
        if (goods != null) {
            redisTemplate.opsForValue().set(key, goods, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return goods;
    }
    
    @Override
    public int updateGoods(Goods goods) {
        int result = goodsDao.updateGoods(goods);
        if (result > 0) {
            // 清除缓存
            redisTemplate.delete(GOODS_LIST_KEY);
            redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + goods.getId());
        }
        return result;
    }
}
