package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class ClientApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ClientApplication.class, args);
    }

}
