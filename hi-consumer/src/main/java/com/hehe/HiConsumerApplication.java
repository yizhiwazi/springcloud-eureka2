package com.hehe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient //开启客户端
@SpringBootApplication
@Controller //开启Component
public class HiConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HiConsumerApplication.class, args);
    }

    //提供REST客户端
    @Bean
    @LoadBalanced //负载均衡 作用：通过Ribbon拦截并调用在Eureka注册的服务
    public RestTemplate rest() {
        return new RestTemplate();
    }

    @Autowired
    RestTemplate restTemplate; //注入REST客户端

    @GetMapping("/")
    public String index() {
        return "redirect:/hello";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        //通过REST客户端远程连接提供方服务并完成JSON返回前台。 PS：hehe-service为服务方的注册名
        return restTemplate.getForEntity("http://hehe-service/world", String.class).getBody();
    }

    /**
     *   上面完成了服务与消费的示例  有兴趣深入的可以继续看看下面的分析
     *
     *   思考:敲完整个DEMO之后，是不是觉得@LoadBlanced注解很神奇，URL并没有使用IP地址+端口号的形式来调用服务，
     *   而是采用了服务名的方式组成，那么这样的请求为什么可以调用成功呢？因为Spring Cloud Ribbon
     *   帮我们做了这件事，它附带的拦截器在进行远程服务调用的时候，自动的去选取服务实例，并将实际要请求的IP地址和端口替换这里的服务名，从而完成服务接口的调用。
     *
     *    其实这里已经蕴含了Ribbon依赖 无需再单独导入了 还是很贴心的嘛
          <dependency>
            <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-eureka</artifactId>
          </dependency>
     *
     */

    /**
     *  思考解读：
     * @LoadBlanced  模拟实现功能
     *
     * 访问地址：localhost:9001/hello2  可以看到调用服务成功！
     */

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @GetMapping("/hello2")
    @ResponseBody
    public String hello2() {
        return handler("http://hehe-service/world");
    }

    //模拟拦截调用
    public String handler(String url) {
        // 省略URL解析步骤..
        String serviceId ="hehe-service"; //根据URL获取服务ID
        String servicePath="world"; //根据URL获取服务请求路径名
        //开始进行远程调用
        ServiceInstance serviceInstance = loadBalancerClient.choose(serviceId);//1.获取服务实例
        String serviceUrl="http://"+serviceInstance.getHost()+":"+serviceInstance.getPort()+"/"+servicePath;//2.获取服务主机和端口
        String result =new RestTemplate().getForEntity(serviceUrl, String.class).getBody();//3.远程调用服务
        return result;
    }



    /**
     *   思考进阶:
     更细心的童鞋会发现，每次都注入REST客户端并加入@LoadBlanced注解很麻烦呀，能不能帮我把这步都干了？

     敲把你能的，这是懒惰到家了啊。。不过 这个还真有

     接下来来介绍远程调用神器Spring Cloud Feign
     */


}
