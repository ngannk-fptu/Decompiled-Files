/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ActiveObjectsImportExportException
 *  com.atlassian.activeobjects.spi.ImportExportException
 *  com.atlassian.activeobjects.spi.PluginInformation
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.backup.PluginInformationFactory;
import com.atlassian.activeobjects.spi.ActiveObjectsImportExportException;
import com.atlassian.activeobjects.spi.ImportExportException;
import com.atlassian.activeobjects.spi.PluginInformation;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.google.common.base.Preconditions;
import java.sql.SQLException;

public final class ImportExportErrorServiceImpl
implements ImportExportErrorService {
    private final PluginInformationFactory pluginInformationFactory;

    public ImportExportErrorServiceImpl(PluginInformationFactory pluginInformationFactory) {
        this.pluginInformationFactory = (PluginInformationFactory)Preconditions.checkNotNull((Object)pluginInformationFactory);
    }

    @Override
    public ImportExportException newImportExportException(String tableName, String message) {
        return new ActiveObjectsImportExportException(tableName, this.getPluginInformation(tableName), message);
    }

    @Override
    public ImportExportException newImportExportSqlException(String tableName, String message, SQLException e) {
        return new ActiveObjectsImportExportException(tableName, this.getPluginInformation(tableName), message, (Throwable)e);
    }

    @Override
    public ImportExportException newRowImportSqlException(String tableName, long rowNum, SQLException e) {
        return new ActiveObjectsImportExportException(tableName, this.getPluginInformation(tableName), "There has been a SQL exception importing row #" + rowNum + " for table '" + tableName + "' see  the cause of this exception for more detail about it.", (Throwable)e);
    }

    @Override
    public ImportExportException newParseException(Throwable t) {
        return new ActiveObjectsImportExportException(null, this.getPluginInformation(null), t);
    }

    @Override
    public ImportExportException newParseException(String message) {
        return new ActiveObjectsImportExportException(null, this.getPluginInformation(null), message);
    }

    @Override
    public ImportExportException newParseException(String message, Throwable t) {
        return new ActiveObjectsImportExportException(null, this.getPluginInformation(null), message, t);
    }

    private PluginInformation getPluginInformation(String tableName) {
        return this.pluginInformationFactory.getPluginInformation(tableName);
    }
}

