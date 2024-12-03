/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.core.util.RedirectHelper;

public abstract class AbstractApplicationLinkResponseHandler {
    protected final ApplicationLinkRequest wrappedRequest;
    protected final boolean followRedirects;
    protected final RedirectHelper redirectHelper;

    public AbstractApplicationLinkResponseHandler(String url, ApplicationLinkRequest wrappedRequest, boolean followRedirects) {
        this.wrappedRequest = wrappedRequest;
        this.followRedirects = followRedirects;
        this.redirectHelper = new RedirectHelper(url);
    }
}

