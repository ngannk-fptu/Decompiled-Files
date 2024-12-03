/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.server;

import org.apache.jackrabbit.webdav.WebdavRequestContext;

public final class WebdavRequestContextHolder {
    private static ThreadLocal<WebdavRequestContext> tlWebdavRequestContext = new ThreadLocal();

    private WebdavRequestContextHolder() {
    }

    public static WebdavRequestContext getContext() {
        return tlWebdavRequestContext.get();
    }

    static void setContext(WebdavRequestContext context) {
        tlWebdavRequestContext.set(context);
    }

    static void clearContext() {
        tlWebdavRequestContext.remove();
    }
}

