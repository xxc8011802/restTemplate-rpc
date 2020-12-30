package com.example.client.controller;

import com.example.api.bean.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
