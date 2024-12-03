/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.plugin;

import com.atlassian.confluence.importexport.ImportExportException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BackupRestoreProvider {
    public void backup(OutputStream var1) throws ImportExportException;

    public void restore(InputStream var1) throws ImportExportException;
}

