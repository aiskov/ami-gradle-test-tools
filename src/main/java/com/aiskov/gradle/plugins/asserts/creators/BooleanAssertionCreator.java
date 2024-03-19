package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.MethodSpec;

import java.util.List;
import java.util.Optional;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class BooleanAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return Boolean.class.isAssignableFrom(context.getFieldTypeRaw()) ||
                Optional.class.isAssignableFrom(context.getFieldTypeRaw())
                        && Boolean.class.isAssignableFrom(context.getUnwrappedFieldTypeRaw());
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return List.of(
                assertionMethod(
                        "is%sFalse".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (Boolean.FALSE.equals($L))", context.getValueAccess())
                                .addStatement(failStatement(context, "is False"))
                                .endControlFlow()
                ),

                assertionMethod(
                        "is%sTrue".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (Boolean.TRUE.equals($L))", context.getValueAccess())
                                .addStatement(failStatement(context, "is True"))
                                .endControlFlow()
                )
        );
    }
}
