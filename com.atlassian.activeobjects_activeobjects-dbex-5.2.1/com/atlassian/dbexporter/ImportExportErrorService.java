/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ImportExportException
 */
package com.atlassian.dbexporter;

import com.atlassian.activeobjects.spi.ImportExportException;
import java.sql.SQLException;

public interface ImportExportErrorService {
    public ImportExportException newImportExportException(String var1, String var2);

    public ImportExportException newImportExportSqlException(String var1, String var2, SQLException var3);

    public ImportExportException newRowImportSqlException(String var1, long var2, SQLException var4);

    public ImportExportException newParseException(Throwable var1);

    public ImportExportException newParseException(String var1);

    public ImportExportException newParseException(String var1, Throwable var2);
}

