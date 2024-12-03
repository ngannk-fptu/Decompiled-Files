/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.google.common.collect.Iterables
 */
package com.atlassian.migration.agent.service.recentlyviewed;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.migration.agent.service.RecentlyViewedService;
import com.google.common.collect.Iterables;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecentlyViewedManagerWrapper
implements RecentlyViewedService {
    private RecentlyViewedManager recentlyViewedManager;

    public RecentlyViewedManagerWrapper(RecentlyViewedManager recentlyViewedManager) {
        this.recentlyViewedManager = recentlyViewedManager;
    }

    @Override
    public int getUniqueUserViews(Set<Long> pages) {
        HashSet userKeys = new HashSet();
        Iterable partition = Iterables.partition(pages, (int)500);
        for (List subset : partition) {
            Map partialResult = this.recentlyViewedManager.getRecentViewers((Iterable)subset);
            partialResult.forEach((page, viewers) -> userKeys.addAll(viewers));
        }
        return userKeys.size();
    }
}

