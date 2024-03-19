package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class CommonAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return true;
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return List.of(
                // fieldIsEqualTo
                assertionMethod(
                        "is%sEqualTo".formatted(context.getFieldName()),
                        ClassName.get(Object.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (!$T.equals($L, expected))", ClassName.get(Objects.class), context.getValueAccess())
                                .addStatement(failStatement(context, "equal to <%s>"))
                                .endControlFlow()
                ),

                // fieldIsNotEqualTo
                assertionMethod(
                        "is%sNotEqualTo".formatted(context.getFieldName()),
                        ClassName.get(Object.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.equals($L, expected))", ClassName.get(Objects.class), context.getValueAccess())
                                .addStatement(failStatement(context, "not equal to <%s>"))
                                .endControlFlow()
                )
        );
    }
}
