package com.qf.controller;


import com.qf.domain.GoodsType;
import com.qf.service.GoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GoodsTypeController {
    @Autowired
    private GoodsTypeService goodsTypeService;

    //商品类型列表
    @RequestMapping("getGoodsType")
    String getAllGoodsType(Model model){
        model.addAttribute("gtlist",goodsTypeService.getAllGoodType());
        return "admin/showGoodsType";
    }
    //查询商品分类
    @RequestMapping("selectByNameAndFlag")
    String getSearchtype(String flag,String name,Model model) {
        model.addAttribute("gtlist",goodsTypeService.getSearchtype(flag,name));
        return "admin/showGoodsType";
    }
    //删除商品分类
    @RequestMapping("deleteGoodsType")
    String deleteGoodsType(Integer count){
        goodsTypeService.deleteGoodsType(count);
        return "admin/showGoodsType";
    }
    //增加商品分类页
    @RequestMapping("goodstypeshowadd")
    String ShowAddGoodsType(GoodsType goodsType, Model model){
        model.addAttribute ( "gtlist", goodsTypeService.getAllGoodType());
        return "/addGoodsType";
    }
    //添加商品分类
    @RequestMapping ("goodstypeadd")
    String addGoodsType (GoodsType goodsType, Model model){
        int result = goodsTypeService.addGoodsType (goodsType);
        model.addAttribute ( "gtlist", goodsTypeService.getAllGoodType());
        if (result>0) {
            return "admin/showGoodsType";
        } else {
            model.addAttribute( "msg","添加失败，请稍后再次尝试");
            return "admin/addGoodsType";
        }
    }
}
