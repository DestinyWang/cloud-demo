package org.destiny.cloud.microserviceconsumermovie.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * design by 2018/8/12 22:11
 *
 * @author destiny
 * @version JDK 1.8.0_101
 * @since JDK 1.8.0_101
 */
@FeignClient(name = "default", url = "http://localhost:8761")
public interface TestFeignClient {

    /**
     * 根据服务名查询服务详情
     * @param serviceName
     * @return
     */
    @RequestMapping(value = "/eureka/apps/{serviceName}")
    String findByServiceName(@PathVariable("serviceName") String serviceName);
}
