package org.destiny.cloud.config;

import feign.Contract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * design by 2018/8/12 21:25
 *
 * @author destiny
 * @version JDK 1.8.0_101
 * @since JDK 1.8.0_101
 */
@Configuration
public class FeignConfiguration {

    /**
     * 将默认的 SpringMVC 契约替换成 Feign 缺省契约
     * @return
     */
    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }

}
