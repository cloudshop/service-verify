package com.cloud.verify.service.Sms;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface SmsServiceI {

    /**
     * 发送给手机验证码并保存
     * @return 生成的图像数据
     * */
    public Map initAndSendSmsCode(String phone)throws Exception;

    /**
     * 生成指定字符串的图像数据
     * @return 生成的图像数据
     * */
    public BufferedImage getImage();
}
