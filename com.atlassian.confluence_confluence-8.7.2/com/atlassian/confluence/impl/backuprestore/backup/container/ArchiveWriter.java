/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.parallel.InputStreamSupplier
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import java.io.InputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;

public interface ArchiveWriter
extends AutoCloseable {
    public void compressFromStream(InputStream var1, String var2);

    public void compressFromStream(InputStream var1, String var2, String var3);

    public void compressFromStreamSupplier(InputStreamSupplier var1, String var2);

    public void compressFromStreamSupplier(InputStreamSupplier var1, String var2, String var3);

    @Override
    public void close() throws BackupRestoreException;
}

