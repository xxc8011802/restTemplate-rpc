package com.example.register;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.register.constant.Constant.ZK_CONFIG_PATH;

@SpringBootTest
class RegisterApplicationTests
{

    /**
     * 测试监听节点的信息变更
     */
    @Test
    void IZkDataListenerTest()
    {
        ZkClient zk = new ZkClient(new ZkConnection("127.0.0.1:2181"), 5000);
        if (!zk.exists(ZK_CONFIG_PATH)) {
            //zk.createPersistent(ZK_CONFIG_PATH);
            zk.createEphemeral(ZK_CONFIG_PATH);
        }
        zk.writeData(ZK_CONFIG_PATH, "1");
        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){

        }
        if (!zk.exists(ZK_CONFIG_PATH + "test")) {
            zk.createPersistent(ZK_CONFIG_PATH +"test");
        }
        zk.writeData(ZK_CONFIG_PATH, "2");
        zk.delete(ZK_CONFIG_PATH);
        zk.delete(ZK_CONFIG_PATH +"test");

    }

}
