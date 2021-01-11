package com.example.clientbak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @desc 配置类
 * 用于加载需要的Bean文件
 * @ImportResource 引入对应的xml文件
 */
@Configuration
@ImportResource(locations = {"classpath:serviceBean.xml"})
public class ConfigClass
{
    /**
     * 服务优雅下线关闭配置
     * @return
     */
    @Bean
    public ShutDown getShutDown() {
        return new ShutDown();
    }
}
