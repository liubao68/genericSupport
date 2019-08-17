package com.huawei.cse.porter.file.test;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.provider.pojo.RpcSchema;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestSchema(schemaId = "MyEndpoint")
@RequestMapping(path = "/")
public class MyEndpoint  {
    @RpcReference(microserviceName = "file-service", schemaId = "MyEndpoint")
    private IMyService service;

    @GetMapping("getBean")
    PersonBean getBean() {
        return service.hello();
    }
}
