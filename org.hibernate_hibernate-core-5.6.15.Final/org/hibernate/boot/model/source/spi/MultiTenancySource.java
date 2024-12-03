/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.RelationalValueSource;

public interface MultiTenancySource {
    public RelationalValueSource getRelationalValueSource();

    public boolean isShared();

    public boolean bindAsParameter();
}

