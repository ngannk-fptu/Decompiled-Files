/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import java.util.Collection;

public interface Subscriber {
    public void onMonitoredObjectsExport(Class<?> var1, Collection<Object> var2) throws InterruptedException, BackupRestoreException;

    public Collection<Class<?>> getWatchingEntityClasses();
}

