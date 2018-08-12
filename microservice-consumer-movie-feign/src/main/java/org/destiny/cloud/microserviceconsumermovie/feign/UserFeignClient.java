package org.destiny.cloud.microserviceconsumermovie.feign;

import org.destiny.cloud.microserviceconsumermovie.model.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * design by 2018/8/11 21:39
 *
 * @author destiny
 * @version JDK 1.8.0_101
 * @since JDK 1.8.0_101
 */
@FeignClient(name = "microservice-provider-user")
public interface UserFeignClient {

    /**
     * 根据 id 获取对应 User
     * @param id
     * @return
     */
    @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
    User findById(@PathVariable(value = "id") Long id);

    /**
     * 使用 Post 方法请求
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    User postUser(@RequestBody User user);
}
