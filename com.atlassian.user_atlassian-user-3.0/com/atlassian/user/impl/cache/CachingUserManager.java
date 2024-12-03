/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CachingUserManager
implements UserManager {
    private static final Logger log = Logger.getLogger(CachingUserManager.class);
    private final UserManager underlyingUserManager;
    private final CacheFactory cacheFactory;
    private String userCacheName = null;
    private String userROCacheName = null;
    private String repositoryCacheName = null;
    private static final String CACHE_SUFFIX_USERS = "users";
    private static final String CACHE_SUFFIX_USERS_RO = "users_ro";
    private static final String CACHE_SUFFIX_REPOSITORIES = "repository";
    protected static User NULL_USER = new DefaultUser(){

        public String toString() {
            return "NULL USER";
        }
    };

    public CachingUserManager(UserManager underlyingUserManager, CacheFactory cacheFactory) {
        this.underlyingUserManager = underlyingUserManager;
        this.cacheFactory = cacheFactory;
    }

    @Override
    public Pager<User> getUsers() throws EntityException {
        return this.underlyingUserManager.getUsers();
    }

    @Override
    public Pager<String> getUserNames() throws EntityException {
        return this.underlyingUserManager.getUserNames();
    }

    @Override
    public User getUser(String username) throws EntityException {
        User cachedUser = (User)this.getUserCache().get((Object)username);
        if (cachedUser != null) {
            return NULL_USER.equals(cachedUser) ? null : cachedUser;
        }
        User user = this.underlyingUserManager.getUser(username);
        this.cacheUser(username, user);
        return user;
    }

    private void cacheUser(String username, User user) {
        this.getUserCache().put((Object)username, (Object)(user == null ? NULL_USER : user));
    }

    private void cacheRepository(String username, RepositoryIdentifier repository) {
        this.getRepositoryCache().put((Object)username, (Object)repository);
    }

    private void cacheUserROFlag(User user, boolean ro) {
        this.getUserROFlagCache().put((Object)user.getName(), (Object)ro);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Cache getUserCache() {
        CachingUserManager cachingUserManager = this;
        synchronized (cachingUserManager) {
            if (this.userCacheName == null) {
                this.userCacheName = this.getCacheKey(CACHE_SUFFIX_USERS);
            }
        }
        return this.cacheFactory.getCache(this.userCacheName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Cache getUserROFlagCache() {
        CachingUserManager cachingUserManager = this;
        synchronized (cachingUserManager) {
            if (this.userROCacheName == null) {
                this.userROCacheName = this.getCacheKey(CACHE_SUFFIX_USERS_RO);
            }
        }
        return this.cacheFactory.getCache(this.userROCacheName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Cache getRepositoryCache() {
        CachingUserManager cachingUserManager = this;
        synchronized (cachingUserManager) {
            if (this.repositoryCacheName == null) {
                this.repositoryCacheName = this.getCacheKey(CACHE_SUFFIX_REPOSITORIES);
            }
        }
        return this.cacheFactory.getCache(this.repositoryCacheName);
    }

    @Override
    public User createUser(String username) throws EntityException {
        User user = this.underlyingUserManager.createUser(username);
        if (user != null) {
            this.cacheUser(user.getName(), user);
        }
        return user;
    }

    @Override
    public User createUser(User userTemplate, Credential credential) throws EntityException {
        User user = this.underlyingUserManager.createUser(userTemplate, credential);
        if (user != null) {
            this.cacheUser(user.getName(), user);
        }
        return user;
    }

    @Override
    public void alterPassword(User user, String plainTextPass) throws EntityException {
        this.underlyingUserManager.alterPassword(user, plainTextPass);
        if (user != null) {
            this.cacheUser(user.getName(), this.underlyingUserManager.getUser(user.getName()));
        }
    }

    @Override
    public void saveUser(User user) throws EntityException {
        this.underlyingUserManager.saveUser(user);
        if (user != null) {
            this.cacheUser(user.getName(), user);
        }
    }

    @Override
    public void removeUser(User user) throws EntityException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("removing user: " + user.getName()));
        }
        this.underlyingUserManager.removeUser(user);
        if (log.isDebugEnabled()) {
            log.debug((Object)("user " + user.getName() + " removed from underlying user manager " + this.underlyingUserManager.getIdentifier().getName()));
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("removing user from cache: " + user.getName()));
            }
            this.removeUserFromCache(user);
            if (log.isDebugEnabled()) {
                log.debug((Object)("removed user from cache: " + user.getName()));
                if (this.getUserCache().get((Object)user.getName()) != null) {
                    log.error((Object)"WTF???");
                }
            }
        }
        catch (Exception e) {
            throw new EntityException("User removed in underlying repository but could not remove from cache");
        }
    }

    private void removeUserFromCache(User user) {
        if (user != null) {
            this.getUserCache().remove((Object)user.getName());
        }
    }

    @Override
    public boolean isReadOnly(User user) throws EntityException {
        Boolean cachedROFlag = (Boolean)this.getUserROFlagCache().get((Object)user.getName());
        if (cachedROFlag == null) {
            boolean ro = this.underlyingUserManager.isReadOnly(user);
            this.cacheUserROFlag(user, ro);
            return ro;
        }
        return cachedROFlag;
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.underlyingUserManager.getIdentifier();
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        RepositoryIdentifier cachedRepository = (RepositoryIdentifier)this.getRepositoryCache().get((Object)entity.getName());
        if (cachedRepository != null) {
            return cachedRepository;
        }
        RepositoryIdentifier repository = this.underlyingUserManager.getRepository(entity);
        this.cacheRepository(entity.getName(), repository);
        return repository;
    }

    @Override
    public boolean isCreative() {
        return this.underlyingUserManager.isCreative();
    }

    private String getCacheKey(String cacheName) {
        String className = this.underlyingUserManager.getClass().getName();
        String repositoryKey = this.underlyingUserManager.getIdentifier().getKey();
        return className + "." + repositoryKey + "." + cacheName;
    }
}

