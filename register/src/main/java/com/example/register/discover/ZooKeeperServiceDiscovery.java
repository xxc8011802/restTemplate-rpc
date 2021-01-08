package com.example.register.discover;

import cn.hutool.core.collection.CollectionUtil;
import com.example.register.constant.Constant;
import com.example.register.discover.loadbalance.LoadBalance;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.register.constant.Constant.ZK_REGISTRY_PATH;

/**
 * 基于 ZooKeeper 的服务发现接口实现
 *
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);

    private String zkAddress;

    private final ZkClient zkClient;

    private static Map<String,List<String>> interfaceNodeList = new HashMap<>();

    private static int count=0;

    public int getCount()
    {
        return count;
    }

    public ZooKeeperServiceDiscovery(String zkAddress) {
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        List<String> rootChild = zkClient.getChildren(ZK_REGISTRY_PATH);
        for (String name : rootChild){
            String path =ZK_REGISTRY_PATH + "/"+ name;
            //服务监听 监听当前服务下的所有分布式服务的节点信息,并存储到map中
            List <String> nodeList = zkClient.subscribeChildChanges(path, new IZkChildListener()
            {
                @Override
                public void handleChildChange(String parentPath, List<String> currentChilds)
                    throws Exception
                {
                    System.out.println("[[-------------parentPath is" + parentPath + "------------------]]");
                    System.out.println("[[-------------Child change ,currentChilds is" + currentChilds + "-------------]]");
                    interfaceNodeList.put(path,currentChilds);
                }

            });
        }
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String name) {
        // 创建 ZooKeeper 客户端
        //ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try {
            // 获取 service 节点
            String servicePath = ZK_REGISTRY_PATH + "/" + name;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            // 获取 address 节点 可以做负载均衡处理
            String address;
            int size = addressList.size();
            if (size == 1) {
                // 若只有一个地址，则获取该地址
                address = addressList.get(0);
                LOGGER.debug("get only address node: {}", address);
            } else {
                // 若存在多个地址，则随机获取一个地址
                address = LoadBalance.routeLRU(name,addressList);
                //address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("get random address node: {}", address);
            }
            // 获取 address 节点的值
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            //zkClient.close();
        }
    }
}