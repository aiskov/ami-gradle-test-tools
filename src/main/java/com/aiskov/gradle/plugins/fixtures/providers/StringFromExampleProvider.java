package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class StringFromExampleProvider implements ValueProvider {
    @Override
    public boolean isApplicable(FieldContext context) {
        return String.class.isAssignableFrom(context.getType());
    }

    @Override
    public String getProviderCode(FieldContext context) {
        return exampleFromAnnotation(context);
    }

    @SneakyThrows
    private static String exampleFromAnnotation(FieldContext context) {
        for (Annotation annotation : context.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();

            for (Method method : annotationType.getMethods()) {
                if (! method.getName().equals("example")) continue;
                return "\"%s\"".formatted(method.invoke(annotation));
            }
        }

        return null;
    }

}
