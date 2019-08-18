/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.swagger.generator.core.utils;

import io.swagger.converter.ModelConverters;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.servicecomb.swagger.generator.core.OperationGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

public final class ParamUtils {
    private static DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamUtils.class);

    private ParamUtils() {

    }

    // 如果existName为empty，则通过原型查找
    public static String getParameterName(String existName, Method method, int paramIdx) {
        if (StringUtils.isEmpty(existName)) {
            existName = getParameterName(method, paramIdx);
        }

        return existName;
    }

    public static String getParameterName(Executable methodOrConstructor, int parameterIndex) {
        MethodParameter methodParameter = MethodParameter.forMethodOrConstructor(methodOrConstructor, parameterIndex);
        return getParameterName(methodParameter, parameterIndex);
    }

    public static String getParameterName(Method method, int paramIdx) {
        MethodParameter methodParameter = new MethodParameter(method, paramIdx);
        return getParameterName(methodParameter, paramIdx);
    }

    public static String getParameterName(MethodParameter methodParameter, int paramIdx) {
        methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);

        String paramName = methodParameter.getParameterName();
        if (paramName == null) {
            // 小于jdk8的场景中，即使有debug参数，也无法对着interface获取参数名，此时直接使用arg + paramIndex来表示
            paramName = "arg" + paramIdx;
        }
        return paramName;
    }

    public static Type getGenericParameterType(Class clazz, Method method, int paramIdx) {
        Type type = method.getGenericParameterTypes()[paramIdx];
        return getGenericParameterType(clazz, method.getDeclaringClass(), type);
    }

    public static Type getGenericParameterType(Class mainClass, Class declaringClass, Type type) {
        if (type instanceof Class) {
            return type;
        }

        if (type instanceof TypeVariable) {
            TypeVariable[] typeVariables = declaringClass.getTypeParameters();
            Type[] actualTypes;
            if (mainClass.getGenericSuperclass() != null) {
                actualTypes = getActualTypes(mainClass.getGenericSuperclass());
            } else {
                actualTypes = new Type[0];
                Type[] interfaceTypes = mainClass.getGenericInterfaces();
                for (Type t : interfaceTypes) {
                    Type[] ttTypes = getActualTypes(t);
                    Type[] tempTypes = new Type[actualTypes.length + ttTypes.length];
                    System.arraycopy(actualTypes, 0, tempTypes, 0, actualTypes.length);
                    System.arraycopy(ttTypes, 0, tempTypes, actualTypes.length, ttTypes.length);
                    actualTypes = tempTypes;
                }
            }
            for (int i = 0; i < typeVariables.length; i++) {
                if (typeVariables[i] == type) {
                    return actualTypes[i];
                }
            }
        } else if (type instanceof GenericArrayType) {
            Class t = (Class) getGenericParameterType(mainClass, declaringClass, ((GenericArrayType) type).getGenericComponentType());
            return Array.newInstance(t, 0).getClass();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] targetTypes = new Type[parameterizedType.getActualTypeArguments().length];
            for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
                targetTypes[i] = getGenericParameterType(mainClass, declaringClass, parameterizedType.getActualTypeArguments()[i]);
            }
            return TypeUtils.parameterize((Class) parameterizedType.getRawType(), targetTypes);
        }
        throw new IllegalArgumentException(String.format("not implement (%s) (%s) (%s)", mainClass.getName(), declaringClass.getName(), type.getTypeName()));
    }

    private static Type[] getActualTypes(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        return new Type[0];
    }

    public static String generateBodyParameterName(Method method) {
        return method.getName() + "Body";
    }

    public static BodyParameter createBodyParameter(OperationGenerator operationGenerator,
                                                    int paramIdx) {
        Method method = operationGenerator.getProviderMethod();
        String paramName = getParameterName(method, paramIdx);
        Type paramType = getGenericParameterType(operationGenerator.getCls(), method, paramIdx);
        return createBodyParameter(operationGenerator.getSwagger(), paramName, paramType);
    }

    public static BodyParameter createBodyParameter(Swagger swagger, String paramName, Type paramType) {
        addDefinitions(swagger, paramType);

        Property property = ModelConverters.getInstance().readAsProperty(paramType);
        Model model = PropertyBuilder.toModel(property);
        if (model instanceof ModelImpl && property instanceof StringProperty) {
            ((ModelImpl) model).setEnum(((StringProperty) property).getEnum());
        }

        BodyParameter bodyParameter = new BodyParameter();
        bodyParameter.setName(paramName);
        bodyParameter.setSchema(model);

        return bodyParameter;
    }

    public static void addDefinitions(Swagger swagger, Type paramType) {
        Map<String, Model> models = ModelConverters.getInstance().readAll(paramType);
        for (Map.Entry<String, Model> entry : models.entrySet()) {
            if (null != swagger.getDefinitions()) {
                Model tempModel = swagger.getDefinitions().get(entry.getKey());
                if (null != tempModel && !tempModel.equals(entry.getValue())) {
                    LOGGER.warn("duplicate param model: " + entry.getKey());
                    throw new IllegalArgumentException("duplicate param model: " + entry.getKey());
                }
            }
            swagger.addDefinition(entry.getKey(), entry.getValue());
        }
    }


    public static void setParameterType(OperationGenerator operationGenerator, Method method, int paramIdx,
                                        AbstractSerializableParameter<?> parameter) {
        Swagger swagger = operationGenerator.getSwagger();

        Type paramType = ParamUtils.getGenericParameterType(operationGenerator.getCls(), method, paramIdx);

        ParamUtils.addDefinitions(swagger, paramType);

        Property property = ModelConverters.getInstance().readAsProperty(paramType);

        if (isComplexProperty(property)) {
            // cannot set a simple parameter(header, query, etc.) as complex type
            String msg = String.format("not allow complex type for %s parameter, method=%s:%s, paramIdx=%d, type=%s",
                    parameter.getIn(),
                    method.getDeclaringClass().getName(),
                    method.getName(),
                    paramIdx,
                    paramType.getTypeName());
            throw new Error(msg);
        }
        parameter.setProperty(property);
    }

    /**
     * Set param type info. For {@linkplain javax.ws.rs.BeanParam BeanParam} scenario.
     *
     * @param paramType type of the swagger parameter
     * @param parameter swagger parameter
     */
    public static void setParameterType(Type paramType, AbstractSerializableParameter<?> parameter) {
        Property property = ModelConverters.getInstance().readAsProperty(paramType);

        if (isComplexProperty(property)) {
            // cannot set a simple parameter(header, query, etc.) as complex type
            throw new IllegalArgumentException(
                    String.format(
                            "not allow such type of param:[%s], param name is [%s]",
                            property.getClass(),
                            parameter.getName()));
        }
        parameter.setProperty(property);
    }

    public static boolean isComplexProperty(Property property) {
        if (RefProperty.class.isInstance(property) || ObjectProperty.class.isInstance(property)
                || MapProperty.class.isInstance(property)) {
            return true;
        }

        if (ArrayProperty.class.isInstance(property)) {
            return isComplexProperty(((ArrayProperty) property).getItems());
        }

        return false;
    }

    public static int findParameterByName(String name, List<Parameter> parameterList) {
        for (int idx = 0; idx < parameterList.size(); idx++) {
            Parameter parameter = parameterList.get(idx);
            if (name.equals(parameter.getName())) {
                return idx;
            }
        }

        return -1;
    }

    public static boolean isRealBodyParameter(Parameter parameter) {
        return BodyParameter.class.getName().equals(parameter.getClass().getName());
    }
}
