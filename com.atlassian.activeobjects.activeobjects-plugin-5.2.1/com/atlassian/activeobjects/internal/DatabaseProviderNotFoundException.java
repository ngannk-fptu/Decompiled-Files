/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ActiveObjectsPluginException;

public class DatabaseProviderNotFoundException
extends ActiveObjectsPluginException {
    private final String driverClassName;

    public DatabaseProviderNotFoundException(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    @Override
    public String getMessage() {
        return "Could not find database provider for data source which uses JDBC driver <" + this.driverClassName + ">";
    }
}

