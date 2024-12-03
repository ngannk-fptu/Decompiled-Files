/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.applinks.internal.util.remote;

import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

public abstract class AnonymousApplinksResponseHandler<R>
implements ApplicationLinkResponseHandler<R> {
    public R credentialsRequired(Response response) throws ResponseException {
        throw new IllegalStateException("Credentials required invoked for anonymous request");
    }
}

