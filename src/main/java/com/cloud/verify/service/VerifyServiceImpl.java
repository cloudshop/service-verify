package com.cloud.verify.service;

import com.cloud.verify.config.SmsConstents;
import com.cloud.verify.web.rest.util.VerifyCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class VerifyServiceImpl implements VerifyService {

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
    public Map smsValidate(String phone,String smsCode) throws Exception {
         String flag="failed";
         String content="";
         Object code=redisTemplate.boundValueOps("sms_"+phone).get();
         if (smsCode==null){
             content= SmsConstents.SMS_VALIDATE_TIMEOUT;
         }else {
             if (!smsCode.equals(code)){
                 content=SmsConstents.SMS_VALIDATE_WRONG;
             }else {
                 flag="success";
                 content=SmsConstents.SMS_VALIDATE_RIGHT;
             }
         }
         Map param=new HashMap();
         param.put("message",flag);
         param.put("content",content);
         return param;
    }

	@Override
	public String getVerifyCodeByPhone(String phone) {
		Object code=redisTemplate.boundValueOps("sms_"+phone).get();
		return code==null? "":code.toString();
	}

}
