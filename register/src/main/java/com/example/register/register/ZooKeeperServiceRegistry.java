package com.example.register.register;

import com.alibaba.fastjson.JSON;
import com.example.register.constant.Constant;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.example.register.constant.Constant.ZK_CONFIG_PATH;
import static com.example.register.constant.Constant.ZK_REGISTRY_PATH;

/**
 * 基于 ZooKeeper 的服务注册接口实现
 *
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private final ZkClient zkClient;

    //节点数据监听
    private IZkDataListener dataListener;

    /**
     * 创建zk连接
     * @param zkAddress zk地址
     */
    public ZooKeeperServiceRegistry(String zkAddress) {
        this.dataListener = new IZkDataListener()
        {
            /**
             * @param s  节点
             * @param o  节点值
             * @throws Exception
             */
            @Override
            public void handleDataChange(String s, Object o)
                throws Exception
            {
                System.out.println("node data change:[" + s + "]" + "[date:" + o + "]");
            }

            @Override
            public void handleDataDeleted(String s)
                throws Exception
            {
                System.out.println("node data deleted:["+s+"]");
            }
        };
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void init(String serviceName, String serviceAddress){
        register(serviceName, serviceAddress);
        //监听指定节点的数据变更
        String path = ZK_CONFIG_PATH +"/" + serviceName;
        zkClient.subscribeDataChanges(path, dataListener);
    }

    /**
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            //创建服务根目录节点
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }
        String addressPath = servicePath + "/" + serviceAddress;
        zkClient.createEphemeral(addressPath, serviceAddress);
        // 创建临时顺序节点 address（临时）,临时节点随着服务的断开则删除
        //String addressPath = servicePath + "/address-";
        //String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        //LOGGER.debug("create address node: {}", addressNode);
    }

    @Override
    public synchronized void  remove(String serviceName, String serviceAddress){

    }

    public static void main(String[] args)
    {
        //监听节点的动态变化,只能监听指定节点以及子节点的变化
        ZkClient zkc = new ZkClient(new ZkConnection("127.0.0.1:2181"), 5000);
        String path =ZK_REGISTRY_PATH + "/" + "com.example.api.bean.CompanyService";
        List<String> Nodes = zkc.subscribeChildChanges(path, new IZkChildListener()
        {
            //currentChilds 包含了当前服务下的所有节点信息
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds)
                throws Exception
            {
                System.out.println("[[-------------parentPath is" + parentPath + "------------------]]");
                System.out.println("[[-------------Child change ,currentChilds is" + currentChilds + "-------------]]");
            }
        });
        if(zkc.exists(ZK_REGISTRY_PATH )){
            System.out.println("根节点已存在");
        }else{
            zkc.createPersistent(ZK_REGISTRY_PATH);
        }
        if(zkc.exists(ZK_REGISTRY_PATH + "/"+"com.example.api.bean.UserService")){
            System.out.println("com.example.api.bean.UserService节点已存在");
        }else{
            zkc.createPersistent(ZK_REGISTRY_PATH+ "/"+"com.example.api.bean.UserService");
        }
        if(zkc.exists(ZK_REGISTRY_PATH + "/"+"com.example.api.bean.CompanyService")){
            System.out.println("com.example.api.bean.CompanyService");
        }else{
            zkc.createPersistent(ZK_REGISTRY_PATH+ "/"+"com.example.api.bean.CompanyService");
        }
        //zkc.createEphemeralSequential(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.UserService/address-", "localhost:9091");
        //zkc.createEphemeralSequential(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.UserService/address-", "localhost:9092");
        zkc.createEphemeral(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.CompanyService/address_9091", "localhost:9091");
        zkc.createEphemeral(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.CompanyService/address_9092", "localhost:9092");
        zkc.delete(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.CompanyService/address_9091");
        zkc.delete(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.CompanyService/address_9092");
        //zkc.deleteRecursive(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.UserService");
        //zkc.deleteRecursive(ZK_REGISTRY_PATH + "/" + "com.example.api.bean.CompanyService");
        //zkc.deleteRecursive(ZK_REGISTRY_PATH);
    }
}