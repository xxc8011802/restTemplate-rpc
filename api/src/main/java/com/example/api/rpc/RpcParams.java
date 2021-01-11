package com.example.api.rpc;

import java.io.Serializable;

/**
 * rpc远程调用的请求入参
 */
public class RpcParams implements Serializable
{
    /*
     * 类名 拿到方法的类名
     */
    private String className;

    /*
     * 方法名 拿到具体方法名
     */
    private String methodName;

    /*
     * 请求Id requestId
     */
    private String requestId;

    /*
     * 请求类型
     */
    private String type;

    /*
     * 请求参数
     */
    private String values;

    public RpcParams(){

    }

    public RpcParams(String className, String methodName, String requestId, String type, String values)
    {
        this.className = className;
        this.methodName = methodName;
        this.requestId = requestId;
        this.type = type;
        this.values = values;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getValues()
    {
        return values;
    }

    public void setValues(String values)
    {
        this.values = values;
    }
}
