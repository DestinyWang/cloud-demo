package org.destiny.cloud.microserviceprovideruser.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.destiny.cloud.microserviceprovideruser.dao.UserRepository;
import org.destiny.cloud.microserviceprovideruser.model.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王康
 * hzwangkang1@corp.netease.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * Corpright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * @version JDK 1.8.0_101
 * @since 2018/7/29 19:41
 */
@RestController
public class UserController implements BeanNameAware, BeanFactoryAware, ApplicationContextAware {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EurekaClient eurekaClient;

    @Value("${server.port}")
    private String port;

    @GetMapping("/simple/{id}")
    public User findById(@PathVariable("id") Long id) {
        System.out.println("UserController.findById");
        return userRepository.getOne(id);
    }

    @GetMapping("/eureka/instance")
    public String serviceUrl() {
        InstanceInfo server = eurekaClient.getNextServerFromEureka("MICROSERVICE-PROVIDER-USER", false);
        return server.getHostName();
    }

    @GetMapping("/port")
    public String port() {
        return port;
    }

    @PostMapping("/user")
    public User postUser(@RequestBody User user) {
        System.out.println("user = [" + user + "]");
        return user;
    }


    @Override
    public void setBeanName(String name) {

    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
