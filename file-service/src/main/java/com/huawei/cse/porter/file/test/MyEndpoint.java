package com.huawei.cse.porter.file.test;

import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.springframework.beans.factory.annotation.Autowired;

@RpcSchema(schemaId = "MyEndpoint")
public class MyEndpoint extends AbstractBaseService<PersonBean> implements IMyService {
    public MyEndpoint(@Autowired IMyService other) {
        super(other);
    }
}
