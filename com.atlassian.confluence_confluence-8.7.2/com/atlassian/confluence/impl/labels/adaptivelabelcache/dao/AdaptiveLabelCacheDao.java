/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.impl.labels.adaptivelabelcache.dao;

import com.atlassian.confluence.impl.labels.adaptivelabelcache.LiteSearchResultCacheEntry;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.REQUIRES_NEW)
public interface AdaptiveLabelCacheDao {
    public LiteSearchResultCacheEntry read(long var1);

    public void write(long var1, LiteSearchResultCacheEntry var3);

    public void clear();

    public void removeRecordsExpiredAfter(long var1);

    public void removeRecord(long var1);

    @Transactional(readOnly=true)
    public String getSpaceKeyFromSpaceId(long var1);

    @Transactional(readOnly=true)
    public long getSpaceIdByKey(String var1);
}

