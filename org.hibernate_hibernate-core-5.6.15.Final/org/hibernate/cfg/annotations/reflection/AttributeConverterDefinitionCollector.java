/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations.reflection;

import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;

public interface AttributeConverterDefinitionCollector {
    public void addAttributeConverter(AttributeConverterInfo var1);

    public void addAttributeConverter(ConverterDescriptor var1);
}

