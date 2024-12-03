/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.recentlyviewed;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public interface RecentlyViewedManager {
    @Deprecated
    public void savePageView(String var1, long var2, String var4, long var5);

    public void savePageView(String var1, long var2, String var4, String var5, long var6);

    public void removePageViews(long var1);

    public List<Long> getRecentlyViewedIds(String var1);

    @Deprecated
    public List<RecentlyViewed> getRecentlyViewed(String var1, int var2);

    public List<RecentlyViewed> getRecentlyViewed(@Nonnull UserKey var1, boolean var2, int var3);

    public List<RecentlyViewed> getRecentlyViewedPages(String var1, boolean var2, int var3);

    public List<RecentlyViewed> getRecentlyViewedPages(String var1, Date var2, int var3);

    public List<RecentlyViewed> getRecentlyViewed(String var1, Date var2, Set<String> var3, int var4, int var5);

    @Deprecated
    public List<Space> getRecentlyViewedSpaces(String var1, int var2);

    default public List<com.atlassian.confluence.api.model.content.Space> findRecentlyViewedSpaces(String userKey, int limit) {
        return Collections.emptyList();
    }

    public Map<Long, Collection<UserKey>> getRecentViewers(Iterable<Long> var1);

    public void deleteOldEntries();
}

