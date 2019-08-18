package com.huawei.cse.porter.file;

import com.huawei.cse.porter.file.test.IMyService;
import com.huawei.cse.porter.file.test.MyEndpoint;
import org.apache.servicecomb.foundation.common.utils.BeanUtils;
import org.apache.servicecomb.foundation.common.utils.Log4jUtils;
import org.apache.servicecomb.swagger.generator.core.utils.ParamUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FileMain {
    public static void main(String[] args) throws Exception {
        Method[] methods = MyEndpoint.class.getMethods();
        System.out.println(ParamUtils.getGenericParameterType(MyEndpoint.class, methods[0].getDeclaringClass(), methods[0].getGenericReturnType()));
        System.out.println(ParamUtils.getGenericParameterType(MyEndpoint.class, methods[0].getDeclaringClass(), methods[1].getGenericReturnType()));
        System.out.println(ParamUtils.getGenericParameterType(MyEndpoint.class, methods[0].getDeclaringClass(), methods[2].getGenericReturnType()));
        System.out.println(ParamUtils.getGenericParameterType(MyEndpoint.class, methods[0].getDeclaringClass(), methods[3].getGenericReturnType()));

        methods = IMyService.class.getMethods();
        System.out.println(ParamUtils.getGenericParameterType(IMyService.class, methods[0].getDeclaringClass(), methods[0].getGenericReturnType()));
        System.out.println(ParamUtils.getGenericParameterType(IMyService.class, methods[0].getDeclaringClass(), methods[1].getGenericReturnType()));
        System.out.println(ParamUtils.getGenericParameterType(IMyService.class, methods[0].getDeclaringClass(), methods[2].getGenericReturnType()));
        System.out.println(ParamUtils.getGenericParameterType(IMyService.class, methods[0].getDeclaringClass(), methods[3].getGenericReturnType()));


        Log4jUtils.init();
        BeanUtils.init();
    }
}
