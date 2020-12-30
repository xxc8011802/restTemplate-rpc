package com.example.api.rpc;

import java.io.Serializable;

/**
 * rpc响应 :支持序列化
 */
public class RpcResult implements Serializable
{
    private static final long serialVersionUID = 1810538160493143519L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误原因
     */
    private String message;


    private String rpcResultType;


    private String rpcResultValue;

    public RpcResult(){

    }

    public RpcResult(boolean success, String message, String rpcResultType, String rpcResultValue)
    {
        this.success = success;
        this.message = message;
        this.rpcResultType = rpcResultType;
        this.rpcResultValue = rpcResultValue;
    }

/*    public RpcResult(Builder builder)
    {
        this.success = success;
        this.message = message;
        this.rpcResultType = rpcResultType;
        this.rpcResultValue = rpcResultValue;
    }*/

    public static RpcResult getFailResult(String reason) {
        return new RpcResult(false, "失败", null, null);
    }

    public static RpcResult getSuccessResult(String resultType, String resultValue) {
        return new RpcResult(true, "成功了", resultType, resultValue);
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getRpcResultType()
    {
        return rpcResultType;
    }

    public void setRpcResultType(String rpcResultType)
    {
        this.rpcResultType = rpcResultType;
    }

    public String getRpcResultValue()
    {
        return rpcResultValue;
    }

    public void setRpcResultValue(String rpcResultValue)
    {
        this.rpcResultValue = rpcResultValue;
    }

/*  public static class Builder{
        private boolean success;
        private String message;
        private String rpcResultType;
        private String rpcResultValue;

        public Builder success(boolean success){
            this.success = success;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder rpcResultType(String rpcResultType){
            this.rpcResultType = rpcResultType;
            return this;
        }

        public Builder rpcResultValue(String rpcResultValue){
            this.rpcResultValue = rpcResultValue;
            return this;
        }
        public RpcResult build() {
            return new RpcResult(this);
        }
    }*/

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("RpcResult{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", rpcResultType='").append(rpcResultType).append('\'');
        sb.append(", rpcResultValue='").append(rpcResultValue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
