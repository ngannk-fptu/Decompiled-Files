/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.spi.DatabaseType
 */
package com.atlassian.oauth2.provider.core.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;

public abstract class OAuth2ProviderDao {
    protected final ActiveObjects activeObjects;

    protected OAuth2ProviderDao(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    protected String whereColumnIsGreaterThanValue(String column) {
        return "? > (" + this.quotes() + column + this.quotes() + " + ?)";
    }

    private String quotes() {
        return this.addQuotes() ? "\"" : "";
    }

    private boolean addQuotes() {
        return SystemProperty.DO_NOT_USE_QUOTES_IN_SQL.getValue() == false && (SystemProperty.USE_QUOTES_IN_SQL.getValue() != false || DatabaseType.POSTGRESQL == this.activeObjects.moduleMetaData().getDatabaseType());
    }
}

