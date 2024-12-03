/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ImportExportException
 *  com.atlassian.dbexporter.ImportExportErrorService
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.activeobjects.spi.ImportExportException;
import com.atlassian.confluence.upgrade.recovery.DbDumpException;
import com.atlassian.dbexporter.ImportExportErrorService;
import java.sql.SQLException;

public class RecoveryErrorService
implements ImportExportErrorService {
    public ImportExportException newImportExportException(String tableName, String message) {
        throw new DbDumpException("Problem with table \"" + tableName + "\": " + message);
    }

    public ImportExportException newImportExportSqlException(String tableName, String message, SQLException e) {
        throw new DbDumpException("Problem with table \"" + tableName + "\": " + message, e);
    }

    public ImportExportException newRowImportSqlException(String tableName, long rowNum, SQLException e) {
        throw new DbDumpException("Problem with table \"" + tableName + "\", row " + rowNum + ":", e);
    }

    public ImportExportException newParseException(Throwable t) {
        return new DbDumpException(t.getMessage(), t);
    }

    public ImportExportException newParseException(String message) {
        return new DbDumpException(message);
    }

    public ImportExportException newParseException(String message, Throwable t) {
        return new DbDumpException(message, t);
    }
}

