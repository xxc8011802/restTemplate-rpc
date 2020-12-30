package com.example.client.service;


import com.example.api.bean.User;
import com.example.api.bean.UserService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService
{
    public UserServiceImpl(){

    }

    public String getName(String name){
        User user = new User(name,100);
        return user.getUserName();
    }

    public User getUserInfo(User user){
        return new User("apple",1);
    }


    public static void main(String[] args)
    {
        // 启动 Spring 应用上下文
        ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/serviceBean.xml");
        UserService userService = applicationContext.getBean(UserService.class);
        System.out.println(userService.getName("aaaxxc"));
    }
}
