package com.aiskov.gradle.plugins.fixtures;

import com.aiskov.gradle.plugins.common.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class FieldContext {
    private final Field field;
    private final List<Class<?>> allClasses;

    public String getName() {
        return this.field.getName();
    }

    public Class<?> getType() {
        return this.field.getType();
    }

    public Annotation[] getAnnotations() {
        return this.field.getAnnotations();
    }

    public List<Class<?>> getGenericType() {
        return ReflectionUtils.getGenericParamsOfFieldType(this.field);
    }

    public String getProviderByTypeVariable(Class<?> typeVariable) {
        this.allClasses.add(typeVariable);
        return typeVariable.getCanonicalName() + "Fixtures.create" + typeVariable.getSimpleName() + "()";
    }
}
