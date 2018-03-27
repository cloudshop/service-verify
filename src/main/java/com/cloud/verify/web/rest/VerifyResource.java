package com.cloud.verify.web.rest;

import com.cloud.verify.service.Sms.SmsServiceI;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;


import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VerifyResource {
    private static final Logger LOGGER= LoggerFactory.getLogger(VerifyResource.class);

    @Autowired
    SmsServiceI smsService;

    /*
    * 发送短信验证码
    * */
    @RequestMapping(value = "/smsCode",method = RequestMethod.GET)
    public ResponseEntity<Object> smsCode(@RequestParam("phone") String phone,@RequestParam("callback") String jsonpCallback)throws Exception{
            if(StringUtils.isBlank(phone)){
                throw new Exception("电话号码为空");
            }
            Map result=smsService.initAndSendSmsCode(phone);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("content",result.get("content"));
            jsonObject.put("target",result.get("target"));
            if(StringUtils.isNotBlank(jsonpCallback)){//处理jsonp跨域
                String jsonpData=jsonpCallback+"("+jsonObject+")";
                return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin","*")
                    .header("Content-Type","application/x-javascript;charset=UTF-8")
                    .body(jsonpData);
            }
            return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    /*
    * 生成图形验证码
    * */
    @RequestMapping(value = "/imageCode",method = RequestMethod.GET)
    public void charAndNumCode( HttpServletResponse response) {
        //将ContentType设为"image/jpeg"，让浏览器识别图像格式。
        response.setContentType("image/jpeg");
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 2000);

        try {
            //获得验证码的图像数据
            BufferedImage bi = smsService.getImage();
            //获得Servlet输出流
            ServletOutputStream outStream = response.getOutputStream();
            ImageIO.write(bi, "jpeg", outStream);
            //强行将缓冲区的内容输入到页面
            outStream.flush();
            //关闭输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**短信验证
     * @param param {"phone":"","smsCode",""}
     * @return {"message":""success,"content":"验证码正确"}
     * */
    @RequestMapping(value = "/smsValidate",method = RequestMethod.POST)
    public ResponseEntity<Object> smsValidate(@NotNull @RequestBody Map param)throws Exception{
            Map result=smsService.smsValidate(param);
            return new ResponseEntity<Object>(result,HttpStatus.OK);
    }
}
