package com.example.server1.service;

import com.example.api.bean.User;
import com.example.api.bean.UserService;
import com.example.server1.rpc.RpcService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/*@Service*/
@RpcService(UserService.class)
public class UserServiceImpl implements UserService
{
    public UserServiceImpl(){

    }

    public String getName(String name){
        User user = new User(name,20);
        return user.getUserName();
    }

    public User getUserInfo(User user){
        User user2 = new User(user.getUserName(),user.getAge()+30);
        return user2;
    }


    public static void main(String[] args)
    {
        // 启动 Spring 应用上下文
        ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/serviceBean.xml");
        UserService userService = applicationContext.getBean(UserService.class);
        System.out.println(userService.getName("aaaxxc"));
    }
}
