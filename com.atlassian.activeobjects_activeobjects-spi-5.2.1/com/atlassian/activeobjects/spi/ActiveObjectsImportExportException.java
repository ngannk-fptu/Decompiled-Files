/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.ImportExportException;
import com.atlassian.activeobjects.spi.PluginInformation;
import java.util.Objects;

public final class ActiveObjectsImportExportException
extends ImportExportException {
    private final PluginInformation pluginInformation;
    private final String tableName;

    public ActiveObjectsImportExportException(String tableName, PluginInformation pluginInformation, String message) {
        super(message);
        this.pluginInformation = Objects.requireNonNull(pluginInformation);
        this.tableName = tableName;
    }

    public ActiveObjectsImportExportException(String tableName, PluginInformation pluginInformation, Throwable t) {
        super(t);
        this.pluginInformation = Objects.requireNonNull(pluginInformation);
        this.tableName = tableName;
    }

    public ActiveObjectsImportExportException(String tableName, PluginInformation pluginInformation, String message, Throwable t) {
        super(message, t);
        this.pluginInformation = Objects.requireNonNull(pluginInformation);
        this.tableName = tableName;
    }

    public PluginInformation getPluginInformation() {
        return this.pluginInformation;
    }

    public String getTableName() {
        return this.tableName;
    }

    @Override
    public String getMessage() {
        return "There was an error during import/export with " + this.pluginInformation + (this.tableName != null ? " (table " + this.tableName + ")" : "") + ":" + super.getMessage();
    }
}

