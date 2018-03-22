package com.cloud.verify.web.rest;

import com.cloud.verify.service.Sms.SmsServiceI;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sms")
public class SmsResource {
    private static final Logger LOGGER= LoggerFactory.getLogger(SmsResource.class);

    @Autowired
    SmsServiceI smsService;


    /*
    * 发送短信验证码
    * */
    @GetMapping("/initSmsCode")
    public ResponseEntity<Map> initSmsCode(@RequestParam("phone") String phone)throws Exception{
        if(StringUtils.isBlank(phone)){
                throw new Exception("电话号码为空");
        }
        Map result=smsService.initAndSendSmsCode(phone);
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    /*
    * 生成图形验证码
    * */
    @GetMapping("/initImageCode")
    public void initCharAndNumCode( HttpServletResponse response){
        //将ContentType设为"image/jpeg"，让浏览器识别图像格式。
        response.setContentType("image/jpeg");
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 2000);

        try{
            //获得验证码的图像数据
            BufferedImage bi = smsService.getImage();
            //获得Servlet输出流
            ServletOutputStream outStream = response.getOutputStream();
            //创建可用来将图像数据编码为JPEG数据流的编码器
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outStream);
            //将图像数据进行编码
            encoder.encode(bi);
            //强行将缓冲区的内容输入到页面
            outStream.flush();
            //关闭输出流
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
