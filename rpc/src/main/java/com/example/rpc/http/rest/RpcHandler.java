package com.example.rpc.http.rest;

import com.alibaba.fastjson.JSON;
import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import com.example.rpc.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * rpc服务端解析rpc请求处理
 */
public class RpcHandler
{
    public static RpcResult getResult(RpcParams rpcParams){
        try{
            //接口和实现类的映射关系，根据接口名获得对应的实现类的实例
            Map<String, Object> map = RestRpcServer.getMap();
            Object ServiceBean = map.get(rpcParams.getClassName());

            //获取类(如果获取的是接口则要获取对应的实现方法类)??
            Class clazz = Class.forName(rpcParams.getClassName());
            //参数类型
            Class<?>[] type = ClassUtil.getArgTypes(rpcParams.getType());
            //请求参数信息
            Object[] args = ClassUtil.getArgObjects(rpcParams.getValues(),type);
            //获取类下的所有方法信息
            Method method = clazz.getMethod(rpcParams.getMethodName(),type);
            method.setAccessible(true);
            /**
             * 反射:根据实例和参数进行请求
             */
            Object rpcResultValue = method.invoke(ServiceBean, args);
            RpcResult rpcResult = RpcResult.getSuccessResult(rpcParams.getRequestId(),method.getReturnType().getName(), JSON.toJSONString(rpcResultValue));
            System.out.println("[[----getResult method:  "+ rpcResult.toString()+"----]]");
            return rpcResult;
        }catch (Exception e){
            RpcResult rpcResult = RpcResult.getFailResult(rpcParams.getRequestId(),"failed");
            System.out.println("[[----getResult method:  "+ rpcResult.toString()+"----]]");
            return rpcResult;
        }
    }

}
