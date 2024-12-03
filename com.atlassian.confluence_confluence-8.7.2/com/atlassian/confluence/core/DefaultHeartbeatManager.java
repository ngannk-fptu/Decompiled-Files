/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.jdk.JavaSerializationMarshalling
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.ExternalCacheSettingsBuilder
 *  com.atlassian.vcache.VCacheFactory
 *  io.atlassian.fugue.Either
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.core.VCacheCasUtils;
import com.atlassian.confluence.internal.user.DeferredLookupUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.marshalling.jdk.JavaSerializationMarshalling;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.ExternalCacheSettingsBuilder;
import com.atlassian.vcache.VCacheFactory;
import io.atlassian.fugue.Either;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DefaultHeartbeatManager
implements HeartbeatManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultHeartbeatManager.class);
    private static final String ACTIVITIES_CACHE_NAME = "com.atlassian.confluence.core.DefaultHeartbeatManager.activities";
    private static final Duration TIMEOUT = Duration.ofSeconds(30L);
    private final DirectExternalCache<CacheData> directExternalCache;
    private final ConfluenceUserResolver userAccessor;

    public DefaultHeartbeatManager(VCacheFactory cacheFactory, ConfluenceUserResolver userResolver) {
        Objects.requireNonNull(cacheFactory);
        Objects.requireNonNull(userResolver);
        this.directExternalCache = cacheFactory.getDirectExternalCache(ACTIVITIES_CACHE_NAME, JavaSerializationMarshalling.pair(CacheData.class), new ExternalCacheSettingsBuilder().defaultTtl(Duration.ofMinutes(5L)).build());
        this.userAccessor = userResolver;
    }

    @Override
    public long getHeartbeatInterval() {
        return TIMEOUT.toMillis();
    }

    @Override
    public List<User> getUsersForActivity(String activityKey) {
        Either<Throwable, CacheData> either = VCacheCasUtils.atomicReplace(this.directExternalCache, activityKey, this::cleanTimedOutActivities, () -> new CacheData(), TIMEOUT);
        CacheData cacheData = (CacheData)either.getOrThrow(() -> new RuntimeException("Unable to fetch users for activity: " + activityKey, (Throwable)either.left().get()));
        return cacheData.getActivities().stream().map(activity -> new DeferredLookupUser(activity.userKey)).collect(Collectors.toList());
    }

    @Override
    public void startActivity(String activityKey, String username) {
        this.startActivity(activityKey, this.userAccessor.getUserByName(username));
    }

    @Override
    public void startActivity(String activityKey, User user) {
        if (user == null) {
            return;
        }
        Either<Throwable, CacheData> either = VCacheCasUtils.atomicReplace(this.directExternalCache, activityKey, cacheData -> {
            CacheData result = new CacheData(cacheData.getActivities());
            Activity activity = new Activity(activityKey, FindUserHelper.getUser(user).getKey());
            result.getActivities().remove(activity);
            result.getActivities().add(activity);
            return result;
        }, () -> new CacheData(Collections.singleton(new Activity(activityKey, FindUserHelper.getUser(user).getKey()))), TIMEOUT);
        either.bimap(exception -> {
            log.error("Unable to start heartbeat activity", exception);
            return exception;
        }, v -> v);
    }

    @Override
    public void stopActivity(String activityKey, String username) {
        this.stopActivity(activityKey, this.userAccessor.getUserByName(username));
    }

    @Override
    public void stopActivity(String activityKey, User user) {
        if (user == null) {
            return;
        }
        Either<Throwable, CacheData> either = VCacheCasUtils.atomicReplace(this.directExternalCache, activityKey, cacheData -> {
            CacheData result = new CacheData(cacheData.getActivities());
            Activity activity = new Activity(activityKey, FindUserHelper.getUser(user).getKey());
            result.getActivities().remove(activity);
            return result;
        }, () -> new CacheData(), TIMEOUT);
        either.bimap(exception -> {
            log.error("Unable to stop heartbeat activity", exception);
            return exception;
        }, v -> v);
    }

    private CacheData cleanTimedOutActivities(CacheData cacheData) {
        return new CacheData(cacheData.getActivities().stream().filter(activity -> Instant.now().toEpochMilli() - activity.timestamp.toEpochMilli() <= 2L * TIMEOUT.toMillis()).collect(Collectors.toSet()));
    }

    private static class Activity
    implements Serializable,
    Comparable<Activity> {
        private final Instant timestamp;
        private final UserKey userKey;
        private final String activityKey;

        public Activity(String activityKey, UserKey user) {
            this(Objects.requireNonNull(activityKey), Objects.requireNonNull(user), Instant.now());
        }

        public Activity(String activityKey, UserKey userKey, Instant timestamp) {
            this.timestamp = Objects.requireNonNull(timestamp);
            this.userKey = Objects.requireNonNull(userKey);
            this.activityKey = Objects.requireNonNull(activityKey);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Activity activity = (Activity)o;
            return Objects.equals(this.userKey, activity.userKey) && Objects.equals(this.activityKey, activity.activityKey);
        }

        public int hashCode() {
            return Objects.hash(this.userKey, this.activityKey);
        }

        @Override
        public int compareTo(Activity o) {
            int byActivity = this.activityKey.compareTo(o.activityKey);
            return byActivity != 0 ? byActivity : this.userKey.toString().compareTo(o.userKey.toString());
        }
    }

    private static class CacheData
    implements Serializable {
        private static final long serialVersionUID = 127177732888L;
        private final TreeSet<Activity> activities;

        private CacheData() {
            this.activities = new TreeSet();
        }

        private CacheData(Set<Activity> activities) {
            this.activities = new TreeSet(Objects.requireNonNull(activities));
        }

        public Set<Activity> getActivities() {
            return this.activities;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            return this.activities.equals(((CacheData)o).getActivities());
        }

        public int hashCode() {
            return this.activities.hashCode();
        }
    }
}

