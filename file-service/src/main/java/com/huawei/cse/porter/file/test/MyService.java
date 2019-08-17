package com.huawei.cse.porter.file.test;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyService implements IMyService {
    @Override
    public PersonBean hello() {
        return new PersonBean();
    }

    @Override
    public PersonBean[] helloBody() {
        return new PersonBean[0];
    }
}
