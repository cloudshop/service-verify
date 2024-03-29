package com.cloud.verify.service;

import com.cloud.verify.config.SmsConstents;
import com.cloud.verify.web.rest.errors.BadRequestAlertException;
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
    Integer expiresSecond=120;
    public String smsCodeRegiste(String key,String phone)throws Exception{
            Object code=redisTemplate.boundValueOps(key).get();
            if (code!=null){
                throw new BadRequestAlertException("验证码已发送","verifyCode","verifyCodeAllReadySend");
            }
            String  smsCode=String.valueOf((int)((Math.random()*9+1)*100000));//六位数字短信验证码
            Map param=new HashMap();
            String message=String.format("尊敬的会员：您的手机验证码为：%s，工作人员不会索取，请勿泄露。",smsCode);
            param.put("content",message);
            param.put("target",phone);
            feignSmsClient.SendSmsCode(param);
            redisTemplate.boundValueOps(key).set(smsCode,expiresSecond.intValue(), TimeUnit.SECONDS);//验证码2分钟超时
            return "success";
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
    public Map smsValidate(String phone,/*String type,*/String smsCode) throws Exception {
         String content="";
         String flag="failed";
         String key="gongrong_verify_register_code_{"+phone+"}";
         //String key="gongrong_verify_"+type+"_code_{"+phone+"}";
         Object code=redisTemplate.boundValueOps(key).get();
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
	public String getVerifyCode(String key) {
		Object code = redisTemplate.opsForValue().get(key);
		//Object code=redisTemplate.boundValueOps("sms_"+phone).get();
		return code==null? "":code.toString();
	}

}
