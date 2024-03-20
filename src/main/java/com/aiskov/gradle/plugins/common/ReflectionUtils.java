package com.aiskov.gradle.plugins.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ReflectionUtils {
    public static List<Type> getGenericParamsOfReturnType(Method method) {
        Type genericReturnType = method.getGenericReturnType();

        if (genericReturnType instanceof ParameterizedType type) {
            return List.of(type.getActualTypeArguments());
        }

        return List.of();
    }

    public static List<Class<?>> getGenericParamsOfFieldType(Field field) {
        Type genericReturnType = field.getGenericType();

        List<Class<?>> result = new LinkedList<>();

        if (genericReturnType instanceof ParameterizedType type) {
            Stream.of(type.getActualTypeArguments()).forEach(param -> {
                if (param instanceof Class<?> clazz) {
                    result.add(clazz);
                }
            });
        }

        return result;
    }
}
