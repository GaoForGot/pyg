package cn.goals.sms;

import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信发送服务监听类
 */
@Component
public class SmsService {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms_service")
    public void send(Map<String,String> map){
        try {
            CommonResponse response = smsUtil.send(map.get("phone"),
                    map.get("signName"),
                    map.get("templateCode"),
                    map.get("param"));
            System.out.println("短信服务回执信息: "+response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
