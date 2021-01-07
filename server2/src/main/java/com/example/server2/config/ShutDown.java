package com.example.server2.config;

import com.example.register.register.ServiceRegistry;

import javax.annotation.PreDestroy;

/**
 * POST 请求 http://localhost:9090/actuator/shutdown
 * 客户端服务下线主动关闭zk连接
 */
public class ShutDown
{
    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    public ShutDown()
    {
    }

    public ShutDown(String serviceAddress, ServiceRegistry serviceRegistry)
    {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @PreDestroy
    public void destroy(){
        System.out.println("服务下线");

        System.out.println(serviceAddress);
        System.out.println(serviceRegistry);
        /**
         * 查看当前服务的zk父节点目录下是否还有对应的其余服务节点
         */
/*        try {
            if (zk == null) {
                System.out.println("zk is null");
                return;
            }
            zk.close();
            System.out.println("服务下线- 关闭zk连接成功");
        } catch (InterruptedException e) {
            System.out.println("服务下线- 关闭zk连接异常");
            e.printStackTrace();
        }*/
    }
}
