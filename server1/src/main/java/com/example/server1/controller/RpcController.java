package com.example.server1.controller;

import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.example.rpc.http.rest.RpcHandler.getResult;

/**
 * 接收rpc服务调用请求
 */
@RestController
public class RpcController
{
    @RequestMapping("/")
    public RpcResult rpcMain1 (@RequestBody RpcParams rpcParams){
        return getResult(rpcParams);
    }

    //@PostMapping("/")
    @RequestMapping(value = "/",method = RequestMethod.POST)
    public RpcResult rpcMain (@RequestBody RpcParams rpcParams){
        RpcResult rpcResult = getResult(rpcParams);
        //RpcResult rpcResult = new RpcResult(true,"ok","user","name:1");
        System.out.println("Server return:" + rpcResult.toString());
        return rpcResult;
    }

}
