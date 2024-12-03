/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.tuple.Pair
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.mail.notification.persistence;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.internal.notification.persistence.DelegatingNotificationDaoInternal;
import com.atlassian.confluence.internal.notification.persistence.NotificationDaoInternal;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;

public class CachingNotificationDao
extends DelegatingNotificationDaoInternal {
    private final ReadThroughCache<Pair<UserKey, Long>, Boolean> isWatchingContentCache;

    public CachingNotificationDao(NotificationDaoInternal delegate, CacheFactory cacheFactory) {
        super(delegate);
        this.isWatchingContentCache = ReadThroughAtlassianCache.create(cacheFactory, CoreCache.IS_USER_WATCHING_CONTENT);
    }

    @Override
    public boolean isWatchingContent(@NonNull ConfluenceUser user, @NonNull ContentEntityObject content) {
        return this.isWatchingContentCache.get((Pair<UserKey, Long>)Pair.of((Object)user.getKey(), (Object)content.getId()), () -> this.delegate.isWatchingContent(user, content));
    }

    @Override
    @Deprecated
    public void remove(EntityObject object) {
        Optional<Pair<UserKey, Long>> maybeKey = CachingNotificationDao.tryExtractKey(object);
        this.delegate.remove(object);
        maybeKey.ifPresent(this.isWatchingContentCache::remove);
    }

    @Override
    public void removeEntity(Notification objectToRemove) {
        Optional<Pair<UserKey, Long>> maybeKey = CachingNotificationDao.tryExtractKey(objectToRemove);
        this.delegate.removeEntity(objectToRemove);
        maybeKey.ifPresent(this.isWatchingContentCache::remove);
    }

    @Override
    @Deprecated
    public void save(EntityObject objectToSave) {
        Optional<Pair<UserKey, Long>> maybeKey = CachingNotificationDao.tryExtractKey(objectToSave);
        this.delegate.save(objectToSave);
        maybeKey.ifPresent(this.isWatchingContentCache::remove);
    }

    @Override
    public void saveEntity(Notification objectToSave) {
        Optional<Pair<UserKey, Long>> maybeKey = CachingNotificationDao.tryExtractKey(objectToSave);
        this.delegate.saveEntity(objectToSave);
        maybeKey.ifPresent(this.isWatchingContentCache::remove);
    }

    @Override
    @Deprecated
    public void saveRaw(EntityObject objectToSave) {
        Optional<Pair<UserKey, Long>> maybeKey = CachingNotificationDao.tryExtractKey(objectToSave);
        this.delegate.saveRaw(objectToSave);
        maybeKey.ifPresent(this.isWatchingContentCache::remove);
    }

    @Override
    public void saveRawEntity(Notification objectToSave) {
        Optional<Pair<UserKey, Long>> maybeKey = CachingNotificationDao.tryExtractKey(objectToSave);
        this.delegate.saveRawEntity(objectToSave);
        maybeKey.ifPresent(this.isWatchingContentCache::remove);
    }

    private static Optional<Pair<UserKey, Long>> tryExtractKey(EntityObject object) {
        if (object instanceof Notification) {
            Notification notification = (Notification)object;
            ConfluenceUser user = notification.getReceiver();
            ContentEntityObject content = notification.getContent();
            if (user != null && content != null) {
                return Optional.of(Pair.of((Object)user.getKey(), (Object)content.getId()));
            }
        }
        return Optional.empty();
    }
}

