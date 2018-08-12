package org.destiny.cloud.microserviceconsumermovie.feign;

import feign.Param;
import feign.RequestLine;
import org.destiny.cloud.config.FeignConfiguration;
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
@FeignClient(name = "microservice-provider-user", configuration = FeignConfiguration.class)
public interface UserFeignClient {

    /**
     * 根据 id 获取对应 User
     * @param id
     * @return
     */
//    @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
//    User findById(@PathVariable(value = "id") Long id);
    @RequestLine(value = "GET /simple/{id}")
    User findById(@Param("id") Long id);

}
