/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.AbstractBackupProgressMonitor;
import com.atlassian.activeobjects.spi.BackupProgressMonitor;

public final class NullBackupProgressMonitor
extends AbstractBackupProgressMonitor {
    public static final BackupProgressMonitor INSTANCE = new NullBackupProgressMonitor();

    private NullBackupProgressMonitor() {
    }
}

