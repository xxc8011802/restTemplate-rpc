package com.example.rpc.http.netty;

import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import com.example.rpc.http.netty.code.RpcDecoder;
import com.example.rpc.http.netty.code.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient extends SimpleChannelInboundHandler<RpcResult>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private String host;
    private int port;

    private  RpcResult rpcResult;

    public NettyClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResult rpcResult) throws Exception {
        this.rpcResult = rpcResult;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception", cause);
        ctx.close();
    }

    public RpcResult send(RpcParams rpcParams) throws Exception{
        //初始化线程池 IO连接和工作
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            //Bootstrap是Netty客户端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception{
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcParams.class)); // 编码 RPC 请求
                    pipeline.addLast(new RpcDecoder(RpcResult.class)); // 解码 RPC 响应
                    pipeline.addLast(NettyClient.this); // 处理 RPC 响应
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 连接 RPC 服务器 连接指定的服务地址
            ChannelFuture future = bootstrap.connect(host, port).sync();
            // 写入 RPC 请求数据并关闭连接
            Channel channel = future.channel();
            channel.writeAndFlush(rpcParams).sync();
            channel.closeFuture().sync();
            // 返回 RPC 响应对象
            return rpcResult;
        }finally{
            workGroup.shutdownGracefully();
        }
    }
}
