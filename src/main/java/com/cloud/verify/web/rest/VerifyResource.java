package com.cloud.verify.web.rest;

import com.cloud.verify.service.VerifyService;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Api("验证码微服务")
@RestController
@RequestMapping("/api")
public class VerifyResource {
    private static final Logger LOGGER= LoggerFactory.getLogger(VerifyResource.class);

    @Autowired
    VerifyService verifyService;

    @ApiOperation("发送短信验证码")
    @GetMapping("/verify/smscode")
    public ResponseEntity smsCode(@NotNull @RequestParam("phone") String phone/*,@RequestParam("callback") String jsonpCallback*/)throws Exception{
                String result=verifyService.initAndSendSmsCode(phone);
                return ResponseUtil.wrapOrNotFound(Optional.ofNullable(result));
           /* if(StringUtils.isNotBlank(jsonpCallback)){//处理jsonp跨域
           JSONObject jsonObject=new JSONObject();
            jsonObject.put("content",result.get("content"));
            jsonObject.put("target",result.get("target"));
                String jsonpData=jsonpCallback+"("+jsonObject+")";
                return ResponseEntity.ok()
                    .header("Access-Control-Allow-Origin","*")
                    .header("Content-Type","application/x-javascript;charset=UTF-8")
                    .body(jsonpData);
            }*/
    }

    @ApiOperation("生成图形验证码")
    @GetMapping("/verify/imagecode")
    public void charAndNumCode( HttpServletResponse response) {
        //将ContentType设为"image/jpeg"，让浏览器识别图像格式。
        response.setContentType("image/jpeg");
        //设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 2000);

        try {
            //获得验证码的图像数据
            BufferedImage bi = verifyService.getImage();
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
     * @param  {"phone":"","smsCode",""}
     * @return {"message":""success,"content":"验证码正确"}
     * */
    @ApiOperation("短信验证")
    @GetMapping("/verify/smsvalidate")
    public ResponseEntity<Map> smsValidate(@RequestParam("phone") String phone,@RequestParam("smsCode") String smsCode )throws Exception{
            Map result=verifyService.smsValidate(phone,smsCode);
            return new ResponseEntity<Map>(result,HttpStatus.OK);
    }

    /**
     * @author 逍遥子
     * @email 756898059@qq.com
     * @date 2018年3月30日
     * @version 1.0
     * @param phone
     * @return
     */
    @GetMapping("/verify/{phone}")
    public ResponseEntity<String> getVerifyCodeByPhone(@PathVariable String phone) {
    	String vc = verifyService.getVerifyCodeByPhone(phone);
    	return new ResponseEntity<String>(vc, null, HttpStatus.OK);
    }

}
