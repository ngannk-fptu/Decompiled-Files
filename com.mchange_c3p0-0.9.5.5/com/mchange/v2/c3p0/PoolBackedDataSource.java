/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;

public final class PoolBackedDataSource
extends AbstractPoolBackedDataSource
implements PooledDataSource {
    public PoolBackedDataSource(boolean autoregister) {
        super(autoregister);
    }

    public PoolBackedDataSource() {
        this(true);
    }

    public PoolBackedDataSource(String configName) {
        this();
        this.initializeNamedConfig(configName, false);
    }

    @Override
    public String toString(boolean show_config) {
        return this.toString();
    }
}

