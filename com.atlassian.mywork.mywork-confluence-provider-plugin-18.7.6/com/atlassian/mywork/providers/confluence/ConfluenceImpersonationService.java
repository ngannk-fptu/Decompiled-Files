/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.mywork.service.ImpersonationService
 *  com.atlassian.user.User
 */
package com.atlassian.mywork.providers.confluence;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.mywork.service.ImpersonationService;
import com.atlassian.user.User;
import java.util.concurrent.Callable;

public class ConfluenceImpersonationService
implements ImpersonationService {
    private final UserAccessor userAccessor;

    public ConfluenceImpersonationService(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runAs(String username, Runnable runnable) {
        User oldUser = AuthenticatedUserThreadLocal.getUser();
        AuthenticatedUserThreadLocal.setUser((User)this.userAccessor.getUser(username));
        try {
            runnable.run();
        }
        finally {
            AuthenticatedUserThreadLocal.setUser((User)oldUser);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <V> V runAs(String username, Callable<V> callable) throws Exception {
        User oldUser = AuthenticatedUserThreadLocal.getUser();
        AuthenticatedUserThreadLocal.setUser((User)this.userAccessor.getUser(username));
        try {
            V v = callable.call();
            return v;
        }
        finally {
            AuthenticatedUserThreadLocal.setUser((User)oldUser);
        }
    }
}

