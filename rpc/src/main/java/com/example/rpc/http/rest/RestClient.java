package com.example.rpc.http.rest;

import com.alibaba.fastjson.JSONObject;
import com.example.api.rpc.RpcParams;
import com.example.api.rpc.RpcResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * restTemplate 提供http调用的service
 */
/*@Component*/
public class RestClient
{
    @Autowired
    private RestTemplate restTemplate;

    /**
     *
     * @param url get请求方式
     * @return
     * @throws Exception
     */
    public RpcResult doGet(String url) throws Exception{
        String result = restTemplate.getForObject(url,String.class);
        //解析远程调用的请求响应
        RpcResult rpcResult = JSONObject.parseObject(result,RpcResult.class);
        /*if(rpcResult==null){
            //如果返回为空，重新设置下响应
            //使用构建器模式写入一个http的响应返回
            rpcResult = new RpcResult.Builder().success(false).message("failed").rpcResultType("").rpcResultValue("").build();
        }*/
        return rpcResult;
    }

    /**
     * post请求方式
     */
    public RpcResult doPost(String url,JSONObject jsonObject) throws Throwable{

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        //采用json编码协议传输
        String userjson = JSONObject.toJSONString(jsonObject);
        HttpEntity<String> request = new HttpEntity<>(userjson, headers);
        ResponseEntity<String>
            responseEntity = restTemplate.postForEntity(url,request,String.class);

        RpcResult rpcResult = JSONObject.parseObject(responseEntity.getBody(),RpcResult.class);
        return rpcResult;
    }

    /**
     * 发送rpc请求,类和方法名都保存在rpcParams中
     * @param url
     * @param rpcParams
     * @return
     * @throws Throwable
     */
    public RpcResult send(String url ,RpcParams rpcParams) throws RestClientException
    {
        HttpEntity<RpcParams> request = new HttpEntity<>(rpcParams);
        // 五秒超时则直接返回
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        restTemplate.setRequestFactory(requestFactory);
        //
        ResponseEntity<RpcResult> responseEntity = restTemplate.postForEntity(url,request,RpcResult.class);
        //System.out.println("[[client get :" + responseEntity.getBody()+"]]");
        return responseEntity.getBody();
    }
}
