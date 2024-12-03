/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.OperationFailedException
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.exception.OperationFailedException;
import javax.annotation.Nullable;

public interface CacheRefresher {
    public CacheSynchronisationResult synchroniseAll(DirectoryCache var1) throws OperationFailedException;

    public CacheSynchronisationResult synchroniseChanges(DirectoryCache var1, @Nullable String var2) throws OperationFailedException;
}

