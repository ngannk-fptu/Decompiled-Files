/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface CacheSettings {
    @Nullable
    public Long getExpireAfterAccess();

    public long getExpireAfterAccess(long var1);

    @Nullable
    public Long getExpireAfterWrite();

    public long getExpireAfterWrite(long var1);

    @Nullable
    public Boolean getFlushable();

    public boolean getFlushable(boolean var1);

    @Nullable
    public Boolean getLocal();

    public boolean getLocal(boolean var1);

    @Nullable
    public Integer getMaxEntries();

    public int getMaxEntries(int var1);

    @Nullable
    public Boolean getReplicateAsynchronously();

    public boolean getReplicateAsynchronously(boolean var1);

    @Nullable
    public Boolean getReplicateViaCopy();

    public boolean getReplicateViaCopy(boolean var1);

    @Nullable
    public Boolean getStatisticsEnabled();

    public boolean getStatisticsEnabled(boolean var1);

    @Nonnull
    public CacheSettings override(@Nonnull CacheSettings var1);
}

