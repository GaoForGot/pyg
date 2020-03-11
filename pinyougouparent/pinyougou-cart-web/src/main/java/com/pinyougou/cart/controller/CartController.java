package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.grouppojo.Cart;
import entities.CartListMessage;
import entities.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 6000)
    private CartService cartService;

    /**
     * 获取购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public CartListMessage findCartList() {

        //获取当前用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户: " + userName);

        //获取cookie购物车
        //不获取cookid购物车, 就不知道用户是否在未登录状态下添加过商品, 所以每次都要获取cookie购物车
        String cartJson = CookieUtil.getCookieValue(request, "cart", "UTF-8");

        if (cartJson==null||cartJson.equals("")) {//如果购物车列表为空, 则给一个表示list的空值, 防止空指针异常
            cartJson = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartJson, Cart.class);


        String message = "购物车已成功获取";
        boolean isMergeSucced = true;
        //如果当前用户未登录, 则直接返回cookie购物车
        //如果当前用户已登录, 且cookie购物车里有东西, 就合并购物车, 否则直接返回redis购物车, 并删除cookie购物车
        if (userName.equals("anonymousUser")) {//如果当前是匿名用户

            return new CartListMessage(isMergeSucced, message, cartList_cookie);

        } else {//如果当前是登录用户
            List<Cart> cartList_redis = cartService.findRedisCartList(userName);
            if (cartList_cookie.size() > 0) {//如果cookie购物车不为空
                //将cookie购物车与redis购物车合并, 并存入redis
                try {
                    cartList_redis = cartService.mergeCartList(cartList_cookie, cartList_redis);
                } catch (Exception e) {
                    e.printStackTrace();
                    isMergeSucced = false;
                    message = "购物车合并失败, 商品不存在或已下架";
                }
                cartService.addCartListToRedis(userName, cartList_redis);
                //将cookie购物车删除
                CookieUtil.deleteCookie(request, response, "cart");
            }
            return new CartListMessage(isMergeSucced, message, cartList_redis);
        }


    }

    /**
     * 添加商品(SKU)到购物车
     * @param itemId 商品ID
     * @param num 要添加的数量
     */
    @RequestMapping("/addToCart")
    @CrossOrigin(allowedHeaders = "http://localhost:9105", allowCredentials = "true")//等同于下面那两句, allowCredentials默认为true, 可以省略
    public Message addToCart(Long itemId, Integer num) {
        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");//允许跨域访问的来源地址(不是本服务器地址), 可以用*表示允许所有
        response.setHeader("Access-Control-Allow-Credentials", "true");//如需用cookie, 则必须有这句, 但是上面不可以用*
        try {
            //从cookie或redis中取出购物车
            List<Cart> cartList = findCartList().getCartList();
            //调用服务, 加入购物车
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();

            if (userName.equals("anonymousUser")) {//如果用户未登录
                //将购物车放入cookie
                String cartJson = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request, response, "cart", cartJson, 3600 * 24, "UTF-8");
            } else {//如果用户已登录
                //将购物车添加到redis
                cartService.addCartListToRedis(userName, cartList);
            }

            return new Message(true, "添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(false, "添加购物车失败, 商品不存在或已下架");
        }
    }

}
