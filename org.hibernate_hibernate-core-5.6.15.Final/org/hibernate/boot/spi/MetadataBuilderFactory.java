/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.spi.MetadataBuilderImplementor;

public interface MetadataBuilderFactory {
    public MetadataBuilderImplementor getMetadataBuilder(MetadataSources var1, MetadataBuilderImplementor var2);
}

