package com.huawei.cse.porter.file.test;

public interface IBaseService<T extends AbstractBean> {
    T hello();
    T[] helloBody();
}
