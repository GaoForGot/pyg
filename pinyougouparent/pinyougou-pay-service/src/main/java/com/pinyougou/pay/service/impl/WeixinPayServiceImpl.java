package com.pinyougou.pay.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import utils.HttpClient;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${notifyurl}")
    private String notifyurl;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee){
        try {
            Map paramMap = new HashMap();
            paramMap.put("appid", appid);//公众号id
            paramMap.put("mch_id", partner);//商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            paramMap.put("body", "品优购");//商品描述
            paramMap.put("out_trade_no", out_trade_no);//商户内部订单号
            paramMap.put("total_fee", total_fee);//总价(分为单位)
            paramMap.put("spbill_create_ip", "127.0.0.1");//客户端ip, 符合ip格式即可
            paramMap.put("notify_url", notifyurl);//回调地址, 能访问即可
            paramMap.put("trade_type", "NATIVE");//交易类型, 扫码支付为native
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送xml: "+ paramXml);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.post();//发送预支付请求
            String xmlResponse = httpClient.getContent();//获取响应
            Map<String, String> resultXml = WXPayUtil.xmlToMap(xmlResponse);
            System.out.println("返回xml: " + resultXml);
            Map<String, String> resultMap = new HashMap<>();
            //提取返回结果中的必要信息
            resultMap.put("result_code", resultXml.get("result_code"));//业务结果 SUCCESS/FAIL
            resultMap.put("prepay_id", resultXml.get("prepay_id"));//预支付交易标识, 用于后续接口调用
            resultMap.put("code_url", resultXml.get("code_url"));//生成二维码所需连接
            resultMap.put("out_trade_no", out_trade_no);//订单号
            resultMap.put("total_fee", total_fee);//订单金额
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询订单支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no){
        try {
            Map paramMap = new HashMap();
            paramMap.put("appid",appid);
            paramMap.put("mch_id",partner);
            paramMap.put("out_trade_no",out_trade_no);
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            String resultXml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
