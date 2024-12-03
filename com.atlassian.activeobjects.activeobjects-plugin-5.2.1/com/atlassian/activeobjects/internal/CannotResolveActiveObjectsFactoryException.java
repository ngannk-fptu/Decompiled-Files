/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ActiveObjectsPluginException;
import com.atlassian.activeobjects.internal.DataSourceType;

public class CannotResolveActiveObjectsFactoryException
extends ActiveObjectsPluginException {
    private final DataSourceType dataSourceType;

    public CannotResolveActiveObjectsFactoryException(DataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public DataSourceType getDataSourceType() {
        return this.dataSourceType;
    }

    @Override
    public String getMessage() {
        return "Could not resolve active objects factory for data source type <" + (Object)((Object)this.dataSourceType) + ">";
    }
}

