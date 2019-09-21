package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("经过UserDetailsService");
        SimpleGrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_SELLER");
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(authority1);
        TbSeller seller = sellerService.findOne(s);
        String status = seller.getStatus();
        if (seller != null) {
            return new User(s, seller.getPassword(), status.equals("1"),true,true,true,authorityList);
        }
        return null;
    }
}
