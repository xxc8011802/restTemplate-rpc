package com.example.register.discover;

import java.util.List;
import java.util.Set;

/**
 * 服务发现接口
 *
 */
public interface ServiceDiscovery
{



    /**
     * 根据服务名称查找服务地址
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    String discover(String serviceName);

    /**
     * 根据服务名称查找服务地址
     * 一致性hash使用
     *
     * @param serviceName 服务名称
     * @param consistentHashKey 服务名称
     * @return 服务地址
     */
    String discover(String serviceName,String consistentHashKey);

    /**
     * 查询服务下的节点
     * @param serviceName 服务名称
     * @return
     */
    List<String> getNodeByServiceName(String serviceName);

    /**
     * 列出所有服务
     * @return
     */
    Set<String> listService();

}