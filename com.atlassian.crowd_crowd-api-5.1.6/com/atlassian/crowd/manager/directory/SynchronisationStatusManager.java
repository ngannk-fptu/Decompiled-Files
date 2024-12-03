/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

public interface SynchronisationStatusManager {
    public void syncStarted(Directory var1);

    @Deprecated
    public void syncStatus(long var1, String var3, Serializable ... var4);

    public void syncStatus(long var1, SynchronisationStatusKey var3, List<Serializable> var4);

    default public void syncFailure(long directoryId, SynchronisationMode mode, Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void syncFinished(long var1);

    public void syncFinished(long var1, SynchronisationStatusKey var3, List<Serializable> var4);

    public void removeStatusesForDirectory(long var1);

    @Deprecated
    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(Directory var1);

    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long var1) throws DirectoryNotFoundException;

    @Nullable
    public String getLastSynchronisationTokenForDirectory(long var1);

    public void storeSynchronisationTokenForDirectory(long var1, String var3);

    public void clearSynchronisationTokenForDirectory(long var1);
}

