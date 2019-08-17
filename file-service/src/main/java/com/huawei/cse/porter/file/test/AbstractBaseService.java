package com.huawei.cse.porter.file.test;

public class AbstractBaseService<T extends AbstractBean> implements IBaseService<T> {
    private IBaseService<T> target;

    protected AbstractBaseService(IBaseService<T> t) {
        target = t;
    }

    @Override
    public T hello() {
        return target.hello();
    }

    @Override
    public T[] helloBody() {
        return target.helloBody();
    }
}
