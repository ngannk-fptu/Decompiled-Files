/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.user.RequestContext
 */
package com.atlassian.business.insights.core.spi;

import com.atlassian.business.insights.api.user.RequestContext;

public class NoopRequestContext
implements RequestContext {
    public void dumpThreadContextInfo() {
    }

    public void runInCustomContext(Runnable action) {
        action.run();
    }
}

