/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.directory;

import javax.annotation.Nullable;

public interface DirectorySynchronisationTokenStore {
    @Nullable
    public String getLastSynchronisationTokenForDirectory(long var1);

    public void storeSynchronisationTokenForDirectory(long var1, String var3);

    public void clearSynchronisationTokenForDirectory(long var1);
}

