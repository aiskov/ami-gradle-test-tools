package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

public class RandomNumberProvider implements ValueProvider {

    @Override
    public boolean isApplicable(FieldContext context) {
        return Number.class.isAssignableFrom(context.getType())
                || context.getType().isPrimitive();
    }

    @Override
    public String getProviderCode(FieldContext context) {
        Class<?> type = context.getType();

        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return "RANDOM.nextInt()";
        }

        if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return "RANDOM.nextLong()";
        }

        if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return "RANDOM.nextFloat()";
        }

        if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return "RANDOM.nextDouble()";
        }

        return null;
    }
}
