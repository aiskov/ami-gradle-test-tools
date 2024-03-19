package com.aiskov.gradle.plugins.asserts.creators;

import com.aiskov.gradle.plugins.asserts.AssertionContext.AssertionFieldContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static javax.lang.model.element.Modifier.PUBLIC;

public interface AssertionCreator {

    boolean isApplicable(AssertionFieldContext context);

    List<MethodSpec> create(AssertionFieldContext context);

    default List<MethodSpec> createIfApplicable(AssertionFieldContext context) {
        if (! isApplicable(context)) return List.of();
        return create(context);
    }

    /**
     * <code>
     *   public SELF nameOfTheMethod(PARAM_TYPE expected) {
     *     isNotNull();
     *     ... body ...
     *     return this;
     *   }
     * </code>
     *
     * <ul>
     *     <li>nameOfTheMethod - Name of the method</li>
     *     <li>SELF - Class that contains assertions</li>
     *     <li>PARAM_TYPE - Type of the parameter</li>
     *     <li>... body ... - Body of the assertion that we get from bodyBuilder</li>
     * </ul>
     *
     * @param name name of the method
     * @param paramType type of the parameter
     * @param selfClass class that contains assertions
     * @param bodyBuilder builder that add assertion logic
     * @return Method specification of assertion without parameters
     */
    static MethodSpec assertionMethod(String name, TypeName paramType, ClassName selfClass, Consumer<MethodSpec.Builder> bodyBuilder) {
        return assertionMethod(name, Map.of("expected", paramType), selfClass, bodyBuilder);
    }

    /**
     * <code>
     *   public SELF nameOfTheMethod() {
     *     isNotNull();
     *     ... body ...
     *     return this;
     *   }
     * </code>
     *
     * <ul>
     *     <li>nameOfTheMethod - Name of the method</li>
     *     <li>SELF - Class that contains assertions</li>
     *     <li>... body ... - Body of the assertion that we get from bodyBuilder</li>
     * </ul>
     *
     * @param name name of the method
     * @param selfClass class that contains assertions
     * @param bodyBuilder builder that add assertion logic
     * @return Method specification of assertion without parameters
     */
    static MethodSpec assertionMethod(String name, ClassName selfClass, Consumer<MethodSpec.Builder> bodyBuilder) {
        return assertionMethod(name, Map.of(), selfClass, bodyBuilder);
    }

    /**
     * <code>
     *   public SELF nameOfTheMethod(PARAM_TYPE_1 PARAM_NAME_1, PARAM_TYPE_2 PARAM_NAME_2, ...) {
     *     isNotNull();
     *     ... body ...
     *     return this;
     *   }
     * </code>
     *
     * <ul>
     *     <li>nameOfTheMethod - Name of the method</li>
     *     <li>SELF - Class that contains assertions</li>
     *     <li>PARAM_TYPE_1 and PARAM_NAME_1 - Passed as map names and types of parameter</li>
     *     <li>... body ... - Body of the assertion that we get from bodyBuilder</li>
     * </ul>
     *
     * @param name name of the method
     * @param paramTypes map of parameter names and types
     * @param selfClass class that contains assertions
     * @param bodyBuilder builder that add assertion logic
     * @return Method specification of assertion without parameters
     */
    static MethodSpec assertionMethod(String name, Map<String, TypeName> paramTypes, ClassName selfClass, Consumer<MethodSpec.Builder> bodyBuilder) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addModifiers(PUBLIC)
                .returns(selfClass)
                .addStatement("isNotNull()");

        if (! paramTypes.isEmpty()) {
            paramTypes.forEach((paramName, paramType) ->
                    builder.addParameter(ParameterSpec.builder(paramType, paramName).build()));
        }

        bodyBuilder.accept(builder);

        return builder
                .addStatement("return this")
                .build();
    }

    static CodeBlock failStatement(AssertionFieldContext context, String message) {
        if (message.contains("<%s>")) {
            return CodeBlock.of("failWithMessage(\"\\n\\nExpected that $L in $L $L but was <%s>\\n\\nActual:\\n%s\\n\", expected, $L, actual)",
                    context.getFieldName(), context.getTargetType().simpleName(), message, context.getValueAccess());
        }

        return CodeBlock.of("failWithMessage(\"\\n\\nExpected that $L in $L $L but was <%s>\\n\\nActual:\\n%s\\n\", $L, actual)",
                context.getFieldName(), context.getTargetType().simpleName(), message, context.getValueAccess());
    }
}
