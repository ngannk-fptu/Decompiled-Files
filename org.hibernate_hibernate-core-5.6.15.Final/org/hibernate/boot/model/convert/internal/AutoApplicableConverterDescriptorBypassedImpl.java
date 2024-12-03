/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.boot.model.convert.internal;

import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.spi.AutoApplicableConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;

public class AutoApplicableConverterDescriptorBypassedImpl
implements AutoApplicableConverterDescriptor {
    public static final AutoApplicableConverterDescriptorBypassedImpl INSTANCE = new AutoApplicableConverterDescriptorBypassedImpl();

    private AutoApplicableConverterDescriptorBypassedImpl() {
    }

    @Override
    public ConverterDescriptor getAutoAppliedConverterDescriptorForAttribute(XProperty xProperty, MetadataBuildingContext context) {
        return null;
    }

    @Override
    public ConverterDescriptor getAutoAppliedConverterDescriptorForCollectionElement(XProperty xProperty, MetadataBuildingContext context) {
        return null;
    }

    @Override
    public ConverterDescriptor getAutoAppliedConverterDescriptorForMapKey(XProperty xProperty, MetadataBuildingContext context) {
        return null;
    }
}

