package com.example.server2.controller;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.api.bean.UserService;
import com.example.server2.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class UserController
{
    private static  final Log log= LogFactory.getLog(UserController.class);

//    @Resource
//    private User user;

    @Resource
    private UserService userService;

    @RequestMapping("/getUserName")
    public String getUserName(){
        String userName = userService.getName("xxc");
        return userName;
    }

    /**
     * 接收json格式的数据返回user对象
     * @param userInfojson
     * @return
     */
    @PostMapping("/getUserInfo")
    public User getUserInfo(@RequestBody String userInfojson){
        User user2= JSONObject.parseObject(userInfojson,new TypeReference<User>() {});
        user2.setUserName(user2.getUserName());
        user2.setAge(user2.getAge()+10);
        return user2;
    }

    /**
     * 接收json格式的数据返回user对象
     * @param user
     * @return
     */
    @RequestMapping(value = "/getUserInfoPOJO",method = RequestMethod.POST)
    //@PostMapping("/getUserInfoPOJO")
    public User getUserInfoPOJO(@RequestBody User user){
        User user2= new User(user.getUserName(),user.getAge()+20);
        System.out.println(user2);
        return user2;
    }

}
