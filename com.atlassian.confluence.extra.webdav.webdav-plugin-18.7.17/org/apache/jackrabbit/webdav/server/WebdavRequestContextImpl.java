/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.server;

import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestContext;

class WebdavRequestContextImpl
implements WebdavRequestContext {
    private final WebdavRequest request;

    WebdavRequestContextImpl(WebdavRequest request) {
        this.request = request;
    }

    @Override
    public WebdavRequest getRequest() {
        return this.request;
    }
}

