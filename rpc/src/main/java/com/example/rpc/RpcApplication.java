package com.example.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RpcApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RpcApplication.class, args);
    }

}
