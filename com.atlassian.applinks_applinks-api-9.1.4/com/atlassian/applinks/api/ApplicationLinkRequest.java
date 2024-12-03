/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

public interface ApplicationLinkRequest
extends Request<ApplicationLinkRequest, Response> {
    public <R> R execute(ApplicationLinkResponseHandler<R> var1) throws ResponseException;
}

