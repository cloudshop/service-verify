package com.cloud.verify.service;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface VerifyService {

    /**
     * 发送给手机验证码并保存
     * */
    public String smsCodeRegiste(String key,String phone)throws Exception;

    /**
     * 生成指定字符串的图像数据
     * @return 生成的图像数据
     * */
    public BufferedImage getImage();

    public Map smsValidate(String phone, String type,String smsCode)throws Exception;

	public String getVerifyCode(String key);

}
