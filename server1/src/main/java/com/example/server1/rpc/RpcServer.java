package com.example.server1.rpc;

import com.example.register.register.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RpcServer implements ApplicationContextAware
{
    private static ApplicationContext applicationContext;

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    //构造函数注入 服务地址
    public RpcServer(String serviceAddress){
        this.serviceAddress=serviceAddress;
    }

    //
    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry)
    {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx){
    }

}
