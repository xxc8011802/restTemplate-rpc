package com.example.rpc.http;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import com.example.register.discover.ServiceDiscovery;
import com.example.rpc.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * rpc代理类，代理需要调用的方法，是的可以屏蔽底层的http调用，服务发现，序列化这些操作
 */

public class ServiceProxy
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProxy.class);

    @Autowired
    HttpClientService httpClientService;

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public ServiceProxy(){

    }

    public ServiceProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public ServiceProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    /**
     * loader: 用哪个类加载器去加载代理对象
     * interfaces:动态代理类需要实现的接口
     * h:动态代理方法在执行时，会调用h里面的invoke方法去执行
     * @param interfaceClass
     * @param serviceVersion
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion){
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass}, new InvocationHandler(
        ){
            /**
             * 传入远程服务的请求调用信息
             * @param proxy 要代理的类对象，例如UserServiceImpl类型的对象
             * @param method 要代理的方法对象，例如UserServiceImpl的getUserInfo方法
             *               一个java.lang.reflect.Method对象提供关于类或者接口上单个方法的信息访问,
             * @return
             */
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                RpcParams rpcParams = new RpcParams();
                if(method.getName().equals("getUserInfo")){
                    System.out.println("method is getUserInfo");
                }
                rpcParams.setClassName(method.getDeclaringClass().getName());
                rpcParams.setMethodName(method.getName());
                //参数类型和参数值都转换为String传递
                rpcParams.setType(JSONObject.toJSONString(method.getParameterTypes()));
                rpcParams.setValues(JSONObject.toJSONString(args));
                // 获得服务的主机名和端口号，之后可通过服务发现的方式来获取服务的ip和端口号
                String host = "localhost";
                String port = "9091";
                String url="http://localhost:9091/";
                //服务发现
                if (serviceDiscovery != null) {
                    String serviceName = interfaceClass.getName();
                    if (StrUtil.isNotEmpty(serviceVersion)) {
                        serviceName += "-" + serviceVersion;
                    }
                    serviceAddress = serviceDiscovery.discover(serviceName);
                    url = "http://"+serviceAddress;
                    LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                }
                if (StrUtil.isEmpty(serviceAddress)) {
                    throw new RuntimeException("server address is empty");
                }

                // 创建 RPC 客户端对象并发送 RPC 请求
                RpcResult rpcResult = httpClientService.send(url,rpcParams);
                //响应的rpc的ResultValue是json类型的,需要转换为需要的类型
                Object value = JSONObject.parseObject(rpcResult.getRpcResultValue(),ClassUtil.getArgTypeClass(rpcResult.getRpcResultType()));
                return value;
            }
        });
    }




}
