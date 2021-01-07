package com.example.client.controller;

import com.example.api.bean.CompanyService;
import com.example.api.bean.User;
import com.example.api.bean.UserService;
import com.example.rpc.http.ServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
public class RestTemplateController
{
    @Resource
    ApplicationContext context ;
    /**
     * restTemplate实现http请求
     */
    @Autowired
    private RestTemplate restTemplate;

    /*@RequestMapping("/testRestTemplate")
    @ResponseBody
    public Object testRestTemplate() throws IOException
    {
        *//*ResponseEntity result= restTemplate.getForEntity("http://localhost:9091/getUserName",ResponseEntity.class);*//*
        String result = restTemplate.getForObject("http://localhost:9091/getUserName",String.class);
        return result;
    }*/

/*    *//**
     * 测试restTemplate发送post请求
     * 传输方式可以采用LinkedMultiValueMap  JSONObject 实体类传送参数
     * 使用json 格式传输
     * @return
     * @throws IOException
     *//*
    @RequestMapping("/testRestTemplatePost")
    @ResponseBody
    public Object testRestTemplatePost() throws IOException
    {
        User user = new User("fdm",10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        //采用json编码协议传输
        String userjson = JSONObject.toJSONString(user);
        HttpEntity<String> request = new HttpEntity<>(userjson, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:9091/getUserInfo",request,String.class);
        return responseEntity.getBody();
    }*/

    /**
     * 测试restTemplate发送post请求
     * @return
     * @throws IOException
     */
/*    @RequestMapping("/testRestTemplatePostPOJO")
    @ResponseBody
    public Object testRestTemplatePostPOJO() throws IOException
    {
        User user = new User("fdm",10);
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity<User> responseEntity = restTemplate.postForEntity("http://localhost:9091/getUserInfoPOJO",request,User.class);
        System.out.println(responseEntity.getBody());
        return responseEntity.getBody();
    }*/

    /**
     * 测试rpc
     * 传输方式可以采用LinkedMultiValueMap  JSONObject 实体类传送参数
     * 使用json 格式传输
     * @return
     * @throws IOException
     */
    @RequestMapping("/testRestTemplateRpc")
    @ResponseBody
    public Object testRestTemplateRpc() throws IOException
    {
        //ApplicationContext context = new ClassPathXmlApplicationContext("serviceBean.xml");
        ServiceProxy serviceProxy = context.getBean(ServiceProxy.class);
        UserService userService = serviceProxy.create(UserService.class);
        User user = userService.getUserInfo(new User("xxc",2));
        CompanyService companyService = serviceProxy.create(CompanyService.class);
        String companyName = companyService.getCompanyName("mi");
        return companyName+user.toString();
    }



    public static void main(String[] args)
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("serviceBean.xml");
        ServiceProxy serviceProxy = context.getBean(ServiceProxy.class);
        UserService userService = serviceProxy.create(UserService.class);
        User user = userService.getUserInfo(new User("bob",10));
    }

}
