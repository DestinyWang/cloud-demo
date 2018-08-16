# 1. Spring Cloud 是什么

    一套基于 SpringBoot 的快速构建分布式系统的工具集
    
# 2. 服务提供者与服务发现者

名词 | 概念
---|---
服务提供者 | 服务的被调用方
服务消费者 | 服务的调用方


## 2.1 通过服务发现组件解决IP, 端口硬编码的问题
![](http://oetw0yrii.bkt.clouddn.com/18-7-30/73551814.jpg)
服务发现组件的核心功能:
1. 服务注册表
2. 服务注册/注销
3. 健康检查

## 2.2 Eureka

    Eureka 是 Netflix 开源的服务发现组件
    Spring Cloud 对 Eureka 提供了很好的支持
    
![](https://github.com/Netflix/eureka/raw/master/images/eureka_architecture.png)


名词 | 含义
---|---
Application Client | 服务消费者
Application Service | 服务提供者
Make Remote Call | 调用 RestFul 接口
us-east-1c, us-east-1d | zone, 都属于 us-east-1 这个 zone

- Eureka Client 在上线的时候, Eureka Server 会加新的节点信息添加进服务注册表, Eureka Server 默认每 30s 会进行一次心跳检查, 如果某个节点连续三次没有响应, Eureka Server 就会将其从服务注册表中注销;
- Eureka Server 的服务注册表使用复制的方式实现高可用;
- Eureka Client 保存着服务注册表的缓存, 即使所有 Eureka Server 都宕机, 依然可以通过自身的缓存完成服务发现



region 和 zone 的关系:
![](http://oetw0yrii.bkt.clouddn.com/18-7-30/76745885.jpg)

### 2.2.1 搭建 Eureka Server 环境
#### 添加依赖:
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

#### 在 SpringBootApplication 类上添加 Eureka 注解  
将该工程声明为一个 Eureka Server
```java
@SpringBootApplication
@EnableEurekaServer
public class MicroserviceDiscoveryEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceDiscoveryEurekaApplication.class, args);
	}
}
```

#### 在 application.yml 添加配置文件:
```yml
server:
  port: 8761
eureka:
  client:
    service-url:
      defauleZone: http://localhost:8761/eureka
    register-with-eureka: false
    fetch-registry: false
```
### 2.2.2 搭建 Eureka Client 环境
#### 添加依赖:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```
#### 在 application.yml 添加配置文件:
```yml
eureka:
  client:
    service-url:
      defauleZone: http://localhost:8761/eureka

  # 配置节点名称
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```
#### 在 SpringBootApplication 类上添加 Eureka 注解  
将该工程声明为一个 Eureka Client
```java
@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceProviderUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceProviderUserApplication.class, args);
	}
}
```

### 2.2.3 配置登录权限
#### 添加 security 依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### 在 application.yml 配置用户名密码
```yml
# eureka 账户&密码
security:
  user:
    name: user
    password: passowrd
  basic:
    enabled: true
```

#### 启动 Eureka Server 后, 在页面上需要输入账号密码
![](http://oetw0yrii.bkt.clouddn.com/18-7-31/1765164.jpg)

#### Eureka Client 的注册链接也要相应作出修改
```yml
eureka:
  client:
    service-url:
      defauleZone: http://user:password@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${server.port}
```

### 2.2.4 actuator
监控管理生产环境的模块

再次启动 Application 的时候, 就会发现启动日志中多了如下内容:

```
2018-08-01 21:54:58.157  INFO 39361 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2018-08-01 21:54:58.696  INFO 39361 --- [           main] s.w.s.m.m.a.RequestMappingHandlerAdapter : Looking for @ControllerAdvice: org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@74c79fa2: startup date [Wed Aug 01 21:54:55 CST 2018]; parent: org.springframework.context.annotation.AnnotationConfigApplicationContext@47eaca72
2018-08-01 21:54:58.778  INFO 39361 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/simple/{id}],methods=[GET]}" onto public org.destiny.cloud.microserviceprovideruser.model.User org.destiny.cloud.microserviceprovideruser.controller.UserController.findById(java.lang.Long)
2018-08-01 21:54:58.781  INFO 39361 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error]}" onto public org.springframework.http.ResponseEntity<java.util.Map<java.lang.String, java.lang.Object>> org.springframework.boot.autoconfigure.web.BasicErrorController.error(javax.servlet.http.HttpServletRequest)
2018-08-01 21:54:58.781  INFO 39361 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error],produces=[text/html]}" onto public org.springframework.web.servlet.ModelAndView org.springframework.boot.autoconfigure.web.BasicErrorController.errorHtml(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
2018-08-01 21:54:58.818  INFO 39361 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/webjars/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2018-08-01 21:54:58.818  INFO 39361 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2018-08-01 21:54:58.858  INFO 39361 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**/favicon.ico] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2018-08-01 21:54:59.445  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/refresh || /refresh.json],methods=[POST]}" onto public java.lang.Object org.springframework.cloud.endpoint.GenericPostableMvcEndpoint.invoke()
2018-08-01 21:54:59.445  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/features || /features.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.446  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/info || /info.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.447  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/trace || /trace.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.448  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/beans || /beans.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.448  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/env/{name:.*}],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EnvironmentMvcEndpoint.value(java.lang.String)
2018-08-01 21:54:59.448  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/env || /env.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.449  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/metrics/{name:.*}],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.MetricsMvcEndpoint.value(java.lang.String)
2018-08-01 21:54:59.449  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/metrics || /metrics.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.449  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/env],methods=[POST]}" onto public java.lang.Object org.springframework.cloud.context.environment.EnvironmentManagerMvcEndpoint.value(java.util.Map<java.lang.String, java.lang.String>)
2018-08-01 21:54:59.449  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/env/reset],methods=[POST]}" onto public java.util.Map<java.lang.String, java.lang.Object> org.springframework.cloud.context.environment.EnvironmentManagerMvcEndpoint.reset()
2018-08-01 21:54:59.451  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/mappings || /mappings.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.452  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/archaius || /archaius.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.452  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/health || /health.json],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.HealthMvcEndpoint.invoke(java.security.Principal)
2018-08-01 21:54:59.452  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/autoconfig || /autoconfig.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.453  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/configprops || /configprops.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.453  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/heapdump || /heapdump.json],methods=[GET],produces=[application/octet-stream]}" onto public void org.springframework.boot.actuate.endpoint.mvc.HeapdumpMvcEndpoint.invoke(boolean,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse) throws java.io.IOException,javax.servlet.ServletException
2018-08-01 21:54:59.454  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/resume || /resume.json],methods=[POST]}" onto public java.lang.Object org.springframework.cloud.endpoint.GenericPostableMvcEndpoint.invoke()
2018-08-01 21:54:59.454  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/restart || /restart.json],methods=[POST]}" onto public java.lang.Object org.springframework.cloud.context.restart.RestartMvcEndpoint.invoke()
2018-08-01 21:54:59.454  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/dump || /dump.json],methods=[GET],produces=[application/json]}" onto public java.lang.Object org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter.invoke()
2018-08-01 21:54:59.455  INFO 39361 --- [           main] o.s.b.a.e.mvc.EndpointHandlerMapping     : Mapped "{[/pause || /pause.json],methods=[POST]}" onto public java.lang.Object org.springframework.cloud.endpoint.GenericPostableMvcEndpoint.invoke()
```

访问其中一个:
![](http://oetw0yrii.bkt.clouddn.com/18-8-1/56937881.jpg)

## 2.3 Eureka Server 高可用
#### 1. 创建 `application-instance1.yml` ，作为 instance1 服务中心的配置，并将serviceUrl指向 instance2 和 instance3
```yml
spring:
  application:
    name: spring-cloud-eureka
  profiles: instance1
server:
  port: 8081
eureka:
  client:
    service-url:
      defaultZone: http://instance2:8082/eureka,http://instance3:8083/eureka
#    register-with-eureka: false       # 是否将自己注册到 Eureka Server
#    fetch-registry: false             # 是否从 Eureka Server 获取注册信息
  instance:
    hostname: instance1
```

#### 2. 创建 `application-instance2.yml` ，作为 instance2 服务中心的配置，并将serviceUrl指向 instance1 和 instance3
```yml
spring:
  application:
    name: spring-cloud-eureka
  profiles: instance2
server:
  port: 8082
eureka:
  client:
    service-url:
      defaultZone: http://instance1:8081/eureka,http://instance3:8083/eureka
#    register-with-eureka: false       # 是否将自己注册到 Eureka Server
#    fetch-registry: false             # 是否从 Eureka Server 获取注册信息
  instance:
    hostname: instance2
```

#### 3. 创建 `application-instance3.yml` ，作为 instance3 服务中心的配置，并将serviceUrl指向 instance1 和 instance2
```yml
spring:
  application:
    name: spring-cloud-eureka
  profiles: instance3
server:
  port: 8083
eureka:
  client:
    service-url:
      defaultZone: http://instance1:8081/eureka,http://instance2:8082/eureka
  instance:
    hostname: instance3
```

#### 4. host 转换
在`/etc/hosts`文件中加入如下配置


    127.0.0.1			instance1
    127.0.0.1			instance2
    127.0.0.1			instance3
    
#### 5. 依次执行下面命令

#打包

    mvn clean package
    
#### 分别以peer1和peeer2 配置信息启动eureka

    java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=instance1
    java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=instance2
    java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=instance3
    
#### 查看结果

![](http://oetw0yrii.bkt.clouddn.com/18-8-1/96762547.jpg)

# 3. Ribbon

实现负载均衡的主要方式:
1. 服务器端负载均衡: 使用 Nginx, 用 Nginx 完成反向代理;
2. 客户端负载均衡: 客户端获取所调用服务的 IP 和端口, 并自己完成负载均衡算法.

## 3.1 Ribbon 是什么

    Ribbon 工作时分为两步:
    第一步先选择 Eureka Server, 它优先选择在同一个 Zone 且负载较少的 Server;
    第二步再根据用户指定的策略, 在从 Server 取到的服务注册表中选择一个地址. 其中 Ribbon 提供了多种策略, 例如轮询, 随机, 根据响应时间加权等.

![](http://oetw0yrii.bkt.clouddn.com/18-8-4/57984752.jpg)

## 3.2 引入 Ribbon
#### 依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

#### 给 RestTemplate 添加`@LoadBalance`
```java
@SpringBootApplication
@EnableEurekaClient
public class MicroserviceConsumerMovieRibbonApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceConsumerMovieRibbonApplication.class, args);
	}
}
```

#### 发送 Http 请求是采用 `virtual IP`
```java
@RestController
public class MovieController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/movie/user/{id}")
    public User findById(@PathVariable("id") Long id) {
        return restTemplate.getForObject("http://microservice-provider-user/simple/" + id, User.class);
    }

}
```

#### 即可得到结果

![](http://oetw0yrii.bkt.clouddn.com/18-8-4/92527081.jpg)

在用户微服务下添加如下接口:

```java
@Value("${server.port}")
private String port;


@GetMapping("/movie/port")
public String getPort() {
    return restTemplate.getForObject("http://microservice-provider-user/port", String.class);
}
```

分别以 7901 和 7900 端口启动两个服务

然后使用 Ribbon 负载均衡的方式调用

![](http://oetw0yrii.bkt.clouddn.com/18-8-5/57652440.jpg)

## 3.3 定制 Ribbon
#### 添加配置类


```java
@Configuration
public class RibbonConfig {

    @Autowired
    private IClientConfig config;

    /**
     * 指定为随机规则
     * @param config
     * @return
     */
    @Bean
    public IRule iRule(IClientConfig config) {
        return new RandomRule();
    }
}
```

#### 添加 SpringBootApplication 注解
@Ribbon 的 `name` 属性, 需要配置为注册的服务的 `serviceId`
```java
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "microservice-provider-user", configuration = RibbonConfig.class)
public class MicroserviceConsumerMovieRibbonApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceConsumerMovieRibbonApplication.class, args);
	}
}
```

> RibbonConfig 添加了 @Configuration, 因此自身也是一个配置类, 但不要将它配置在主上下文中, 否则它将会被所有 RibbonClient 共享配置  
不应该放在 @SpringBootApplication 所扫描的路径中
    
    
![](http://oetw0yrii.bkt.clouddn.com/18-8-5/57178557.jpg)

再次记录 Ribbon 请求的 IP, 发现由轮询变为随机.

## 3.4 使用配置文件自定义 Ribbon

> This allows you to change behavior at start up time in different enviroments

> The supported properties are listed below and should be profixed by <client name>.ribbon  

- NFLoadBalancerClassName: should implement ILoadBalancer
- NFLoadBalancerRuleClassName: should implement IRule
- NFLoadBalancerPingClassName: should implement IPing
- NIWSServerListClassName: should implement ServerList
- NIWSServerListFilterClassName: should implement ServerListFilter

配置文件自定义 Ribbon 的方式比上文通过 Java 代码定义的方式优先级更高, 也高于默认配置

    优先级: 配置文件 > 默认配置 > Java 代码配置
    
#### 在 `application.yml` 中添加以下配置
代表对 `microservice-provider-user` 的 Ribbon 进行配置
```yml
microservice-provider-user:
  ribbon:
    # 负载均衡规则: 响应时间加权
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

![](http://oetw0yrii.bkt.clouddn.com/18-8-5/73859558.jpg)

## 3.4 脱离 Eureka 使用 Ribbon
### 3.4.1 如果本身就没有 Eureka 依赖
#### 添加依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
```
#### 修改 `application.yml`

```yml
stores:
  ribbon:
    listOfServers: 10.211.55.4:8080,10.211.55.5:8080
```

### 3.4.2 如果 classPath 下存在 Eureka
#### 修改 `application.yml`

```yml
ribbon:
  eureka:
    enable: false
```

# 4. 声明式的 REST Client `Feign`
## 4.1 使用 Feign
#### 引入依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

#### 启动类新增注解
```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ConsumerMovieFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerMovieFeignApplication.class, args);
	}
}
```

#### 创建接口
```java
@FeignClient("microservice-provider-user")
public interface UserFeignClient {

    @GetMapping("/simple/{id}")
    User findById(@PathVariable(name = "id") Long id);
    
}
```

此时启动 Feign 应用会报错:

    org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'movieController': Unsatisfied dependency expressed through field 'userFeignClient'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.destiny.cloud.microserviceconsumermovie.UserFeignClient': FactoryBean threw exception on object creation; nested exception is java.lang.IllegalStateException: Method findById not annotated with HTTP method type (ex. GET, POST)
	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:588) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:88) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.postProcessPropertyValues(AutowiredAnnotationBeanPostProcessor.java:366) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1225) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:552) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:483) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:306) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:230) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:302) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:197) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:759) ~[spring-beans-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:866) ~[spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:542) ~[spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.refresh(EmbeddedWebApplicationContext.java:122) ~[spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:761) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:371) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1186) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1175) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.destiny.cloud.microserviceconsumermovie.ConsumerMovieFeignApplication.main(ConsumerMovieFeignApplication.java:14) [classes/:na]
	
