package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.MethodSpec;

import java.util.List;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class NullableAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return ! context.getFieldTypeRaw().isPrimitive();
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return List.of(
                assertionMethod(
                        "isNull%s".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($L != null)", context.getValueAccess())
                                .addStatement(failStatement(context, "is null"))
                                .endControlFlow()
                ),

                assertionMethod(
                        "isNotNull%s".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($L == null)", context.getValueAccess())
                                .addStatement(failStatement(context, "is not null"))
                                .endControlFlow()
                )
        );
    }
}
