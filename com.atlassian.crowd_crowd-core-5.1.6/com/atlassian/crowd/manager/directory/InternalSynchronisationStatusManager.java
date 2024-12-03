/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import java.util.Collection;

public interface InternalSynchronisationStatusManager
extends SynchronisationStatusManager {
    public Collection<DirectorySynchronisationStatus> getStalledSynchronizations();
}

