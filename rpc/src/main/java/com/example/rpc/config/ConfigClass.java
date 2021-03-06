package com.example.rpc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @desc 配置类
 * 用于加载需要的Bean文件
 * @ImportResource 引入对应的xml文件
 */
@Configuration
@ImportResource(locations = {"classpath:rpcserver.xml"})
public class ConfigClass
{
}
