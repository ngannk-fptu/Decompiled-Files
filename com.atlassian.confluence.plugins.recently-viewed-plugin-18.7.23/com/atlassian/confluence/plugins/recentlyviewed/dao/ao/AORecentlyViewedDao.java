/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Joiner
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  net.java.ao.Query
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.recentlyviewed.dao.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.confluence.plugins.recentlyviewed.dao.RecentlyViewedDao;
import com.atlassian.confluence.plugins.recentlyviewed.dao.ao.AORecentlyViewed;
import com.atlassian.confluence.plugins.recentlyviewed.dao.ao.AbstractAODao;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AORecentlyViewedDao
extends AbstractAODao<AORecentlyViewed, Long>
implements RecentlyViewedDao {
    public static final int MAX_RESULTS = 400;

    @Autowired
    public AORecentlyViewedDao(@ComponentImport ActiveObjects ao) {
        super(AORecentlyViewed.class, ao);
    }

    @Override
    public int deleteOldRecentyViewed(int days) {
        return this.delete((Supplier<Query>)((Supplier)() -> Query.select().where("LAST_VIEW_DATE < ?", new Object[]{AORecentlyViewedDao.getDaysAgo(days)})));
    }

    @Override
    public List<Long> findRecentContentIds(String userKey) {
        ArrayList contentIds = Lists.newArrayList();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID").where("USER_KEY = ?", new Object[]{userKey}).limit(400), row -> contentIds.add(row.getContentId()));
        return contentIds;
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewed(String userKey, int limit) {
        return this.findRecentlyViewed(userKey, limit, 0);
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewed(String userKey, int limit, int offset) {
        return this.findRecentlyViewed(new UserKey(userKey), limit, offset);
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewed(@Nonnull UserKey userKey, int limit, int offset) {
        ArrayList contentIds = Lists.newArrayList();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID,LAST_VIEW_DATE").where("USER_KEY = ?", new Object[]{userKey.getStringValue()}).order("LAST_VIEW_DATE DESC").limit(limit).offset(offset), aoRecentlyViewed -> {
            RecentlyViewed recentlyViewed = new RecentlyViewed(aoRecentlyViewed.getContentId(), aoRecentlyViewed.getLastViewDate().getTime());
            contentIds.add(recentlyViewed);
        });
        return contentIds;
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewedPages(String userKey, int limit) {
        ArrayList contentIds = Lists.newArrayList();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID,LAST_VIEW_DATE").where("USER_KEY = ? and CONTENT_TYPE = 'PAGE'", new Object[]{userKey}).order("LAST_VIEW_DATE DESC").limit(limit), aoRecentlyViewed -> {
            RecentlyViewed recentlyViewed = new RecentlyViewed(aoRecentlyViewed.getContentId(), aoRecentlyViewed.getLastViewDate().getTime());
            contentIds.add(recentlyViewed);
        });
        return contentIds;
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewedPages(String userKey, Date after, int limit) {
        ArrayList recentlyViewedItems = Lists.newArrayList();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID,LAST_VIEW_DATE").where("USER_KEY = ? and CONTENT_TYPE = 'PAGE' and LAST_VIEW_DATE > ?", new Object[]{userKey, after}).order("LAST_VIEW_DATE DESC").limit(limit), aoRecentlyViewed -> {
            RecentlyViewed recentlyViewed = new RecentlyViewed(aoRecentlyViewed.getContentId(), aoRecentlyViewed.getLastViewDate().getTime());
            recentlyViewedItems.add(recentlyViewed);
        });
        return recentlyViewedItems;
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewed(String userKey, Date after, Set<String> spaceKeys, int limit, int offset) {
        ArrayList recentlyViewedItems = Lists.newArrayList();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID,SPACE_KEY,LAST_VIEW_DATE").where(this.setupWhereClause(spaceKeys), new Object[]{userKey, after}).order("LAST_VIEW_DATE DESC").limit(limit).offset(offset), aoRecentlyViewed -> {
            RecentlyViewed recentlyViewed = new RecentlyViewed(aoRecentlyViewed.getContentId(), aoRecentlyViewed.getLastViewDate().getTime());
            recentlyViewedItems.add(recentlyViewed);
        });
        return recentlyViewedItems;
    }

    private String setupWhereClause(Set<String> spaceKeys) {
        Object where = "USER_KEY = ?  and LAST_VIEW_DATE > ? and CONTENT_TYPE in ('PAGE','BLOGPOST')";
        if (spaceKeys != null && !spaceKeys.isEmpty()) {
            where = (String)where + " and SPACE_KEY in (" + Joiner.on((String)",").join((Iterable)spaceKeys.stream().map(this::wrapSpaceKey).collect(Collectors.toList())) + ")";
        }
        return where;
    }

    private String wrapSpaceKey(String spaceKey) {
        return "'" + spaceKey + "'";
    }

    @Override
    public Option<RecentlyViewed> findRecentlyViewedEntry(ContentId contentId, UserKey userKey) {
        return Option.option((Object)((RecentlyViewed)Iterables.getFirst(this.findRecentlyViewedEntries((Iterable<ContentId>)Option.some((Object)contentId), userKey), null)));
    }

    @Override
    public List<RecentlyViewed> findRecentlyViewedEntries(Iterable<ContentId> contentId, UserKey userKey) {
        ArrayList result = Lists.newArrayList();
        ArrayList longContentIds = new ArrayList();
        longContentIds.addAll(ImmutableList.copyOf((Iterable)Iterables.transform(contentId, ContentId::asLong)));
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID,LAST_VIEW_DATE").where("CONTENT_ID in (" + Joiner.on((String)",").join(longContentIds) + ") and USER_KEY = ?", new Object[]{userKey.toString()}).order("LAST_VIEW_DATE DESC"), aoRecentlyViewed -> {
            RecentlyViewed recentlyViewed = new RecentlyViewed(aoRecentlyViewed.getContentId(), aoRecentlyViewed.getLastViewDate().getTime());
            result.add(recentlyViewed);
        });
        return result;
    }

    @Override
    public List<String> findRecentlyViewedSpaceKeys(String userKey, int limit) {
        LinkedList spaceKeys = Lists.newLinkedList();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID, SPACE_KEY").where("USER_KEY = ?", new Object[]{userKey}).order("LAST_VIEW_DATE DESC").limit(limit), aoRecentlyViewed -> {
            String spaceKey = aoRecentlyViewed.getSpaceKey();
            if (!spaceKeys.contains(spaceKey)) {
                spaceKeys.add(spaceKey);
            }
        });
        return spaceKeys;
    }

    @Override
    public Map<Long, Collection<String>> findRecentViewers(Iterable<Long> contentIds) {
        if (Iterables.isEmpty(contentIds)) {
            return Collections.emptyMap();
        }
        ArrayListMultimap result = ArrayListMultimap.create();
        this.ao.stream(AORecentlyViewed.class, Query.select((String)"ID,CONTENT_ID,USER_KEY,LAST_VIEW_DATE").where("CONTENT_ID in (" + Joiner.on((String)",").join(contentIds) + ")", new Object[0]).order("LAST_VIEW_DATE DESC"), arg_0 -> AORecentlyViewedDao.lambda$findRecentViewers$8((Multimap)result, arg_0));
        return result.asMap();
    }

    @Override
    public void update(long contentId, String contentType, String userKey, String spaceKey, long timestamp) {
        Date lastViewDate = new Date(timestamp);
        AORecentlyViewed recentlyViewed = this.findOnly(AORecentlyViewed.class, Query.select().where("USER_KEY = ? AND CONTENT_ID = ?", new Object[]{userKey, contentId}));
        if (recentlyViewed == null) {
            this.ao.create(AORecentlyViewed.class, (Map)ImmutableMap.of((Object)"USER_KEY", (Object)userKey, (Object)"CONTENT_ID", (Object)contentId, (Object)"CONTENT_TYPE", (Object)contentType, (Object)"SPACE_KEY", (Object)spaceKey, (Object)"LAST_VIEW_DATE", (Object)lastViewDate));
        } else {
            if (recentlyViewed.getSpaceKey() == null) {
                recentlyViewed.setSpaceKey(spaceKey);
            }
            recentlyViewed.setLastViewDate(lastViewDate);
            recentlyViewed.save();
        }
    }

    @Override
    public int delete(long contentId) {
        return this.delete((Supplier<Query>)((Supplier)() -> Query.select().where("CONTENT_ID = ?", new Object[]{contentId})));
    }

    private static Date getDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(5, -days);
        return calendar.getTime();
    }

    private static /* synthetic */ void lambda$findRecentViewers$8(Multimap result, AORecentlyViewed x) {
        result.put((Object)x.getContentId(), (Object)x.getUserKey());
    }
}

