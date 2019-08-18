package com.huawei.cse.porter.file.test;

import java.util.List;

public class AbstractBaseService<T extends AbstractBean> implements IBaseService<T> {
    private IBaseService<T> target;

    protected AbstractBaseService(IBaseService<T> t) {
        target = t;
    }

    @Override
    public T hello(T a) {
        return target.hello(a);
    }

    @Override
    public T[] helloBody(T[] a) {
        return target.helloBody(a);
    }

    @Override
    public List<T> helloList(List<T> a) {
        return a;
    }

    @Override
    public PersonBean actual() {
        return target.actual() ;
    }
}
