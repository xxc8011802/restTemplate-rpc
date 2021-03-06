package com.example.rpc.http.rest;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.example.register.register.ServiceRegistry;
import com.example.rpc.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * 存放接口和实现类的映射关系
 */
public class RestRpcServer implements ApplicationContextAware, InitializingBean,BeanNameAware,BeanFactoryAware
{
    private static ApplicationContext applicationContext;
    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private static Map<String, Object> handlerMap = new HashMap<>();

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    public RestRpcServer(){

    }

    //构造函数注入 服务地址
    public RestRpcServer(String serviceAddress){
        this.serviceAddress=serviceAddress;
    }

    //
    public RestRpcServer(String serviceAddress, ServiceRegistry serviceRegistry)
    {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    public void init(){
        System.out.println("init");
    }


    @Override
    public void setBeanName(String var1){
        System.out.println("setBeanName"+var1);
    }

    public void setBeanFactory(BeanFactory var1) throws BeansException{
        System.out.println("setBeanFactory"+var1);
    }

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

    /**
     * afterPropertiesSet 会在init-method初始化之前执行
     * 主要实现服务的注册
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception{
        System.out.println("afterPropertiesSet");
        // 获取 RPC 服务器的 IP 地址与端口号
        String[] addressArray = StrUtil.split(serviceAddress, ":");
        String ip = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        // 注册 RPC 服务地址
        if (serviceRegistry != null) {
            for (String interfaceName : handlerMap.keySet()) {
                serviceRegistry.init(interfaceName, serviceAddress);
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
