package com.travelai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器，处理根路径重定向
 */
@Controller
@RequestMapping
public class IndexController {

    /**
     * 处理根路径请求，重定向到登录页面
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }
}