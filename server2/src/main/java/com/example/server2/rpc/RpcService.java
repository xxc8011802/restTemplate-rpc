package com.example.server2.rpc;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供给服务端实现类的注解，使得rpc调用中可以根据接口名获取对应的实现类
 * Target 表示注解的作用目标 TYPE表示对接口，类，枚举进行注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService
{
    /**
     * 服务接口类
     */
    Class<?> value();

    /**
     * 服务版本号，如果有同名的接口实现，可通过version区分属性
     */
    String version() default "";

}
