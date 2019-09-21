package com.goals.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.goals.service.IService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/aaa")
@Controller
public class MyController {

    @Reference
    private IService service;

    @RequestMapping(value= "/bbb", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String findName() {
        String name = service.getName();
        System.out.println(name);
        return name;
    }

}
