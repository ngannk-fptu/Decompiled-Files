/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.AuthenticationContext
 *  com.atlassian.seraph.auth.AuthenticationContextImpl
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.auth.AuthenticationContextImpl;
import com.atlassian.user.User;
import java.security.Principal;
import java.util.concurrent.Callable;

public class AuthenticatedUserThreadLocal {
    private static final AuthenticationContext authenticationContext = new AuthenticationContextImpl();

    public static void set(ConfluenceUser user) {
        authenticationContext.setUser((Principal)((Object)user));
    }

    @Deprecated
    public static void setUser(User user) {
        authenticationContext.setUser((Principal)((Object)FindUserHelper.getUser(user)));
    }

    public static ConfluenceUser get() {
        Object principal = authenticationContext.getUser();
        if (principal != null && !(principal instanceof ConfluenceUser)) {
            principal = FindUserHelper.getUserByUsername(principal.getName());
            authenticationContext.setUser((Principal)principal);
        }
        return (ConfluenceUser)principal;
    }

    @Deprecated
    public static User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    public static void reset() {
        AuthenticatedUserThreadLocal.set(null);
    }

    public static String getUsername() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return user == null ? null : user.getName();
    }

    public static boolean isAnonymousUser() {
        return AuthenticatedUserThreadLocal.get() == null;
    }

    @Deprecated
    public static AutoCloseable asUser(ConfluenceUser user) {
        ConfluenceUser originalUser = AuthenticatedUserThreadLocal.get();
        AuthenticatedUserThreadLocal.set(user);
        return () -> AuthenticatedUserThreadLocal.set(originalUser);
    }

    public static void asUser(ConfluenceUser user, Runnable task) {
        ConfluenceUser originalUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(user);
            task.run();
        }
        finally {
            AuthenticatedUserThreadLocal.set(originalUser);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T asUser(ConfluenceUser user, Callable<T> task) throws Exception {
        ConfluenceUser originalUser = AuthenticatedUserThreadLocal.get();
        try {
            AuthenticatedUserThreadLocal.set(user);
            T t = task.call();
            return t;
        }
        finally {
            AuthenticatedUserThreadLocal.set(originalUser);
        }
    }
}

