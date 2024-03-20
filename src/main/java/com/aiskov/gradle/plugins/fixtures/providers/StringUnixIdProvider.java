package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

public class StringUnixIdProvider implements ValueProvider {
    @Override
    public boolean isApplicable(FieldContext context) {
        String fieldName = context.getName();
        return String.class.isAssignableFrom(context.getType()) && (
                fieldName.toLowerCase().endsWith("unixid")
                        || fieldName.toLowerCase().endsWith("userid")
                        || fieldName.toLowerCase().endsWith("by")
        );
    }

    @Override
    public String getProviderCode(FieldContext context) {
        return "USER_IDS.get(RANDOM.nextInt(USER_IDS.size()))";
    }
}
