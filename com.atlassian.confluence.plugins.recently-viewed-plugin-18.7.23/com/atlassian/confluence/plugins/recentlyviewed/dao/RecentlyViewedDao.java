/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.recentlyviewed.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public interface RecentlyViewedDao {
    public int deleteOldRecentyViewed(int var1);

    public List<Long> findRecentContentIds(String var1);

    @Deprecated
    public List<RecentlyViewed> findRecentlyViewed(String var1, int var2);

    @Deprecated
    public List<RecentlyViewed> findRecentlyViewed(String var1, int var2, int var3);

    public List<RecentlyViewed> findRecentlyViewed(UserKey var1, int var2, int var3);

    @Deprecated
    public List<RecentlyViewed> findRecentlyViewed(String var1, Date var2, Set<String> var3, int var4, int var5);

    public List<RecentlyViewed> findRecentlyViewedPages(String var1, int var2);

    public List<RecentlyViewed> findRecentlyViewedPages(String var1, Date var2, int var3);

    public Option<RecentlyViewed> findRecentlyViewedEntry(ContentId var1, UserKey var2);

    public List<RecentlyViewed> findRecentlyViewedEntries(Iterable<ContentId> var1, UserKey var2);

    public List<String> findRecentlyViewedSpaceKeys(String var1, int var2);

    public Map<Long, Collection<String>> findRecentViewers(Iterable<Long> var1);

    public void update(long var1, String var3, String var4, String var5, long var6);

    public int delete(long var1);
}

