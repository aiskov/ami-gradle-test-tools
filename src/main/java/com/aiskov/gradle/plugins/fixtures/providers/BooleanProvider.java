package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

public class BooleanProvider implements ValueProvider {

    @Override
    public boolean isApplicable(FieldContext context) {
        return Boolean.class.isAssignableFrom(context.getType());
    }

    @Override
    public String getProviderCode(FieldContext context) {
        return "false";
    }
}
