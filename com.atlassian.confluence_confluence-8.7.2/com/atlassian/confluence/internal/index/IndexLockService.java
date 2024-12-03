/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index;

import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public interface IndexLockService {
    public boolean tryLock(SearchIndex var1, long var2, TimeUnit var4);

    public boolean tryLock(EnumSet<SearchIndex> var1, long var2, TimeUnit var4);

    public void lock(SearchIndex var1);

    public void lock(EnumSet<SearchIndex> var1);

    public void unlock(SearchIndex var1);

    public void unlock(EnumSet<SearchIndex> var1);
}

