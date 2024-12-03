/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.Directory
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.directory;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import javax.annotation.Nullable;

public interface DirectorySynchronisationStatus {
    public Integer getId();

    public Directory getDirectory();

    public long getStartTimestamp();

    @Nullable
    public Long getEndTimestamp();

    public SynchronisationStatusKey getStatus();

    public String getStatusParameters();

    @ExperimentalApi
    public String getIncrementalSyncError();

    @ExperimentalApi
    public String getFullSyncError();

    public String getNodeId();

    @ExperimentalApi
    public String getNodeName();
}

