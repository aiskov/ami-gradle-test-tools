package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class ComparableAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return ! context.getUnwrappedFieldTypeRaw().isEnum() && (
                Comparable.class.isAssignableFrom(context.getFieldTypeRaw()) ||
                        Optional.class.isAssignableFrom(context.getFieldTypeRaw())
                                && Comparable.class.isAssignableFrom(context.getUnwrappedFieldTypeRaw())
        );
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return List.of(
                // fieldIsLessThan
                assertionMethod(
                        "is%sLessThan".formatted(context.getFieldName()),
                        context.getUnwrappedFieldType(),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.compare($L, expected, $T.naturalOrder()) >= 0)",
                                        ClassName.get(Objects.class), context.getValueAccess(), ClassName.get(Comparator.class))
                                    .addStatement(failStatement(context, "less than <%s>"))
                                .endControlFlow()
                ),

                // fieldIsLessThanOrEqualTo
                assertionMethod(
                        "is%sLessThanOrEqualTo".formatted(context.getFieldName()),
                        context.getUnwrappedFieldType(),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.compare($L, expected, $T.naturalOrder()) > 0)",
                                        ClassName.get(Objects.class), context.getValueAccess(), ClassName.get(Comparator.class))
                                    .addStatement(failStatement(context, "less than or equal to <%s>"))
                                .endControlFlow()
                ),

                // fieldIsGreaterThan
                assertionMethod(
                        "is%sGreaterThan".formatted(context.getFieldName()),
                        context.getUnwrappedFieldType(),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.compare($L, expected, $T.naturalOrder()) <= 0)",
                                        ClassName.get(Objects.class), context.getValueAccess(), ClassName.get(Comparator.class))
                                    .addStatement(failStatement(context, "greater than <%s>"))
                                .endControlFlow()
                ),

                // fieldIsGreaterThanOrEqualTo
                assertionMethod(
                        "is%sGreaterThanOrEqualTo".formatted(context.getFieldName()),
                        context.getUnwrappedFieldType(),
                        context.getSelfClass(),
                        builder -> builder
                                .beginControlFlow("if ($T.compare($L, expected, $T.naturalOrder()) < 0)",
                                        ClassName.get(Objects.class), context.getValueAccess(), ClassName.get(Comparator.class))
                                    .addStatement(failStatement(context, "greater than or equal to <%s>"))
                                .endControlFlow()
                )
        );
    }
}
