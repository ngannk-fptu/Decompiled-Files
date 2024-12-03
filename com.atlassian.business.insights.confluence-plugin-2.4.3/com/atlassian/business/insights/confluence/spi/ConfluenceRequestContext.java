/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.user.RequestContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.business.insights.confluence.spi;

import com.atlassian.business.insights.api.user.RequestContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.concurrent.atomic.AtomicReference;

public class ConfluenceRequestContext
implements RequestContext {
    private final AtomicReference<ConfluenceUser> userReference = new AtomicReference();

    public void dumpThreadContextInfo() {
        this.userReference.set(AuthenticatedUserThreadLocal.get());
    }

    public void runInCustomContext(Runnable action) {
        AuthenticatedUserThreadLocal.set((ConfluenceUser)this.userReference.get());
        action.run();
    }
}

