package com.pinyougou.cart.service;

import com.pinyougou.grouppojo.Cart;

import java.util.List;

public interface CartService {

    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    void addCartListToRedis(String userName, List<Cart> cartList);

    List<Cart> findRedisCartList(String userName);

    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);


}
