
package com.qf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    // 根路径映射
    @RequestMapping("/")
    public String home() {
        return "index";
    }

    // 上下文路径映射
    @RequestMapping("/fengmi")
    public String fengmi() {
        return "index";
    }
}
