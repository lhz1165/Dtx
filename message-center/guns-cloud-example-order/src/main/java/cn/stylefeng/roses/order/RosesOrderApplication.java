package cn.stylefeng.roses.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

/**
 * 订单服务
 *
 * @author stylefeng
 * @Date 2018/1/22 21:27
 */
@SpringBootApplication
@EnableFeignClients("cn.stylefeng.roses.order.modular.consumer")
@EnableDiscoveryClient
@EnableBinding({Source.class})
public class RosesOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RosesOrderApplication.class, args);
    }

}
