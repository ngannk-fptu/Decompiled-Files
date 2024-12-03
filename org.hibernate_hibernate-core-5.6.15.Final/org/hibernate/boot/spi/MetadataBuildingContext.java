/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.boot.spi.MetadataBuildingOptions;

public interface MetadataBuildingContext {
    public BootstrapContext getBootstrapContext();

    public MetadataBuildingOptions getBuildingOptions();

    public MappingDefaults getMappingDefaults();

    public InFlightMetadataCollector getMetadataCollector();

    @Deprecated
    public ClassLoaderAccess getClassLoaderAccess();

    public ObjectNameNormalizer getObjectNameNormalizer();
}

