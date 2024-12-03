/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.user.User;
import java.util.Map;
import java.util.concurrent.Callable;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum AuthenticatedUserImpersonator {
    REQUEST_AWARE,
    REQUEST_AGNOSTIC;


    public <T> T asAnonymousUser(Callable<T> callback) {
        return this.asUser(callback, null);
    }

    public <T> T asUser(Callable<T> callback, @Nullable User user) {
        Callable<T> wrappedCallback = AuthenticatedUserImpersonator.impersonateUser(callback, user);
        if (this == REQUEST_AGNOSTIC) {
            wrappedCallback = AuthenticatedUserImpersonator.hideRequestCache(wrappedCallback);
        }
        try {
            return wrappedCallback.call();
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }

    private static <T> Callable<T> hideRequestCache(final Callable<T> callback) {
        return new Callable<T>(){

            @Override
            public T call() throws Exception {
                Map requestCache = RequestCacheThreadLocal.getRequestCache();
                try {
                    RequestCacheThreadLocal.clearRequestCache();
                    Object v = callback.call();
                    return v;
                }
                finally {
                    RequestCacheThreadLocal.setRequestCache(requestCache);
                }
            }
        };
    }

    private static <T> Callable<T> impersonateUser(final Callable<T> callback, final @Nullable User user) {
        return new Callable<T>(){

            @Override
            public T call() throws Exception {
                ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
                try {
                    AuthenticatedUserThreadLocal.setUser(user);
                    Object v = callback.call();
                    return v;
                }
                finally {
                    AuthenticatedUserThreadLocal.setUser(currentUser);
                }
            }
        };
    }
}

