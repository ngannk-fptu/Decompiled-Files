/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.apache.commons.lang3.ArraySorter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffExclude;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionDiffBuilder<T>
implements Builder<DiffResult<T>> {
    private final T left;
    private final T right;
    private final DiffBuilder<T> diffBuilder;
    private String[] excludeFieldNames;

    public ReflectionDiffBuilder(T lhs, T rhs, ToStringStyle style) {
        this.left = lhs;
        this.right = rhs;
        this.diffBuilder = new DiffBuilder<T>(lhs, rhs, style);
    }

    public String[] getExcludeFieldNames() {
        return (String[])this.excludeFieldNames.clone();
    }

    public ReflectionDiffBuilder<T> setExcludeFieldNames(String ... excludeFieldNamesParam) {
        this.excludeFieldNames = excludeFieldNamesParam == null ? ArrayUtils.EMPTY_STRING_ARRAY : ArraySorter.sort(ReflectionToStringBuilder.toNoNullStringArray(excludeFieldNamesParam));
        return this;
    }

    @Override
    public DiffResult<T> build() {
        if (this.left.equals(this.right)) {
            return this.diffBuilder.build();
        }
        this.appendFields(this.left.getClass());
        return this.diffBuilder.build();
    }

    private void appendFields(Class<?> clazz) {
        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (!this.accept(field)) continue;
            try {
                this.diffBuilder.append(field.getName(), FieldUtils.readField(field, this.left, true), FieldUtils.readField(field, this.right, true));
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unexpected IllegalAccessException: " + e.getMessage(), e);
            }
        }
    }

    private boolean accept(Field field) {
        if (field.getName().indexOf(36) != -1) {
            return false;
        }
        if (Modifier.isTransient(field.getModifiers())) {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if (this.excludeFieldNames != null && Arrays.binarySearch(this.excludeFieldNames, field.getName()) >= 0) {
            return false;
        }
        return !field.isAnnotationPresent(DiffExclude.class);
    }
}

