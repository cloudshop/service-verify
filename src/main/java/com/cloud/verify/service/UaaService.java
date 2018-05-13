package com.cloud.verify.service;

import com.cloud.verify.client.AuthorizedFeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@AuthorizedFeignClient(name="uaa",decode404=true)
public interface UaaService {
	
	@GetMapping("/api/account")
	public UserDTO getAccount();
	
	@GetMapping("/api/users/{login}")
	public ResponseEntity<UserDTO> getUserByLogin(@PathVariable("login") String login);
	
}
