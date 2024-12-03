/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import org.hibernate.internal.util.compare.ComparableComparator;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public interface JavaTypeDescriptor<T>
extends Serializable {
    @Deprecated
    public Class<T> getJavaTypeClass();

    default public Class<T> getJavaType() {
        return this.getJavaTypeClass();
    }

    default public MutabilityPlan<T> getMutabilityPlan() {
        return ImmutableMutabilityPlan.INSTANCE;
    }

    default public Comparator<T> getComparator() {
        return Comparable.class.isAssignableFrom(Comparable.class) ? ComparableComparator.INSTANCE : null;
    }

    default public int extractHashCode(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Value to extract hashCode from cannot be null");
        }
        return value.hashCode();
    }

    default public boolean areEqual(T one, T another) {
        return Objects.deepEquals(one, another);
    }

    default public String extractLoggableRepresentation(T value) {
        return this.toString(value);
    }

    default public String toString(T value) {
        return value == null ? "null" : value.toString();
    }

    public T fromString(String var1);

    public <X> X unwrap(T var1, Class<X> var2, WrapperOptions var3);

    public <X> T wrap(X var1, WrapperOptions var2);
}

