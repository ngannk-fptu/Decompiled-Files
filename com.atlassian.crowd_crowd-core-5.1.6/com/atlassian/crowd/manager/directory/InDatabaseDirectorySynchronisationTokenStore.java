/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.DirectorySynchronisationTokenDao
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.spi.DirectorySynchronisationTokenDao;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationTokenStore;
import javax.annotation.Nullable;

public class InDatabaseDirectorySynchronisationTokenStore
implements DirectorySynchronisationTokenStore {
    private final DirectorySynchronisationTokenDao directorySynchronisationTokenDao;

    public InDatabaseDirectorySynchronisationTokenStore(DirectorySynchronisationTokenDao directorySynchronisationTokenDao) {
        this.directorySynchronisationTokenDao = directorySynchronisationTokenDao;
    }

    @Override
    @Nullable
    public String getLastSynchronisationTokenForDirectory(long directoryId) {
        return this.directorySynchronisationTokenDao.getLastSynchronisationTokenForDirectory(directoryId);
    }

    @Override
    public void storeSynchronisationTokenForDirectory(long directoryId, String syncStatus) {
        try {
            this.directorySynchronisationTokenDao.storeSynchronisationTokenForDirectory(directoryId, syncStatus);
        }
        catch (DirectoryNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearSynchronisationTokenForDirectory(long directoryId) {
        this.directorySynchronisationTokenDao.clearSynchronisationTokenForDirectory(directoryId);
    }
}

