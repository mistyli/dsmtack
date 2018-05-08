package com.manager;

import com.util.DateUtil;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by misty on 2018/4/4.
 */
@Aspect
@Component
public class MultiDataLogAspect {

    Logger log = LoggerFactory.getLogger(LogAspect.class);

//    @Pointcut("execution(public * com.jobs.*.workStatistic(..)) && args(type_code,table_name, field_name)")
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

}
