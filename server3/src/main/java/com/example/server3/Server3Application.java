package com.example.server3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server3Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Server3Application.class, args);
    }

/*    *//**
     * 接口和实现类映射关系map静态注入ApplicationContext中,在工程初始化的时候注入
     * @return
     *//*
    @Bean
    public RestRpcServer getApplicationContext() {
        return new RestRpcServer();
    }*/

}
