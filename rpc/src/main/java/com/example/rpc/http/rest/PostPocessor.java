package com.example.rpc.http.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class PostPocessor implements BeanPostProcessor
{
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        if(beanName.equals("restRpcServer")){
            System.out.println("postProcessBeforeInitialization:"+beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
        if(beanName.equals("restRpcServer")){
            System.out.println("postProcessAfterInitialization:"+beanName);
        }
        return bean;
    }
}
