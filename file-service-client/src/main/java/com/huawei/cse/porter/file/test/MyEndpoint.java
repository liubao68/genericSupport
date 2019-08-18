package com.huawei.cse.porter.file.test;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@RestSchema(schemaId = "MyEndpoint")
@RequestMapping(path = "/")
public class MyEndpoint {
    @RpcReference(microserviceName = "file-service", schemaId = "MyEndpoint")
    private IMyService service;

    @GetMapping("getBean")
    public PersonBean getBean() {
        PersonBean p = new PersonBean();
        p.setName("p");
        return service.hello(p);
    }

    @GetMapping("getBeanArray")
    public PersonBean[] getBeanArray() {
        PersonBean p = new PersonBean();
        p.setName("p");
        PersonBean[] pp = new PersonBean[]{p};
        return service.helloBody(pp);
    }

    @GetMapping("getBeanList")
    public List<PersonBean> getBeanList() {
        PersonBean p = new PersonBean();
        p.setName("p");
        List<PersonBean> pp = new ArrayList<>();
        pp.add(p);
        return service.helloList(pp);
    }
}
