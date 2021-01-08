package com.example.rpc.http.netty.server;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import com.example.register.register.ServiceRegistry;
import com.example.rpc.http.netty.code.RpcDecoder;
import com.example.rpc.http.netty.code.RpcEncoder;
import com.example.rpc.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC 服务器（用于发布 RPC 服务）
 *
 */
public class RpcServer implements ApplicationContextAware, InitializingBean
{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    /**
     * 存放 服务名 与 服务对象 之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx)
    {
        // 扫描带有 RpcService 注解的类并初始化 handlerMap 对象
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtil.isNotEmpty(serviceBeanMap))
        {
            for (Object serviceBean : serviceBeanMap.values())
            {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //1 bossGroup对应的就是主线程池Accepter，只接收客户端的连接（注册，初始化逻辑）
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //2 具体的工作由workerGroup这个从线程池来完成
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //3 创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //4 通过ServerBootstrap的group方法，设置主从"线程池"
            bootstrap.group(bossGroup, workerGroup);
            //5 channel:指定通道channel的类型
            bootstrap.channel(NioServerSocketChannel.class);
            //6 initChannel设置子通道也就是SocketChannel的处理器
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcParams.class)); // 解码 RPC 请求
                    pipeline.addLast(new RpcEncoder(RpcResult.class)); // 编码 RPC 响应
                    pipeline.addLast(new NettyServerHandler(handlerMap)); // 处理 RPC 请求
                }
            });
            //7 配置ServerSocketChannel的选项
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //8 配置子通道也就是SocketChannel的选项
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 获取 RPC 服务器的 IP 地址与端口号
            String[] addressArray = StrUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            // 启动 RPC 服务器 绑定并侦听服务端口
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            // 注册 RPC 服务地址
            if (serviceRegistry != null) {
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            LOGGER.debug("server started on port {}", port);
            // 关闭 RPC 服务器 服务器同步连接断开时,这句代码才会往下执行
            // 也就是服务端执行完这一句:ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
