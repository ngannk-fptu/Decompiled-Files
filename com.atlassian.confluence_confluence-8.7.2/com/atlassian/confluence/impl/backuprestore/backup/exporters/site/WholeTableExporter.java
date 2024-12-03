/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;

public interface WholeTableExporter {
    public void exportAllRecords() throws BackupRestoreException;

    public String getExporterName();
}

