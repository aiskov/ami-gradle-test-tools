package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.assertionMethod;
import static com.aiskov.gradle.plugins.asserts.creators.AssertionCreator.failStatement;

public class TemporalAssertionCreator implements AssertionCreator {
    @Override
    public boolean isApplicable(AssertionFieldContext context) {
        return Temporal.class.isAssignableFrom(context.getUnwrappedFieldTypeRaw());
    }

    @Override
    public List<MethodSpec> create(AssertionFieldContext context) {
        return Stream.of(
                assertionMethod(
                        "is%sEqualToString".formatted(context.getFieldName()),
                        ClassName.get(String.class),
                        context.getSelfClass(),
                        builder -> builder
                                .addStatement("$T value = $L", context.getUnwrappedFieldType(), context.getValueAccess())
                                .addStatement("String strValue = value == null ? null : value.toString()")
                                .beginControlFlow("if (!$T.equals(value, expected))", ClassName.get(Objects.class))
                                    .addStatement(failStatement(context, "equal to <%s>"))
                                .endControlFlow()
                ),

                isEqualToStringUsingFormatter(context)
        ).filter(Objects::nonNull).toList();
    }

    private MethodSpec isEqualToStringUsingFormatter(AssertionFieldContext context) {
        if (Instant.class.isAssignableFrom(context.getUnwrappedFieldTypeRaw())) return null;

        Map<String, TypeName> params = new LinkedHashMap<>();
        params.put("expected", ClassName.get(String.class));
        params.put("formatter", ClassName.get(DateTimeFormatter.class));

        return assertionMethod(
                "is%sEqualToString".formatted(context.getFieldName()),
                params,
                context.getSelfClass(),
                builder -> builder
                        .addStatement("$T value = $L", context.getUnwrappedFieldType(), context.getValueAccess())
                        .addStatement("String strValue = value == null ? null : value.format(formatter)")
                        .beginControlFlow("if (!$T.equals(strValue, expected))", ClassName.get(Objects.class))
                            .addStatement(failStatement(context, "equal to <%s>"))
                        .endControlFlow()
        );
    }
}
