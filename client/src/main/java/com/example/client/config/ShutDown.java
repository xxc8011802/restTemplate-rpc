package com.example.client.config;

import org.apache.zookeeper.ZooKeeper;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * POST 请求 http://localhost:9090/actuator/shutdown
 * 客户端服务下线主动关闭zk连接
 */
public class ShutDown
{
   /* @Resource
    private ZooKeeper zk;*/

    @PreDestroy
    public void destroy(){
        System.out.println("客户端下线");
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
