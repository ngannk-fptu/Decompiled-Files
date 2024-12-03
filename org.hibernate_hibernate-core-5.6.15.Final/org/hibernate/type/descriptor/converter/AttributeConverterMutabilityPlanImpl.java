/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.converter;

import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;

public class AttributeConverterMutabilityPlanImpl<T>
extends MutableMutabilityPlan<T> {
    private final JpaAttributeConverter converter;

    public AttributeConverterMutabilityPlanImpl(JpaAttributeConverter converter) {
        this.converter = converter;
    }

    @Override
    protected T deepCopyNotNull(T value) {
        return (T)this.converter.toDomainValue(this.converter.toRelationalValue(value));
    }
}

