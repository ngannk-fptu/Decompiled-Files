/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;

public interface MetadataBuilderImplementor
extends MetadataBuilder {
    public BootstrapContext getBootstrapContext();

    public MetadataBuildingOptions getMetadataBuildingOptions();
}

