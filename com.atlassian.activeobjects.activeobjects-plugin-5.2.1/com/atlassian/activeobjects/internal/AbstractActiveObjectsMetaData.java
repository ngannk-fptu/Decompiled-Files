/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ActiveObjectsModuleMetaData;
import com.atlassian.activeobjects.spi.DatabaseType;

public abstract class AbstractActiveObjectsMetaData
implements ActiveObjectsModuleMetaData {
    private final DatabaseType databaseType;

    protected AbstractActiveObjectsMetaData(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }
}

