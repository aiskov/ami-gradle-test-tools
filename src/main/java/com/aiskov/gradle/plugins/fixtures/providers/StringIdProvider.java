package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

public class StringIdProvider implements ValueProvider {

    @Override
    public boolean isApplicable(FieldContext context) {
        return context.getName().toLowerCase().endsWith("id")
                && String.class.isAssignableFrom(context.getType());
    }

    @Override
    public String getProviderCode(FieldContext context) {
        return "java.util.UUID.randomUUID().toString()";
    }
}
