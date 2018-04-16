package com.cloud.verify.service;

import com.cloud.verify.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@AuthorizedFeignClient(name="uaa")
public interface UaaService {
	@GetMapping("/api/account")
	public UserDTO getAccount();
}
