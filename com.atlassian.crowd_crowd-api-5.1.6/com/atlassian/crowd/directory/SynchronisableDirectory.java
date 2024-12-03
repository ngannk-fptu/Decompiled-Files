/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;

public interface SynchronisableDirectory
extends RemoteDirectory {
    public boolean isIncrementalSyncEnabled();

    public void synchroniseCache(SynchronisationMode var1, SynchronisationStatusManager var2) throws OperationFailedException;
}

