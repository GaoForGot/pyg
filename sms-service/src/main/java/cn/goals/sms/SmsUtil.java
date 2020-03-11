package cn.goals.sms;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 短信工具类
 */
@Component
public class SmsUtil {

    @Autowired
    private Environment env;

    /**
     * 发送短信
     *
     * @param phoneNumber  电话号码
     * @param signName     短信签名
     * @param templateCode 短信模板号
     * @param param        短信模板参数(验证码)
     */
    public CommonResponse send(String phoneNumber, String signName, String templateCode, String param) throws ClientException {
        String accessKeyId = env.getProperty("alidayu.accessKeyId");
        String secret = env.getProperty("alidayu.secret");
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //电话号码
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        //签名
        request.putQueryParameter("SignName", signName);
        //模板号
        request.putQueryParameter("TemplateCode", templateCode);
        //模板参数(Json)
        request.putQueryParameter("TemplateParam", param);
        CommonResponse response = client.getCommonResponse(request);
        return response;

    }
}
