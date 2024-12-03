/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class TypedValue {
    public static final TypedValue NULL = new TypedValue(null);
    @Nullable
    private final Object value;
    @Nullable
    private TypeDescriptor typeDescriptor;

    public TypedValue(@Nullable Object value) {
        this.value = value;
        this.typeDescriptor = null;
    }

    public TypedValue(@Nullable Object value, @Nullable TypeDescriptor typeDescriptor) {
        this.value = value;
        this.typeDescriptor = typeDescriptor;
    }

    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Nullable
    public TypeDescriptor getTypeDescriptor() {
        if (this.typeDescriptor == null && this.value != null) {
            this.typeDescriptor = TypeDescriptor.forObject((Object)this.value);
        }
        return this.typeDescriptor;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TypedValue)) {
            return false;
        }
        TypedValue otherTv = (TypedValue)other;
        return ObjectUtils.nullSafeEquals((Object)this.value, (Object)otherTv.value) && (this.typeDescriptor == null && otherTv.typeDescriptor == null || ObjectUtils.nullSafeEquals((Object)this.getTypeDescriptor(), (Object)otherTv.getTypeDescriptor()));
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.value);
    }

    public String toString() {
        return "TypedValue: '" + this.value + "' of [" + this.getTypeDescriptor() + "]";
    }
}

