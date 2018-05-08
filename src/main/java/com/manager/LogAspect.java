package com.manager;

import com.util.DateUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by misty on 2018/3/26.
 * 添加aop配置日志
 */
@Aspect
@Component
public class LogAspect {

    Logger log = LoggerFactory.getLogger(LogAspect.class);

//    @Pointcut("execution(public * com.util.*.work(..)) && args(type_code,table_name, field_name)")
//    public void method(String type_code, String table_name, String field_name){}
//
//    @Before("method(type_code,table_name, field_name)")
//    public void beforeWork(String type_code, String table_name, String field_name){
//        log.info(DateUtil.getDateSecNow() + "开始执行表" + table_name + "操作");
//    }
//
//    @After("method(type_code,table_name, field_name)")
//    public void afterWork(String type_code, String table_name, String field_name){
//        log.info(DateUtil.getDateSecNow() + "执行表" + table_name + "操作结束");
//    }
//
//    @AfterThrowing(pointcut = "method(type_code,table_name, field_name)", throwing = "e")
//    public void afterThrowing(String type_code, String table_name, String field_name, Throwable e){
//        log.info("执行表" + table_name + "操作抛出异常--" + e);
//    }

    @Pointcut("execution(* com.*.*.*(..))")
    public void method(){}

    @Before("method()")
    public void beforeWork(JoinPoint point){
        log.info(DateUtil.getDateSecNow() + "--start working,the target method is--" + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
    }

    @After("method()")
    public void afterWork(JoinPoint point){
        log.info(DateUtil.getDateSecNow() + "--end working--" + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
    }

    @AfterThrowing(pointcut = "method()", throwing = "e")
    public void afterThrowing(Throwable e){
        log.info("--exception happened--" + e);
    }

}
