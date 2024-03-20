package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

public interface ValueProvider {
    boolean isApplicable(FieldContext context);

    String getProviderCode(FieldContext context);
}
