package com.qf.controller;

import com.qf.common.utils.FileUtils;
import com.qf.common.utils.LogUtils;
import com.qf.domain.Goods;
import com.qf.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.channels.MulticastChannel;

@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    //商品展示
    @RequestMapping("getGoodsList")
    String getAllGoods(Model model){
        model.addAttribute("goodsList",goodsService.getGoodsList());
        return "admin/showGoods";
    }
    //查询商品
    @RequestMapping("selectByNameAndPub")
    String getSearchGoods(String pubdate,String name,Model model) {
        model.addAttribute("goodsList",goodsService.getSearchGoods(pubdate,name));
        return "admin/showGoods";
    }
    //删除商品分类
    @RequestMapping("goodsDeleteById")
    String deleteGoods(Integer id){
        goodsService.deleteGoods(id);
        return "redirect:getGoodsList";
    }
    //增加商品分类页
    @RequestMapping("toAddGoods")
    String ShowAddGoodsPage(){
        return "addGoods";
    }
    //添加商品
    @RequestMapping ("addGoods")
    String addGoods (MultipartFile file, Goods goods, Model model) throws IOException {
        File dir = FileUtils.createDir("D:\\hadoop\\fengmiB2202\\src\\main\\webapp\\fmwimages");
        File destFile = new File (dir, FileUtils.createFileName(file.getOriginalFilename()));
        file.transferTo (destFile);
        goods.setPicture(dir.getName ()+"/"+destFile.getName ());
        int result = goodsService. addGoods (goods);
        model.addAttribute ( "goodsList", goodsService.getGoodsList());
        if (result>0) {
            return "admin/showGoods";
        } else {
            model.addAttribute( "msg","添加失败，请稍后再次尝试");
            return "admin/addGoods";
        }
    }
}
