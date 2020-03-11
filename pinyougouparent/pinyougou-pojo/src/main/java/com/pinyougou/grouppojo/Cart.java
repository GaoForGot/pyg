package com.pinyougou.grouppojo;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车类
 * 一个购物车包括属于某个商家的所有订单
 * 在一个用户的购物车列表中, 一个商家只有一个购物车
 */
public class Cart implements Serializable {

    private String sellerId;

    private String seller;

    private List<TbOrderItem> orderItemList;


    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

}
