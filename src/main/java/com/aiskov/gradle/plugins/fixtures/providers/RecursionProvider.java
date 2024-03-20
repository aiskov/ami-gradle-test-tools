package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

import java.util.stream.Stream;

public class RecursionProvider implements ValueProvider {

    @Override
    public boolean isApplicable(FieldContext context) {
        return Stream.of(context.getType().getMethods()).anyMatch(method -> "builder".equals(method.getName()));
    }

    @Override
    public String getProviderCode(FieldContext context) {
        return context.getProviderByTypeVariable(context.getType());
    }
}
