package cn.goals.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping("/findUserName")
    public void findUser() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前认证用户名"+name);
    }
}
