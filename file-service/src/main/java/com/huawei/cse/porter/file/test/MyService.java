package com.huawei.cse.porter.file.test;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class MyService implements IMyService {
    @Override
    public PersonBean hello(PersonBean a) {
        return a;
    }

    @Override
    public PersonBean[] helloBody(PersonBean[] t) {
        return t;
    }

    @Override
    public List<PersonBean> helloList(List<PersonBean> a) {
        return a;
    }

    @Override
    public PersonBean actual() {
        return null;
    }
}