问题在于 
```java
@GetMapping("/simple/{id}")
```

Feign 不识别 @GetMapping, 改成

```java
@FeignClient("microservice-provider-user")
public interface UserFeignClient {

    @RequestMapping(value = "/simple/{id}", method = RequestMethod.GET)
    User findById(@PathVariable(name = "id") Long id);

}
```

后可以成功启动.

#### POST 请求
```java
/**
 * 使用 Post 方法请求
 * @param user
 * @return
 */
@RequestMapping(value = "/user", method = RequestMethod.POST)
User postUser(@RequestBody User user);
```

## 4.2 覆写 Feign 配置
来自 SpringCloud 官方文档:
> A central concept in SpringCloud's Feign support is that of the name client.  
SpringCloud 的 Feign 支持的一个核心概念就是命名客户端.

> Each feign client is part of an ensemble of components that work together to contact a remote server on demand, and the ensemble has a name that you give it as an application developer using @FeignClient annotation.  
每个 Feign 都是整体的一部分, 它们一起工作以按需联系远程服务器, 并且该整体具有一个名称, 开发人员可以使用 `@FeignClient` 为其命名

> SpringCloud creates a new ensemble as an ApplictionContext on demand of each named client using FeignClientsConfiguration. This contians(amongst other things) an Feign Decoder, a feign Encoder, and a feign Contract.  
SpringCloud 根据需要使用 `FeignConfiguration` 为每个命名的客户端创建一个新的整体作为 ApplicationContext, 这包含 Feign 解码器, feign 编码器 和 feign 契约.

