package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@SpringBootApplication
/*@ComponentScan(basePackages = "com")
* 扫描包路径下的所有Bean包含注解*/
public class ClientApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ClientApplication.class, args);
    }

}
