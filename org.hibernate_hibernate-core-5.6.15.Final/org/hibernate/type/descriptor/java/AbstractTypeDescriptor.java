/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.internal.util.compare.ComparableComparator;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.MutabilityPlan;

public abstract class AbstractTypeDescriptor<T>
implements BasicJavaDescriptor<T>,
Serializable {
    private final Class<T> type;
    private final MutabilityPlan<T> mutabilityPlan;
    private final Comparator<T> comparator;

    protected AbstractTypeDescriptor(Class<T> type) {
        this(type, ImmutableMutabilityPlan.INSTANCE);
    }

    protected AbstractTypeDescriptor(Class<T> type, MutabilityPlan<T> mutabilityPlan) {
        this.type = type;
        this.mutabilityPlan = mutabilityPlan;
        this.comparator = Comparable.class.isAssignableFrom(type) ? ComparableComparator.INSTANCE : null;
    }

    @Override
    public MutabilityPlan<T> getMutabilityPlan() {
        return this.mutabilityPlan;
    }

    @Override
    public Class<T> getJavaType() {
        return this.type;
    }

    @Override
    @Deprecated
    public Class<T> getJavaTypeClass() {
        return this.getJavaType();
    }

    @Override
    public int extractHashCode(T value) {
        return value.hashCode();
    }

    @Override
    public boolean areEqual(T one, T another) {
        return Objects.equals(one, another);
    }

    @Override
    public Comparator<T> getComparator() {
        return this.comparator;
    }

    @Override
    public String extractLoggableRepresentation(T value) {
        return value == null ? "null" : value.toString();
    }

    protected HibernateException unknownUnwrap(Class conversionType) {
        throw new HibernateException("Unknown unwrap conversion requested: " + this.type.getName() + " to " + conversionType.getName());
    }

    protected HibernateException unknownWrap(Class conversionType) {
        throw new HibernateException("Unknown wrap conversion requested: " + conversionType.getName() + " to " + this.type.getName());
    }
}

