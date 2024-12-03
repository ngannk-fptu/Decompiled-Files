/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 */
package org.hibernate.boot.model.convert.internal;

import javax.persistence.AttributeConverter;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.model.convert.internal.AbstractConverterDescriptor;
import org.hibernate.boot.model.convert.spi.JpaAttributeConverterCreationContext;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.resource.beans.spi.ProvidedInstanceManagedBeanImpl;

public class InstanceBasedConverterDescriptor
extends AbstractConverterDescriptor {
    private final AttributeConverter converterInstance;

    public InstanceBasedConverterDescriptor(AttributeConverter converterInstance, ClassmateContext classmateContext) {
        this(converterInstance, null, classmateContext);
    }

    public InstanceBasedConverterDescriptor(AttributeConverter converterInstance, Boolean forceAutoApply, ClassmateContext classmateContext) {
        super(converterInstance.getClass(), forceAutoApply, classmateContext);
        this.converterInstance = converterInstance;
    }

    @Override
    protected ManagedBean<? extends AttributeConverter> createManagedBean(JpaAttributeConverterCreationContext context) {
        return new ProvidedInstanceManagedBeanImpl<AttributeConverter>(this.converterInstance);
    }
}

