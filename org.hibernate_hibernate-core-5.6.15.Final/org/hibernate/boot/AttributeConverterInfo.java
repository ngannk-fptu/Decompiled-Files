/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 */
package org.hibernate.boot;

import javax.persistence.AttributeConverter;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;

public interface AttributeConverterInfo {
    public Class<? extends AttributeConverter> getConverterClass();

    public ConverterDescriptor toConverterDescriptor(MetadataBuildingContext var1);
}

