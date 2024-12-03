/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.tombstone.TombstoneDao
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.tombstone;

import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.manager.tombstone.TombstoneManager;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TombstoneManagerImpl
implements TombstoneManager {
    public static final Duration TOMBSTONE_LIFETIME = Duration.ofDays(7L);
    private static final Logger log = LoggerFactory.getLogger(TombstoneManagerImpl.class);
    private final TombstoneDao tombstoneDao;

    public TombstoneManagerImpl(TombstoneDao tombstoneDao) {
        this.tombstoneDao = tombstoneDao;
    }

    @Override
    public void removeOldTombstones() {
        Instant cutoff = Instant.now().minus(TOMBSTONE_LIFETIME);
        this.removeTombstonesOlderThan(cutoff);
    }

    @Override
    public void removeTombstonesOlderThan(Instant cutoff) {
        log.debug("Removing tombstones created before {}", (Object)cutoff);
        int removed = this.tombstoneDao.removeAllUpTo(cutoff.toEpochMilli());
        log.debug("Done tombstone pruning job, removed {} tombstones", (Object)removed);
    }
}

