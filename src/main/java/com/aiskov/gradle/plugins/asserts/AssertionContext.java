package com.aiskov.gradle.plugins.asserts;

import com.squareup.javapoet.ClassName;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Data
@RequiredArgsConstructor(staticName = "of")
public class AssertionContext {
    private final ClassName targetType;
    private final ClassName selfClass;
    private final List<? extends Class<?>> recursive;

    private final Set<String> processed = new HashSet<>();

    AssertionFieldContext field(String fieldName, Class<?> fieldType, List<Type> genericParams) {
        Class<?> unwrappedFieldType;
        String valueAccess = "actual.get%s()".formatted(fieldName);

        if (Optional.class.isAssignableFrom(fieldType)) {
            unwrappedFieldType = (Class<?>) genericParams.get(0);
            valueAccess += ".orElse(null)";
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            unwrappedFieldType = (Class<?>) genericParams.get(0);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            unwrappedFieldType = (Class<?>) genericParams.get(1);
        } else if (fieldType.isArray()) {
            unwrappedFieldType = fieldType.arrayType();
        } else {
            unwrappedFieldType = fieldType;
        }

        return new AssertionFieldContext(fieldName, fieldType, unwrappedFieldType, valueAccess);
    }

    @RequiredArgsConstructor
    public class AssertionFieldContext {
        @Getter
        private final String fieldName;

        private final Class<?> fieldType;
        private final Class<?> unwrappedFieldType;

        @Getter
        private final String valueAccess;

        public ClassName getTargetType() {
            return targetType;
        }

        public ClassName getSelfClass() {
            return selfClass;
        }

        public ClassName getFieldType() {
            return ClassName.get(fieldType);
        }

        public ClassName getUnwrappedFieldType() {
            return ClassName.get(unwrappedFieldType);
        }

        public Class<?> getFieldTypeRaw() {
            return fieldType;
        }

        public Class<?> getUnwrappedFieldTypeRaw() {
            return unwrappedFieldType;
        }

        public boolean isRecursive() {
            return recursive.contains(this.unwrappedFieldType);
        }

        boolean isAlreadyProcessed() {
            return processed.add(fieldName);
        }
    }
}
