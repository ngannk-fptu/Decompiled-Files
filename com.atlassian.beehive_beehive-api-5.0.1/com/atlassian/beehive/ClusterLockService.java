/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.beehive;

import com.atlassian.annotations.PublicApi;
import com.atlassian.beehive.ClusterLock;
import javax.annotation.Nonnull;

@PublicApi
public interface ClusterLockService {
    public ClusterLock getLockForName(@Nonnull String var1);
}

