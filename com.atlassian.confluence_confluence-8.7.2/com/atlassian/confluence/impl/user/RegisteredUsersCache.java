/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 */
package com.atlassian.confluence.impl.user;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import java.util.Objects;
import java.util.function.Supplier;

public class RegisteredUsersCache {
    private static final String NUMBER_OF_REGISTERED_USERS = "too.many.users.result";
    private final Cache<String, Integer> cache;

    @Deprecated
    public static String getCacheName() {
        return CoreCache.REGISTERED_USERS.resolve(name -> name);
    }

    public RegisteredUsersCache(CacheFactory cacheFactory) {
        Objects.requireNonNull(cacheFactory);
        this.cache = CoreCache.REGISTERED_USERS.getCache(cacheFactory);
    }

    public void clear() {
        this.cache.removeAll();
    }

    public int getNumberOfRegisteredUsers(Supplier<Integer> valueSupplier) {
        return (Integer)this.cache.get((Object)NUMBER_OF_REGISTERED_USERS, valueSupplier::get);
    }

    public void setNumberOfRegisteredUsers(int val) {
        this.cache.put((Object)NUMBER_OF_REGISTERED_USERS, (Object)val);
    }
}

