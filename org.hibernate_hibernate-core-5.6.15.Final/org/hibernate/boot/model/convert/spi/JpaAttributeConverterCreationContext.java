/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.convert.spi;

import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.type.descriptor.java.spi.JavaTypeDescriptorRegistry;

public interface JpaAttributeConverterCreationContext {
    public ManagedBeanRegistry getManagedBeanRegistry();

    public JavaTypeDescriptorRegistry getJavaTypeDescriptorRegistry();
}

