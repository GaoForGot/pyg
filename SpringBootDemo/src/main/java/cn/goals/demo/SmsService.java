package cn.goals.demo;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

public class SmsService {
    public static void main(String[] args) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4Fp27Kqa7sMGUvkKHBcL", "jHI6lv26zJNaqKWki9Qj7uz2aGXBpg");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", "18203599588");
        request.putQueryParameter("SignName", "高氏商城");
        request.putQueryParameter("TemplateCode", "SMS_184631044");
        request.putQueryParameter("TemplateParam", "{\"code\":\"817122\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
