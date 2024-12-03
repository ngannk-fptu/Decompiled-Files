/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.boot.model.convert.spi;

import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;

public interface AutoApplicableConverterDescriptor {
    public ConverterDescriptor getAutoAppliedConverterDescriptorForAttribute(XProperty var1, MetadataBuildingContext var2);

    public ConverterDescriptor getAutoAppliedConverterDescriptorForCollectionElement(XProperty var1, MetadataBuildingContext var2);

    public ConverterDescriptor getAutoAppliedConverterDescriptorForMapKey(XProperty var1, MetadataBuildingContext var2);
}

