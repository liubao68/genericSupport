package com.huawei.cse.porter.file.test;

import java.util.List;

public interface IBaseService<T extends AbstractBean> {
    T hello(T a);
    T[] helloBody(T[] a);
    List<T> helloList(List<T> a);
    PersonBean actual();
}
