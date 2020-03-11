package com.pinyougou.cart.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entities.Message;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weixinPay")
public class WeixinPayController {

    @Reference(timeout = 3000)
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 创建支付
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.queryPayLogByUsernameFromRedis(username);
        if (payLog != null) {//redis中有数据, 则创建支付
            Map resultMap = weixinPayService.createNative(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
            return resultMap;
        } else {//redis中没有数据就返回空map
            return new HashMap();
        }
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Message queryPayStatus(String out_trade_no) {
        Message message = null;
        int x = 0;
        while (true) {//开始轮询支付状态
            if (x > 100) {//支付超时
                message = new Message(false, "支付超时");
                break;
            }
            //调用查询状态服务
            Map<String, String> resultMap = weixinPayService.queryPayStatus(out_trade_no);
            if (resultMap == null) {//支付出错
                message = new Message(false, "支付发生错误");
                break;
            }
            if (resultMap.get("trade_state").equals("SUCCESS")) {//支付成功
                message = new Message(true, "支付成功");
                //支付成功后, 需要调用order服务, 更新paylog表和order表, 删除redis的paylog缓存
                //需传入支付ID和微信交易id
                orderService.updatePaymentStatus(out_trade_no, resultMap.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;//支付未完成也没有报错, 则等待3秒继续查询
        }
        return message;
    }

}
