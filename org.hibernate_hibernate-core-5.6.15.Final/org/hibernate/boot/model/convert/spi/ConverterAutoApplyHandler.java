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

public interface ConverterAutoApplyHandler {
    public ConverterDescriptor findAutoApplyConverterForAttribute(XProperty var1, MetadataBuildingContext var2);

    public ConverterDescriptor findAutoApplyConverterForCollectionElement(XProperty var1, MetadataBuildingContext var2);

    public ConverterDescriptor findAutoApplyConverterForMapKey(XProperty var1, MetadataBuildingContext var2);
}

