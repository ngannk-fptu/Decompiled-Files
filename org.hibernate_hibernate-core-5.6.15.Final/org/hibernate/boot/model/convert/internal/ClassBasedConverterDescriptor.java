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

public class ClassBasedConverterDescriptor
extends AbstractConverterDescriptor {
    public ClassBasedConverterDescriptor(Class<? extends AttributeConverter> converterClass, ClassmateContext classmateContext) {
        super(converterClass, null, classmateContext);
    }

    public ClassBasedConverterDescriptor(Class<? extends AttributeConverter> converterClass, Boolean forceAutoApply, ClassmateContext classmateContext) {
        super(converterClass, forceAutoApply, classmateContext);
    }

    @Override
    protected ManagedBean<? extends AttributeConverter> createManagedBean(JpaAttributeConverterCreationContext context) {
        return context.getManagedBeanRegistry().getBean(this.getAttributeConverterClass());
    }
}

