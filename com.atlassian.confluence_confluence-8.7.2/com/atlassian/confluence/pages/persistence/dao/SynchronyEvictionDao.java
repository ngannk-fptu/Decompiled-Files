/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages.persistence.dao;

import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface SynchronyEvictionDao {
    @Transactional(readOnly=true)
    public List<Long> findSafeContentWithHistoryOlderThan(int var1, int var2, int var3);

    @Transactional(readOnly=true)
    public List<Long> findContentWithAnyEventOlderThan(int var1, int var2);

    @Transactional(readOnly=true)
    public List<Long> findContentWithAnySnapshotOlderThan(int var1, int var2);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int removeAllSynchronyDataFor(Collection<Long> var1);

    @Transactional(readOnly=true)
    public long getEventsCount(Long var1);

    @Transactional(readOnly=true)
    public long getSnapshotsCount(Long var1);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public int removeApplicationIds(Collection<String> var1);

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void removeContentProperties();
}

