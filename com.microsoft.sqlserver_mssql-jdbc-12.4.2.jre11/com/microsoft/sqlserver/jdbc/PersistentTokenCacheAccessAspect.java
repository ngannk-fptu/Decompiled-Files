/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.aad.msal4j.ITokenCacheAccessAspect
 *  com.microsoft.aad.msal4j.ITokenCacheAccessContext
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PersistentTokenCacheAccessAspect
implements ITokenCacheAccessAspect {
    private static PersistentTokenCacheAccessAspect instance = new PersistentTokenCacheAccessAspect();
    private final Lock lock = new ReentrantLock();
    private String cache = null;

    private PersistentTokenCacheAccessAspect() {
    }

    static PersistentTokenCacheAccessAspect getInstance() {
        return instance;
    }

    public void beforeCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
        this.lock.lock();
        try {
            if (null != this.cache && null != iTokenCacheAccessContext && null != iTokenCacheAccessContext.tokenCache()) {
                iTokenCacheAccessContext.tokenCache().deserialize(this.cache);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void afterCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
        this.lock.lock();
        try {
            if (null != iTokenCacheAccessContext && iTokenCacheAccessContext.hasCacheChanged() && null != iTokenCacheAccessContext.tokenCache()) {
                this.cache = iTokenCacheAccessContext.tokenCache().serialize();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    static void clearUserTokenCache() {
        if (null != PersistentTokenCacheAccessAspect.instance.cache && !PersistentTokenCacheAccessAspect.instance.cache.isEmpty()) {
            PersistentTokenCacheAccessAspect.instance.cache = null;
        }
    }
}

