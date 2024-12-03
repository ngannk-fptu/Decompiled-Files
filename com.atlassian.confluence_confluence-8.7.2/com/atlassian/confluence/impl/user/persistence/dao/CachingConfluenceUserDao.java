/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.user.persistence.dao;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.cache.ReadThroughEntityCache;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CachingConfluenceUserDao
implements ConfluenceUserDao {
    private final ConfluenceUserDao delegate;
    private final UsernameCache usernameCache;

    public static CachingConfluenceUserDao create(ConfluenceUserDao delegate, CacheFactory cacheFactory) {
        ReadThroughEntityCache cache = new ReadThroughEntityCache(ReadThroughAtlassianCache.create(cacheFactory, CoreCache.USER_KEY_BY_USER_NAME), delegate::findByKey, ConfluenceUser::getKey);
        return new CachingConfluenceUserDao(delegate, cache);
    }

    CachingConfluenceUserDao(ConfluenceUserDao delegate, ReadThroughCache<String, ConfluenceUser> cache) {
        this.delegate = delegate;
        this.usernameCache = new UsernameCache(cache);
    }

    @Override
    public void create(ConfluenceUser user) {
        this.delegate.create(user);
        this.usernameCache.remove(user);
    }

    @Override
    public void update(ConfluenceUser user) {
        this.delegate.update(user);
        this.usernameCache.remove(user);
    }

    @Override
    public void remove(ConfluenceUser user) {
        this.delegate.remove(user);
        this.usernameCache.remove(user);
    }

    @Override
    public ConfluenceUser rename(String oldUsername, String newUsername, boolean overrideExisting) {
        ConfluenceUser user = this.delegate.rename(oldUsername, newUsername, overrideExisting);
        this.usernameCache.remove(oldUsername);
        this.usernameCache.remove(newUsername);
        return user;
    }

    @Override
    public ConfluenceUser rename(ConfluenceUser userToRename, String newUsername, boolean overrideExisting) {
        String oldUsername = userToRename.getName();
        ConfluenceUser user = this.delegate.rename(userToRename, newUsername, overrideExisting);
        this.usernameCache.remove(oldUsername);
        this.usernameCache.remove(newUsername);
        return user;
    }

    @Override
    public void deactivateUser(String username) {
        this.delegate.deactivateUser(username);
        this.usernameCache.remove(username);
    }

    @Override
    public ConfluenceUser findByKey(@Nullable UserKey key) {
        return this.delegate.findByKey(key);
    }

    @Override
    public ConfluenceUser findByUsername(@Nullable String username) {
        if (username == null) {
            return null;
        }
        return this.usernameCache.get(username, () -> this.delegate.findByUsername(username)).orElse(null);
    }

    @Override
    public Set<ConfluenceUser> getAll() {
        return this.delegate.getAll();
    }

    @Override
    public Map<String, UserKey> findUserKeysByLowerNames(Iterable<String> names) {
        return this.delegate.findUserKeysByLowerNames(names);
    }

    @Override
    public Map<UserKey, String> findLowerNamesByKeys(Iterable<UserKey> keys) {
        return this.delegate.findLowerNamesByKeys(keys);
    }

    @Override
    public boolean isDeletedUser(ConfluenceUser user) {
        return this.delegate.isDeletedUser(user);
    }

    @Override
    public boolean isUnsyncedUser(ConfluenceUser user) {
        return this.delegate.isUnsyncedUser(user);
    }

    @Override
    public List<ConfluenceUser> searchUnsyncedUsers(String searchParam) {
        return this.delegate.searchUnsyncedUsers(searchParam);
    }

    @Override
    public int countUnsyncedUsers() {
        return this.delegate.countUnsyncedUsers();
    }

    @Override
    public Map<UserKey, Optional<ConfluenceUser>> findByKeys(Set<UserKey> userkeys) {
        return this.delegate.findByKeys(userkeys);
    }

    @Override
    public List<ConfluenceUser> findConfluenceUsersByLowerNames(Iterable<String> lowerNames) {
        return this.delegate.findConfluenceUsersByLowerNames(lowerNames);
    }

    @Override
    public List<UserKey> getAllUserKeys() {
        return this.delegate.getAllUserKeys();
    }

    private static class UsernameCache {
        private final ReadThroughCache<String, ConfluenceUser> cache;

        public UsernameCache(ReadThroughCache<String, ConfluenceUser> cache) {
            this.cache = cache;
        }

        public Optional<ConfluenceUser> get(String username, Supplier<ConfluenceUser> delegateLoader) {
            return Optional.ofNullable(this.cache.get(IdentifierUtils.toLowerCase((String)username), delegateLoader, user -> UsernameCache.getLowerCaseName(user).isPresent()));
        }

        public void remove(ConfluenceUser user) {
            UsernameCache.getLowerCaseName(user).ifPresent(this.cache::remove);
        }

        public void remove(String username) {
            this.cache.remove(IdentifierUtils.toLowerCase((String)username));
        }

        private static Optional<String> getLowerCaseName(ConfluenceUser user) {
            if (user instanceof ConfluenceUserImpl) {
                return Optional.ofNullable(user.getLowerName());
            }
            return Optional.ofNullable(IdentifierUtils.toLowerCase((String)user.getName()));
        }
    }
}

