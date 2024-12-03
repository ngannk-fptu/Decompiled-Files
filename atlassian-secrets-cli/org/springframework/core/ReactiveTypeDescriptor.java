/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ReactiveTypeDescriptor {
    private final Class<?> reactiveType;
    @Nullable
    private final Supplier<?> emptyValueSupplier;
    private final boolean multiValue;
    private final boolean noValue;

    private ReactiveTypeDescriptor(Class<?> reactiveType, @Nullable Supplier<?> emptySupplier, boolean multiValue, boolean noValue) {
        Assert.notNull(reactiveType, "'reactiveType' must not be null");
        this.reactiveType = reactiveType;
        this.emptyValueSupplier = emptySupplier;
        this.multiValue = multiValue;
        this.noValue = noValue;
    }

    public Class<?> getReactiveType() {
        return this.reactiveType;
    }

    public boolean isMultiValue() {
        return this.multiValue;
    }

    public boolean supportsEmpty() {
        return this.emptyValueSupplier != null;
    }

    public boolean isNoValue() {
        return this.noValue;
    }

    public Object getEmptyValue() {
        Assert.state(this.emptyValueSupplier != null, "Empty values not supported");
        return this.emptyValueSupplier.get();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return this.reactiveType.equals(((ReactiveTypeDescriptor)other).reactiveType);
    }

    public int hashCode() {
        return this.reactiveType.hashCode();
    }

    public static ReactiveTypeDescriptor multiValue(Class<?> type, Supplier<?> emptySupplier) {
        return new ReactiveTypeDescriptor(type, emptySupplier, true, false);
    }

    public static ReactiveTypeDescriptor singleOptionalValue(Class<?> type, Supplier<?> emptySupplier) {
        return new ReactiveTypeDescriptor(type, emptySupplier, false, false);
    }

    public static ReactiveTypeDescriptor singleRequiredValue(Class<?> type) {
        return new ReactiveTypeDescriptor(type, null, false, false);
    }

    public static ReactiveTypeDescriptor noValue(Class<?> type, Supplier<?> emptySupplier) {
        return new ReactiveTypeDescriptor(type, emptySupplier, false, true);
    }
}

