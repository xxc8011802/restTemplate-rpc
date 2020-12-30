package com.example.server1.rpc;

import cn.hutool.core.map.MapUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 存放接口和实现类的映射关系
 */
@Component
public class RpcServiceMapper implements ApplicationContextAware
{

    private static ApplicationContext applicationContext;
    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private static Map<String, Object> handlerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext ctx)
    {
        // 扫描带有 RpcService 注解的类并初始化 handlerMap 对象
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtil.isNotEmpty(serviceBeanMap))
        {
            for (Object serviceBean : serviceBeanMap.values())
            {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                /*String serviceVersion = rpcService.version();
                if (StringUtil.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }*/
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    public static Map<String, Object> getMap(){
        return handlerMap;
    }
}
