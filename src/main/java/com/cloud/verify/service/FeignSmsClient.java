package com.cloud.verify.service;

import com.cloud.verify.client.AuthorizedFeignClient;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@AuthorizedFeignClient(name = "sms")
public interface FeignSmsClient {
	
    @PostMapping (value = "/api/messages")
    public Map SendSmsCode(@RequestBody Map param)throws Exception;
    
}
