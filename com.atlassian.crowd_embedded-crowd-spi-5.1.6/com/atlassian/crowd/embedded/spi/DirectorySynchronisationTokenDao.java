/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.embedded.spi;

import com.atlassian.crowd.exception.DirectoryNotFoundException;
import javax.annotation.Nullable;

public interface DirectorySynchronisationTokenDao {
    @Nullable
    public String getLastSynchronisationTokenForDirectory(long var1);

    public void storeSynchronisationTokenForDirectory(long var1, String var3) throws DirectoryNotFoundException;

    public void clearSynchronisationTokenForDirectory(long var1);
}

