/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.user.persistence.dao;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.cache.ReadThroughEntityCache;
import com.atlassian.confluence.impl.content.render.prefetch.PersonalInformationBulkDao;
import com.atlassian.confluence.internal.persistence.DelegatingObjectDaoInternal;
import com.atlassian.confluence.internal.user.persistence.PersonalInformationDaoInternal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ReadThroughCachingPersonalInformationDao
extends DelegatingObjectDaoInternal<PersonalInformation>
implements PersonalInformationDaoInternal,
PersonalInformationBulkDao {
    private final PersonalInformationDaoInternal delegateDao;
    private final ReadThroughCache<UserKey, PersonalInformation> cache;

    public static ReadThroughCachingPersonalInformationDao create(PersonalInformationDaoInternal delegateDao, CacheFactory cacheFactory) {
        return new ReadThroughCachingPersonalInformationDao(delegateDao, ReadThroughEntityCache.forConfluenceEntityObjects(ReadThroughAtlassianCache.create(cacheFactory, CoreCache.USER_ID_BY_USER_KEY), delegateDao::getById));
    }

    private ReadThroughCachingPersonalInformationDao(PersonalInformationDaoInternal delegateDao, ReadThroughCache<UserKey, PersonalInformation> cache) {
        super(delegateDao);
        this.delegateDao = delegateDao;
        this.cache = cache;
    }

    @Override
    public PersonalInformation getByUser(ConfluenceUser user) {
        return this.cache.get(user.getKey(), () -> this.delegateDao.getByUser(user));
    }

    private void removeFromCache(ConfluenceUser user) {
        this.cache.remove(user.getKey());
    }

    @Override
    public List<PersonalInformation> getAllByUser(ConfluenceUser user) {
        return this.delegateDao.getAllByUser(user);
    }

    @Override
    public PersonalInformation getById(long id) {
        return this.delegateDao.getById(id);
    }

    @Override
    public @NonNull List<Long> findIdsWithAssociatedUser() {
        return this.delegateDao.findIdsWithAssociatedUser();
    }

    @Override
    public Collection<PersonalInformation> bulkFetchPersonalInformation(Collection<UserKey> userKeys) {
        return this.cache.getBulk((Set<UserKey>)ImmutableSet.copyOf(userKeys), keysToFetch -> (Map)((PersonalInformationBulkDao)((Object)this.delegateDao)).bulkFetchPersonalInformation((Collection<UserKey>)keysToFetch).stream().collect(ImmutableMap.toImmutableMap(info -> info.getUser().getKey(), info -> info, ReadThroughCachingPersonalInformationDao.pickLowestId()))).values();
    }

    private static <T extends ConfluenceEntityObject> BinaryOperator<T> pickLowestId() {
        return (a, b) -> a.getId() <= b.getId() ? a : b;
    }

    @Override
    public void remove(EntityObject object) {
        if (object instanceof PersonalInformation) {
            this.removeFromCache(((PersonalInformation)object).getUser());
        }
        this.delegateDao.remove(object);
    }

    @Override
    public void removeEntity(PersonalInformation objectToRemove) {
        if (objectToRemove != null) {
            this.removeFromCache(objectToRemove.getUser());
        }
        this.delegateDao.removeEntity(objectToRemove);
    }
}

