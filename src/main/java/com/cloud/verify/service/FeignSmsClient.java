package com.cloud.verify.service;

import com.cloud.verify.config.FeignConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Map;

@FeignClient(value = "sms",configuration = FeignConfiguration.class)
public interface FeignSmsClient {
    @RequestMapping (value = "/api/messages",method = RequestMethod.POST)
    public Map SendSmsCode(@RequestBody Map param)throws Exception;
}
