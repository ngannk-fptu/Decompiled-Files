/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations.reflection;

import java.net.URL;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.MetadataBuildingOptions;

public class ClassLoaderAccessLazyImpl
implements ClassLoaderAccess {
    private final MetadataBuildingOptions metadataBuildingOptions;

    public ClassLoaderAccessLazyImpl(MetadataBuildingOptions metadataBuildingOptions) {
        this.metadataBuildingOptions = metadataBuildingOptions;
    }

    @Override
    public <T> Class<T> classForName(String name) {
        return null;
    }

    @Override
    public URL locateResource(String resourceName) {
        return null;
    }
}

