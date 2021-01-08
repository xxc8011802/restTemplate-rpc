package com.example.server1.service;

import com.example.api.bean.CompanyService;
import com.example.rpc.annotation.RpcService;

@RpcService(CompanyService.class)
public class CompanyServiceImpl implements CompanyService
{

    public CompanyServiceImpl()
    {
    }

    public String getCompanyName(String companyName){
        return "migu";
    }
}
