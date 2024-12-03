/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.internal;

import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;

public class MetadataBuildingContextRootImpl
implements MetadataBuildingContext {
    private final BootstrapContext bootstrapContext;
    private final MetadataBuildingOptions options;
    private final MappingDefaults mappingDefaults;
    private final InFlightMetadataCollector metadataCollector;
    private final ObjectNameNormalizer objectNameNormalizer;

    public MetadataBuildingContextRootImpl(BootstrapContext bootstrapContext, MetadataBuildingOptions options, InFlightMetadataCollector metadataCollector) {
        this.bootstrapContext = bootstrapContext;
        this.options = options;
        this.mappingDefaults = options.getMappingDefaults();
        this.metadataCollector = metadataCollector;
        this.objectNameNormalizer = new ObjectNameNormalizer(){

            @Override
            protected MetadataBuildingContext getBuildingContext() {
                return MetadataBuildingContextRootImpl.this;
            }
        };
    }

    @Override
    public BootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }

    @Override
    public MetadataBuildingOptions getBuildingOptions() {
        return this.options;
    }

    @Override
    public MappingDefaults getMappingDefaults() {
        return this.mappingDefaults;
    }

    @Override
    public InFlightMetadataCollector getMetadataCollector() {
        return this.metadataCollector;
    }

    @Override
    public ClassLoaderAccess getClassLoaderAccess() {
        return this.bootstrapContext.getClassLoaderAccess();
    }

    @Override
    public ObjectNameNormalizer getObjectNameNormalizer() {
        return this.objectNameNormalizer;
    }
}

