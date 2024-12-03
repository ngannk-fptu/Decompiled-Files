/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public interface DirectorySynchronisationInformationStore {
    @Nullable
    public DirectorySynchronisationRoundInformation getActive(long var1);

    public Optional<DirectorySynchronisationRoundInformation> getLast(long var1);

    public void clear(long var1);

    public void clear();

    public void syncStatus(long var1, String var3, List<Serializable> var4);

    public void syncStatus(long var1, SynchronisationStatusKey var3, List<Serializable> var4);

    public void syncStarted(long var1, long var3);

    default public void syncFailure(long directoryId, SynchronisationMode syncMode, String failureReason) {
        throw new UnsupportedOperationException();
    }

    public void syncFinished(long var1, long var3, SynchronisationStatusKey var5, List<Serializable> var6);

    public Collection<DirectorySynchronisationStatus> getStalledSynchronizations();
}

