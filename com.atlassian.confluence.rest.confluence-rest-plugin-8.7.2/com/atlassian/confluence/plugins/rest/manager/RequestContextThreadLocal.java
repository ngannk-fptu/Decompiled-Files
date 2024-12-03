/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.plugins.rest.manager.RequestContext;

public class RequestContextThreadLocal {
    private static ThreadLocal<RequestContext> threadLocal = new ThreadLocal();

    public static RequestContext get() {
        return threadLocal.get();
    }

    public static void set(RequestContext requestContext) {
        threadLocal.set(requestContext);
    }
}

