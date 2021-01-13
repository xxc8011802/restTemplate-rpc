package com.example.rpc.proxy;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import com.example.register.discover.ServiceDiscovery;
import com.example.rpc.circuit.Circuit;
import com.example.rpc.circuit.CircuitConfig;
import com.example.rpc.http.rest.RestClient;
import com.example.rpc.util.ClassUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.example.api.rpc.RpcResult.getFailResult;

/**
 * rpc代理类，代理需要调用的方法，是的可以屏蔽底层的http调用，服务发现，序列化这些操作
 */

public class ServiceProxyRsi
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProxyRsi.class);

    @Autowired
    RestClient restClient;

 /*   @Autowired
    NettyClient nettyClient;*/

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    private CircuitConfig circuitConfig;

    private static HashMap<String,CircuitConfig> circuitBreakerMap = new HashMap<>();

    public ServiceProxyRsi(){

    }

    public ServiceProxyRsi(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public ServiceProxyRsi(ServiceDiscovery serviceDiscovery) {
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
            public Object invoke(Object proxy,Method method,Object args[]){
                //可以作为调用链查看
                String requestId = UUID.randomUUID().toString();
                //rpc调用逻辑
                Supplier<RpcResult> supplier = new Supplier<RpcResult>()
                {
                    @Override
                    public RpcResult get()
                    {
                        RpcParams rpcParams = new RpcParams();
                        rpcParams.setClassName(method.getDeclaringClass().getName());
                        rpcParams.setMethodName(method.getName());

                        //System.out.println("[[----Client generate RequestId"+ requestId + "----]]");
                        rpcParams.setRequestId(requestId);
                        //参数类型和参数值都转换为String传递
                        rpcParams.setType(JSONObject.toJSONString(method.getParameterTypes()));
                        rpcParams.setValues(JSONObject.toJSONString(args));
                        // 获得服务的主机名和端口号，之后可通过服务发现的方式来获取服务的ip和端口号
                        String host = "localhost";
                        int port = 9091;
                        String url="http://localhost:9091/";
                        //服务发现
                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName();
                            if (StrUtil.isNotEmpty(serviceVersion)) {
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName,"");
                            url = "http://"+serviceAddress;
                            LOGGER.debug("discover service: {} => {}", serviceName, serviceAddress);
                        }
                        if (StrUtil.isEmpty(serviceAddress)) {
                            throw new RuntimeException("server address is empty");
                        }
                        // 创建 RPC 客户端对象并发送 RPC 请求 restTemplate
                        RpcResult rpcResult = new RpcResult();
                        try
                        {
                            rpcResult= restClient.send(url,rpcParams);
                        }catch (RestClientException exception){
                        }
                        //如果请求响应为失败则抛出异常,业务异常应该会反应对应的错误码和错误信息
                        if(!rpcResult.isSuccess()&&rpcResult.getMessage()==null){
                            throw new RestClientException("Http Post Failed, RestClient Exception ");
                        }
                        return rpcResult;
                    }
                };

                //捕获异常后异常后处理逻辑
                Function<? super Throwable, ? extends RpcResult> errorHandler = throwable -> {
                    //重试返回
                    //当故障率高于设定的阈值时，熔断器状态会从由CLOSE变为OPEN。这时所有的请求都会抛出CallNotPermittedException异常
                    if(throwable instanceof CallNotPermittedException){
                        System.out.println("[[----throwable 熔断捕获:"+throwable+"----]]");
                        return getFailResult("运行时异常",requestId);
                    }else if(throwable instanceof RestClientException){
                        //请求通信返回的异常码处理
                        System.out.println("[[----throwable 请求超时:"+throwable+"----]]");
                        return getFailResult("请求超时",requestId);
                    }else{
                        //兜底,不知道哪里出来的异常码
                        System.out.println("[[----throwable 其他错误:"+throwable+"----]]");
                        return getFailResult("其他错误",requestId);
                    }
                };

                //resilience4j 熔断配置  应该初始化的时候把需要进行熔断的method加入进行熔断
                //根据接口获取不同方法名的熔断器，例如UserService的getUserInfo和getUserName就使用不同名的熔断配置,做到接口间隔离，互不影响
                if(circuitBreakerMap.get(method.getName())!=null){
                    circuitConfig = circuitBreakerMap.get(method.getName());
                }else{
                    circuitConfig = new CircuitConfig(method.getName());
                    circuitBreakerMap.put(method.getName(),circuitConfig);
                }

                //熔断第一层装饰器
                Supplier<RpcResult> supplier1 = CircuitBreaker.decorateSupplier(circuitConfig.getCircuitBreaker(), supplier);
                //重试第二层装饰器
                Supplier<RpcResult> supplier2 = Retry.decorateSupplier(circuitConfig.getRetry(), supplier1);
                //恢复第三层装饰器
                Try<RpcResult> supplier3 =Try.ofSupplier(supplier2).recover(errorHandler);
                //执行
                RpcResult result = supplier3.get();



                try{
                    if(result.isSuccess()){
                        Object value = JSONObject.parseObject(result.getRpcResultValue(),ClassUtil.getArgTypeClass(result.getRpcResultType()));
                        System.out.println(result.toString());
                        return value;
                    }else{//若响应失败返回一个空
                        System.out.println("failed response and return null");
                        return null;
                    }
                }catch (ClassNotFoundException e){
                    System.out.println("ClassNotFoundException:"+e);
                }
                return null;
            }
        });
    }
}
