package com.pinyougou.user.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {
    //回调此方法时, cas已经对用户已经完成了认证
    //在实际业务中, 可能需要对权限进行查询
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        //实际业务中, 这里的权限可能是从数据库中获取的
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //此处认证服务已交给cas完成, 所以密码为空即可
        System.out.println(username);
        return new User(username, "", authorities);
    }
}
