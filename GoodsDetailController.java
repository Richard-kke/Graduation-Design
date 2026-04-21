package com.qf.controller;

import com.qf.common.utils.LogUtils;
import com.qf.domain.Goods;
import com.qf.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GoodsDetailController {
    
    @Autowired
    private GoodsService goodsService;
    
    // 商品详情页
    @RequestMapping("goodsDetail")
    String showGoodsDetail(@RequestParam("id") int goodsId, Model model, HttpServletRequest request) {
        // 获取商品信息
        Goods goods = goodsService.getGoodsById(goodsId);
        model.addAttribute("goods", goods);
        
        // 记录用户查看商品日志
        if (goods != null) {
            LogUtils.logUserView(request, goodsId, goods.getName());
        }
        
        return "goodsDetail";
    }
    
    // 商品点击日志
    @RequestMapping("logGoodsClick")
    String logGoodsClick(@RequestParam("id") int goodsId, @RequestParam("name") String goodsName, HttpServletRequest request) {
        // 记录用户点击商品日志
        LogUtils.logUserClick(request, goodsId, goodsName);
        
        // 重定向到商品详情页
        return "redirect:goodsDetail?id=" + goodsId;
    }
}
