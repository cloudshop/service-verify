package com.cloud.verify.service.Sms;

import com.cloud.verify.config.SmsConstents;
import com.cloud.verify.web.rest.util.VerifyCodeUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsServiceI{

    @Autowired
    FeignSmsClient feignSmsClient;
    @Autowired
    VerifyCodeUtil verifyCodeUtil;
    @Autowired
    RedisTemplate redisTemplate;
    Integer expiresSecond=300;

    public Map initAndSendSmsCode(String phone)throws Exception{
        String  smsCode=String.valueOf((int)((Math.random()*9+1)*100000));//六位数字短信验证码
        Map param=new HashMap();
        param.put("content",smsCode);
        param.put("target",phone);
        String key="sms_"+phone;
        Map result=feignSmsClient.SendSmsCode(param);
        redisTemplate.boundValueOps(key).set(smsCode,expiresSecond.intValue(), TimeUnit.SECONDS);//验证码5分钟超时
        return param;
    }

    /**
     * 生成指定字符串的图像数据
     * @return 生成的图像数据
     * */
     public BufferedImage getImage(){
         //生成随机数字和字母,长度可定制
         String verifyCode=verifyCodeUtil.getRandString();
         //生成的图像数据
         return verifyCodeUtil.getImage(verifyCode);
     }

    @Override
    public Map smsValidate(Map param) throws Exception {
         String flag="failed";
         String content="";
         Object smsCode=redisTemplate.boundValueOps("sms_"+param.get("phone")).get();
         String currentSmsCode=param.get("smsCode").toString();
         param.clear();
         if (smsCode==null){
             content= SmsConstents.SMS_VALIDATE_TIMEOUT;
         }else {
             if (!smsCode.equals(currentSmsCode)){
                 content=SmsConstents.SMS_VALIDATE_WRONG;
             }else {
                 flag="success";
                 content=SmsConstents.SMS_VALIDATE_RIGHT;
             }
         }
         param.put("message",flag);
         param.put("content",content);
         return param;
    }

}
