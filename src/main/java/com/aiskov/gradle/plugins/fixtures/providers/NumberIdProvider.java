package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

public class NumberIdProvider implements ValueProvider {

    @Override
    public boolean isApplicable(FieldContext context) {
        return context.getName().toLowerCase().endsWith("id")
                && Number.class.isAssignableFrom(context.getType());
    }

    @Override
    public String getProviderCode(FieldContext context) {
        Class<?> type = context.getType();

        if (Integer.class.isAssignableFrom(type)) {
            return "(int) COUNTER.incrementAndGet()";
        }

        if (Long.class.isAssignableFrom(type)) {
            return "COUNTER.incrementAndGet()";
        }

        return null;
    }
}
