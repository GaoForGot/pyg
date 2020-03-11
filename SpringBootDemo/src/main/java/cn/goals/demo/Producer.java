package cn.goals.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
public class Producer {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public void send(String msg) {
        jmsMessagingTemplate.convertAndSend("spring_boot_test", msg);
    }

    @RequestMapping("/sendMap")
    public void send() {
        Map map = new HashMap();
        map.put("phoneNumber", "18203599588");
        map.put("signName", "高氏商城");
        map.put("templateCode", "SMS_184631044");
        map.put("param", "{\"code\":\"666666\"}");
        jmsMessagingTemplate.convertAndSend("sms_service", map);
    }
}
