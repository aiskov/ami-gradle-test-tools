package com.aiskov.gradle.plugins.fixtures.providers;

import com.aiskov.gradle.plugins.fixtures.FieldContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CollectionProvider implements ValueProvider {
    private static final int MAX_ITEMS = 5;
    private static final int MIN_ITEMS = 2;

    @Override
    public boolean isApplicable(FieldContext context) {
        return Collection.class.isAssignableFrom(context.getType());
    }

    @Override
    public String getProviderCode(FieldContext context) {
        String itemGenerator = context.getProviderByTypeVariable(context.getGenericType().get(0));

        String create = "java.util.stream.Stream.generate(() -> %s).limit(RANDOM.nextInt(%s) + %s)"
                .formatted(itemGenerator, MAX_ITEMS - MIN_ITEMS, MIN_ITEMS);

        if (List.class.isAssignableFrom(context.getType())) {
            return create + ".collect(java.util.stream.Collectors.toList())";
        }

        if (Set.class.isAssignableFrom(context.getType())) {
            return create + ".collect(java.util.stream.Collectors.toSet())";
        }

        return null;
    }
}
