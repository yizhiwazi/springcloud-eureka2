package com.hehe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient //开启客户端
@SpringBootApplication
@RestController //开启组件+JSON
public class HiProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiProviderApplication.class, args);
	}

	@RequestMapping("/world")
	public String helloworld (){
		return "远程调用成功: Hello World!!";
	}

}
