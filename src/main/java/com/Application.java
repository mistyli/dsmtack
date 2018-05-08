package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by misty on 2018/3/27.
 */
@SpringBootApplication(scanBasePackages = {"com"})
//@EnableScheduling
@MapperScan(basePackages="com.dao.mapper")
@EnableSwagger2
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
//    ApplicationContext app = SpringApplication.run(Application.class, args);
//    SpringContextUtil.setApplicationContext(app);

//        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
//        rootContext.register(RootContextConfiguration.class);
//        rootContext.refresh();
    }

}