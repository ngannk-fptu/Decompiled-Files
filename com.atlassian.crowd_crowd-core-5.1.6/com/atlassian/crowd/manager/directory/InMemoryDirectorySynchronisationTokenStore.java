/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.manager.directory.DirectorySynchronisationTokenStore;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Deprecated
public class InMemoryDirectorySynchronisationTokenStore
implements DirectorySynchronisationTokenStore {
    private ConcurrentMap<Long, String> tokens;

    public InMemoryDirectorySynchronisationTokenStore() {
        this(new ConcurrentHashMap<Long, String>());
    }

    public InMemoryDirectorySynchronisationTokenStore(ConcurrentMap<Long, String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public String getLastSynchronisationTokenForDirectory(long directoryId) {
        return (String)this.tokens.get(directoryId);
    }

    @Override
    public void storeSynchronisationTokenForDirectory(long directoryId, String syncStatus) {
        this.tokens.put(directoryId, syncStatus);
    }

    @Override
    public void clearSynchronisationTokenForDirectory(long directoryId) {
        this.tokens.remove(directoryId);
    }
}