简单来说, 就是通过 `@FeignClient` 可以创建一个子容器

#### 添加配置类

    和 Ribbon 配置类一样, 如果直接放在启动类能够扫描的路径, 将同时对所有 FeignClient 生效.
    如果需要指定配置关系, 需要放在单路的路径.

```java
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
```
#### FeignClient 引入配置文件
```java
@FeignClient(name = "microservice-provider-user", configuration = FeignConfiguration.class)
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
```

#### 此时会发现服务无法启动

    org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'movieController': Unsatisfied dependency expressed through field 'userFeignClient'; 
    nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.destiny.cloud.microserviceconsumermovie.feign.UserFeignClient': FactoryBean threw exception on object creation; 
    nested exception is java.lang.IllegalStateException: Method findById not annotated with HTTP method type (ex. GET, POST)
    
无法启动的原因是由于上文将 Feign 的契约由 SpringMVC 替换成了 Feign 缺省契约, 因此不再识别 SpringMVC 的契约.

#### 将 FeignClient 契约替换成默认注解
```java
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
```

#### 请求成功
![](http://oetw0yrii.bkt.clouddn.com/18-8-12/81898817.jpg)

## 4.3 覆写 Feign 配置文件

#### 修改配置类以设置日志级别
```java
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

    /**
     * 为请求统一添加用户名密码
     * @return
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("user", "password");
    }

    /**
     * 设置 feign 日志级别
     * @return
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}

```

## 4.5 解决 Feign 第一次请求 timeout 的问题
```
# 设置超时时间
hystrix:command.default.execution.isolation.thread.timeoutInMilliseconds: 5000
# 禁用超时时间
hystrix.command.default.execution.timeout.enable: false
# 直接禁用 hystrix
hystrix:
  metrics:
    enabled: false
```

# 5. 深入理解 Eureka
## 5.1 元数据
- 主机名
- IP 地址
- 端口
- 状态页
- 健康检查

等元数据都会发布在服务注册表, 并由客户端以直接的方式联系服务.

其他原数据可以添加到 `eureka.instance.metadatamap` 中的实例注册, 并且可以在远程客户端中访问, 除非 Eureka 知道元数据的含义, 否则不会更改客户端的行为

#### 改变 instanceId
```
eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}

```

#### 自定义元数据
修改配置
```
eureka:
  client:
    service-url:
      defauleZone: http://user:password@localhost:8761/eureka
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ipAddress}:${spring.application.instance_id:${server.port}}
    metadata-map:
      zone: ABC     # eureka.instance.metadata-map.zone 是 Eureka 自身可识别的配置
      name: destiny
```

登录 `http://localhost:8761/eureka/apps/microservice-provider-user` 即可看到:

![](http://oetw0yrii.bkt.clouddn.com/18-8-16/55822718.jpg)

# 6. Hystrix
在微服务架构中通常会有多个服务层调用, 大量微服务通过网络进行通信, 从而支撑起整个系统.

各个微服务之间也难免存在大量的依赖关系, 然而任何服务都不是 100% 可用的, 网络也往往是脆弱的, 所以难免有一些请求会失败, 基础服务的故障导致级联故障, 进而导致整个系统不可用, 这种现象称为雪崩效应.

雪崩效应的描述是一种因服务提供者的不可用导致服务消费者不可用, 并将不可用逐渐放大的效应.

解决方案:
- 超时机制: 通过网络请求其他服务的时候, 都必须设置超时时间, 正常情况下, 一个远程调用在十几毫秒内就返回了, 当依赖的服务有问题, 响应时间变得很长, 对调用方而言, 就会有大量的进程/线程被挂起, 消耗大量资源.
- 断路器: 当依赖的服务有大量超时的时候, 再去让新的请求访问已经没有意义, 只会无谓的消耗资源. 断路器可以实现快速失败, 如果在一段时间内侦测到许多类似的错误, 就会强迫其后的请求快速失败, 断路器也可以使应用程序能够诊断错误是否已经修正, 如果已经修正, 应用程序会再次尝试调用.

![](http://oetw0yrii.bkt.clouddn.com/18-8-16/37109863.jpg)

## 6.1 引入 Hystrix
