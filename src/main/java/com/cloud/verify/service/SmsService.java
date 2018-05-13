package com.cloud.verify.service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cloud.verify.client.AuthorizedFeignClient;
import com.cloud.verify.domain.MessageDTO;

@AuthorizedFeignClient(name="sms")
public interface SmsService {

	@PostMapping("/api/messages")
	public MessageDTO sendMessage(@RequestBody MessageDTO messageDTO);
	
}
