/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.BackupProgressMonitor;
import com.atlassian.activeobjects.spi.RestoreProgressMonitor;
import java.io.InputStream;
import java.io.OutputStream;

public interface Backup {
    public void save(OutputStream var1, BackupProgressMonitor var2);

    public void restore(InputStream var1, RestoreProgressMonitor var2);

    public void clear();
}

