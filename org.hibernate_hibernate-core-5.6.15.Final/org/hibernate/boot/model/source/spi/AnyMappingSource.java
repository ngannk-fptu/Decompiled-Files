/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.AnyDiscriminatorSource;
import org.hibernate.boot.model.source.spi.AnyKeySource;

public interface AnyMappingSource {
    public AnyDiscriminatorSource getDiscriminatorSource();

    public AnyKeySource getKeySource();

    default public boolean isLazy() {
        return true;
    }
}

