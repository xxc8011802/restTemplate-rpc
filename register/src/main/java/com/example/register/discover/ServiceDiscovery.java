package com.example.register.discover;

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
     * @param consistentHash 服务名称
     * @return 服务地址
     */
    String discover(String serviceName,String consistentHashKey);
}