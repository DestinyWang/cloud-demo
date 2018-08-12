package org.destiny.cloud.microserviceconsumermovie.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.destiny.cloud.microserviceconsumermovie.feign.UserFeignClient;
import org.destiny.cloud.microserviceconsumermovie.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * design by 2018/8/11 21:42
 *
 * @author destiny
 * @version JDK 1.8.0_101
 * @since JDK 1.8.0_101
 */
@RestController
public class MovieController {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private EurekaClient eurekaClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userFeignClient.findById(id);
    }

    @GetMapping("/movie/health")
    public String health() {
        InstanceInfo server = eurekaClient.getNextServerFromEureka("microservice-provider-user", false);
        return server.getHostName() + ": " + server.getPort();
    }
}
