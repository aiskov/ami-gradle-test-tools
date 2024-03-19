package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.List;
import java.util.function.Consumer;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class RecursionAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return true;
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return List.of(
                assertionMethod(
                        "is%sSatisfying".formatted(context.getFieldName()),
                        ParameterizedTypeName.get(ClassName.get(Consumer.class), context.getUnwrappedFieldType()),
                        context.getSelfClass(),
                                builder -> builder
                                        .beginControlFlow("try")
                                        .addStatement("expected.accept($L)", context.getValueAccess())
                                        .nextControlFlow("catch (AssertionError err)")
                                        .addStatement("System.err.println(err.getMessage())")
                                        .addStatement(failStatement(context, "satisfying the given condition"))
                                        .endControlFlow()
                )
        );
    }
}
