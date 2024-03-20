package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.List;
import java.util.Objects;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class EnumAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return context.getUnwrappedFieldTypeRaw().isEnum();
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        String nameAccess = "%s.name()".formatted(context.getValueAccess());

        return List.of(
                // fieldIsEqualToByName
                assertionMethod(
                        "is%sEqualToByName".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (!$T.equals($L, expected))", ClassName.get(Objects.class), nameAccess)
                                    .addStatement(failStatement(context, "equal to <%s>"))
                                .endControlFlow()
                ),

                // fieldIsNotEqualToByName
                assertionMethod(
                        "is%sNotEqualToByName".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.equals($L, expected))", ClassName.get(Objects.class), nameAccess)
                                    .addStatement(failStatement(context, "not equal to <%s>"))
                                .endControlFlow()
                )
        );
    }
}
