/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.recentlyviewed;

import com.atlassian.migration.agent.service.RecentlyViewedService;
import com.atlassian.migration.agent.store.impl.RecentlyViewedStore;
import java.util.Set;

public class LegacyRecentlyViewedService
implements RecentlyViewedService {
    private final RecentlyViewedStore recentlyViewedStore;

    public LegacyRecentlyViewedService(RecentlyViewedStore recentlyViewedStore) {
        this.recentlyViewedStore = recentlyViewedStore;
    }

    @Override
    public int getUniqueUserViews(Set<Long> pages) {
        return this.recentlyViewedStore.getUniqueUserViewsByPages(pages);
    }
}

