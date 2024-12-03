/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.dao.DataAccessException;

public class CachingJournalStateStore
implements JournalStateStore {
    private final JournalStateStore delegate;
    private final Cache<JournalIdentifier, Long> mostRecentIdCache;

    public CachingJournalStateStore(JournalStateStore delegate, CacheFactory cacheFactory) {
        this.delegate = delegate;
        this.mostRecentIdCache = CoreCache.MOST_RECENT_JOURNAL_ID.getCache(cacheFactory);
    }

    @Override
    public long getMostRecentId(@NonNull JournalIdentifier journalId) throws DataAccessException {
        return (Long)this.mostRecentIdCache.get((Object)journalId, () -> this.delegate.getMostRecentId(journalId));
    }

    @Override
    public void setMostRecentId(@NonNull JournalIdentifier journalId, long id) throws DataAccessException {
        this.delegate.setMostRecentId(journalId, id);
        this.mostRecentIdCache.put((Object)journalId, (Object)id);
    }

    @Override
    public void resetAllJournalStates() throws DataAccessException {
        try {
            this.delegate.resetAllJournalStates();
        }
        finally {
            this.mostRecentIdCache.removeAll();
        }
    }
}

