package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class CollectionAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return Collection.class.isAssignableFrom(context.getFieldTypeRaw());
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        ParameterizedTypeName collectionType = ParameterizedTypeName.get(context.getFieldType(), context.getUnwrappedFieldType());
        ParameterizedTypeName itemConsumerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), context.getUnwrappedFieldType());

        return List.of(
                // is<FieldName>Empty
                assertionMethod(
                        "is%sEmpty".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value != null && ! value.isEmpty())")
                                .addStatement(failStatement(context, "is empty"))
                                .endControlFlow()
                ),

                // is<FieldName>NotEmpty
                assertionMethod(
                        "is%sNotEmpty".formatted(context.getFieldName()),
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || value.isEmpty())")
                                .addStatement(failStatement(context, "is not empty"))
                                .endControlFlow()
                ),

                // is<FieldName>HasSize
                assertionMethod(
                        "is%sHasSize".formatted(context.getFieldName()),
                        ClassName.INT,
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (! (value != null && value.size() != expected) || (value == null && expected == 0))")
                                .addStatement(failStatement(context, "has size %s"))
                                .endControlFlow()
                ),

                // is<FieldName>FirstItemSatisfy
                assertionMethod(
                        "is%sFirstItemSatisfy".formatted(context.getFieldName()),
                        itemConsumerType,
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || value.isEmpty())")
                                .addStatement(failStatement(context, "expected not empty collection"))
                                .endControlFlow()
                                .addStatement("$T firstItem = value.iterator().next()", context.getUnwrappedFieldType())
                                .beginControlFlow("try")
                                .addStatement("expected.accept(firstItem)")
                                .nextControlFlow("catch (AssertionError err)")
                                .addStatement("System.err.println(err.getMessage())")
                                .addStatement(failStatement(context, "first item satisfying the given condition"))
                                .endControlFlow()
                ),

                // is<FieldName>LastItemSatisfy
                assertionMethod(
                        "is%sLastItemSatisfy".formatted(context.getFieldName()),
                        itemConsumerType,
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || value.isEmpty())")
                                .addStatement(failStatement(context, "expected not empty collection"))
                                .endControlFlow()
                                .addStatement("$T lastItem = new $T<>(value).getLast()", context.getUnwrappedFieldType(), ClassName.get(LinkedList.class))
                                .beginControlFlow("try")
                                .addStatement("expected.accept(lastItem)")
                                .nextControlFlow("catch (AssertionError err)")
                                .addStatement("System.err.println(err.getMessage())")
                                .addStatement(failStatement(context, "last item satisfying the given condition"))
                                .endControlFlow()
                ),

                // is<FieldName>Contains
                assertionMethod(
                        "is%sContains".formatted(context.getFieldName()),
                        context.getUnwrappedFieldType(),
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || ! value.contains(expected))")
                                .addStatement(failStatement(context, "contains %s"))
                                .endControlFlow()
                ),

                // is<FieldName>NotContains
                assertionMethod(
                        "is%sNotContains".formatted(context.getFieldName()),
                        context.getUnwrappedFieldType(),
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value != null && value.contains(expected))")
                                .addStatement(failStatement(context, "not contains %s"))
                                .endControlFlow()
                ),

                // is<FieldName>ContainsAll
                assertionMethod(
                        "is%sContainsAll".formatted(context.getFieldName()),
                        ParameterizedTypeName.get(ClassName.get(Collection.class), context.getUnwrappedFieldType()),
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || ! value.containsAll(expected))")
                                .addStatement(failStatement(context, "contains all %s"))
                                .endControlFlow()
                ),

                // isAll<FieldName>Satisfy
                assertionMethod(
                        "isAll%sSatisfy".formatted(context.getFieldName()),
                        itemConsumerType,
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || value.isEmpty())")
                                    .addStatement(failStatement(context, "expected not empty collection"))
                                .endControlFlow()
                                .beginControlFlow("for ($T item : value)", context.getUnwrappedFieldType())
                                    .beginControlFlow("try")
                                        .addStatement("expected.accept(item)")
                                    .nextControlFlow("catch (AssertionError err)")
                                        .addStatement("System.err.println(err.getMessage())")
                                        .addStatement(failStatement(context, "all items satisfying the given condition"))
                                    .endControlFlow()
                                .endControlFlow()
                ),

                // isAny<FieldName>Satisfy
                assertionMethod(
                        "isAny%sSatisfy".formatted(context.getFieldName()),
                        itemConsumerType,
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", collectionType, context.getValueAccess())
                                .beginControlFlow("if (value == null || value.isEmpty())")
                                    .addStatement(failStatement(context, "expected not empty collection"))
                                .endControlFlow()
                                .beginControlFlow("for ($T item : value)", context.getUnwrappedFieldType())
                                    .beginControlFlow("try")
                                        .addStatement("expected.accept(item)")
                                        .addStatement("return this")
                                    .nextControlFlow("catch (AssertionError err)")
                                        .addStatement("System.err.println(err.getMessage())")
                                    .endControlFlow()
                                .endControlFlow()
                                .addStatement(failStatement(context, "any item satisfying the given condition"))
                )
        );
    }
}
