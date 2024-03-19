package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import java.util.List;
import java.util.Optional;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class StringAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return String.class.isAssignableFrom(context.getFieldTypeRaw()) ||
                Optional.class.isAssignableFrom(context.getFieldTypeRaw())
                        && String.class.isAssignableFrom(context.getUnwrappedFieldTypeRaw());
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return List.of(
                // is<FieldName>Empty
                assertionMethod(
                        "is%sEmpty".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (! $T.isEmptyString($L))", ClassName.get("com.gene.common.tools", "CheckUtils"), context.getValueAccess())
                                .addStatement(failStatement(context, "is empty"))
                                .endControlFlow()
                ),

                // is<FieldName>NotEmpty
                assertionMethod(
                        "is%sNotEmpty".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (! $T.isNotEmptyString($L))", ClassName.get("com.gene.common.tools", "CheckUtils"), context.getValueAccess())
                                .addStatement(failStatement(context, "is not empty"))
                                .endControlFlow()
                ),

                // is<FieldName>Blank
                assertionMethod(
                        "is%sBlank".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (! $T.isBlank($L))", ClassName.get("org.apache.commons.lang3", "StringUtils"), context.getValueAccess())
                                .addStatement(failStatement(context, "is blank"))
                                .endControlFlow()
                ),

                // is<FieldName>NotBlank
                assertionMethod(
                        "is%sNotBlank".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (! $T.isNotBlank($L))", ClassName.get("org.apache.commons.lang3", "StringUtils"), context.getValueAccess())
                                .addStatement(failStatement(context, "is not blank"))
                                .endControlFlow()
                ),

                // is<FieldName>EqualToIgnoringCase
                assertionMethod(
                        "is%sEqualToIgnoringCase".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if (! $T.equalsIgnoreCase($L, expected))", ClassName.get("org.apache.commons.lang3", "StringUtils"), context.getValueAccess())
                                .addStatement(failStatement(context, "is equal ignoring case <%s>"))
                                .endControlFlow()
                ),

                // is<FieldName>NotEqualToIgnoringCase
                assertionMethod(
                        "is%sNotEqualToIgnoringCase".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.equalsIgnoreCase($L, expected))", ClassName.get("org.apache.commons.lang3", "StringUtils"), context.getValueAccess())
                                .addStatement(failStatement(context, "is not equal ignoring case <%s>"))
                                .endControlFlow()
                ),

                // is<FieldName>Contains
                assertionMethod(
                        "is%sContains".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($L == null || ! $L.contains(expected))", context.getValueAccess(), context.getValueAccess())
                                .addStatement(failStatement(context, "contains <%s>"))
                                .endControlFlow()
                ),

                // is<FieldName>NotContains
                assertionMethod(
                        "is%sNotContains".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($L != null && $L.contains(expected))", context.getValueAccess(), context.getValueAccess())
                                .addStatement(failStatement(context, "not contains <%s>"))
                                .endControlFlow()
                )
        );
    }
}
