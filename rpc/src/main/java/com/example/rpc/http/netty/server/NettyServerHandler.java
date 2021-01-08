package com.example.rpc.http.netty.server;

import com.alibaba.fastjson.JSON;
import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import com.example.rpc.util.ClassUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * RPC 服务端处理器（用于处理 RPC 请求）
 *
 * @author huangyong
 * @since 1.0.0
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcParams> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    private final Map<String, Object> handlerMap;

    public NettyServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcParams rpcParams) throws Exception {
        // 创建并初始化 RPC 响应对象
        RpcResult rpcResult = new RpcResult();
        //response.setRequestId(request.getRequestId());
        try {
            rpcResult = handle(rpcParams);
        } catch (Exception e) {
            LOGGER.error("handle result failure", e);
            rpcResult = RpcResult.getFailResult("failed");
        }
        // 写入 RPC 响应对象并自动关闭连接
        ctx.writeAndFlush(rpcResult).addListener(ChannelFutureListener.CLOSE);
    }

    private RpcResult handle(RpcParams rpcParams) throws Exception {
        // 获取服务对象 接口
        String serviceName = rpcParams.getClassName();
        //String serviceVersion = request.getServiceVersion();
        /*if (StrUtil.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }*/
        Object serviceBean = handlerMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
        }
        // 获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = rpcParams.getMethodName();
        Class<?>[] types = ClassUtil.getArgTypes(rpcParams.getType());
        Object[] args = ClassUtil.getArgObjects(rpcParams.getValues(),types);

        // 执行反射调用
        Method method = serviceClass.getMethod(methodName, types);
        method.setAccessible(true);
        //只获得了rpcresult中value的值
        Object rpcResultValue = method.invoke(serviceBean, args);
        RpcResult rpcResult = RpcResult.getSuccessResult(method.getReturnType().getName(), JSON.toJSONString(rpcResultValue));
        return rpcResult;
        // 使用 CGLib 执行反射调用 ，cglib可以代理非接口
        /*FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
