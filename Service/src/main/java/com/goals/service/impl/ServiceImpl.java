package com.goals.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.goals.service.IService;

@Service
public class ServiceImpl implements IService {

    @Override
    public String getName() {
        return "张三";
    }
}
