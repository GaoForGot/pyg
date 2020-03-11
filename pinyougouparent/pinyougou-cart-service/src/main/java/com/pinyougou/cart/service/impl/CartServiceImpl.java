package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.grouppojo.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 添加商品(sku)到购物车
     *
     * @param cartList 当前用户的购物车列表
     * @param itemId   想要加入购物车的itemId(SKU)
     * @param num      想要加入的数量
     * @return
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //根据itemId, 获取商家id和商家名
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //如果商品不存在(被删除了)或不是上架状态, 则无法加入购物车
        if (item==null||!item.getStatus().equals("1")) {
            throw new RuntimeException("商品不存在或已下架");
        }

        //根据商家id, 获取购物车
        Cart cart = searchCartBySellerId(cartList, item.getSellerId());

        //判断: 购物车列表中是否已有该商家的购物车
        if (cart != null) {//购物车列表中有商家id购物车

            //根据itemId, 从订单列表中中获取订单对象
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = searchOrderItemListByItemId(orderItemList, itemId);

            //判断: 订单列表中是否已有当前传入的itemId
            if (orderItem != null) {//购物车中已有itemId, 更改数量和总价
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()).multiply(orderItem.getPrice()));
                if (orderItem.getNum() < 1) {//如果订单数量小于1, 则将该商品从订单列表中删除
                    orderItemList.remove(orderItem);
                    if (orderItemList.size() == 0) {//如果订单列表中没有订单, 则将该购物车从购物车列表中删除
                        cartList.remove(cart);
                    }
                }
            } else {//购物车中没有itemId 新建tbOrderItem对象, 加入订单列表
                orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
            }

        } else {//购物车列表中没有sellerId, 根据商家id和商家名, 创建新的cart对象, 并加入购物车列表
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSeller(item.getSeller());
            TbOrderItem orderItem = createOrderItem(item, num);
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }

        return cartList;
    }

    /**
     * 将购物车存入redis中已userName为键的map键值对中
     * @param userName
     * @param cartList
     */
    @Override
    public void addCartListToRedis(String userName, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartLists").put(userName, cartList);
    }

    /**
     * 根据username, 从redis中获取购物车列表
     * @param userName
     * @return
     */
    @Override
    public List<Cart> findRedisCartList(String userName) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartLists").get(userName);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 合并两个购物车列表
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList1) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                cartList2 = addItemToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList2;
    }

    /**
     * 根据商家id, 从购物车列表中查询购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据itemId, 从订单列表中查询订单
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 根据item对象和数量, 创建新的订单对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        //缺少:  主键id,  订单id
        TbOrderItem newOrderItem = new TbOrderItem();
        newOrderItem.setItemId(item.getId());
        newOrderItem.setGoodsId(item.getGoodsId());
        newOrderItem.setTitle(item.getTitle());
        newOrderItem.setPrice(item.getPrice());
        newOrderItem.setNum(num);
        newOrderItem.setTotalFee(new BigDecimal(num).multiply(item.getPrice()));
        newOrderItem.setPicPath(item.getImage());
        newOrderItem.setSellerId(item.getSellerId());
        return newOrderItem;
    }

}
