/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.upm.api.util.Option;
import javax.servlet.http.HttpServletRequest;

public class RequestContext {
    private final boolean pacUnreachable;
    private final Option<HttpServletRequest> request;

    private RequestContext(boolean pacUnreachable, Option<HttpServletRequest> request) {
        this.pacUnreachable = pacUnreachable;
        this.request = request;
    }

    public RequestContext() {
        this(false, Option.none(HttpServletRequest.class));
    }

    public RequestContext(HttpServletRequest request) {
        this(false, Option.some(request));
    }

    public RequestContext pacUnreachable(boolean pacUnreachable) {
        return new RequestContext(pacUnreachable, this.request);
    }

    public boolean isPacUnreachable() {
        return this.pacUnreachable;
    }

    public Option<HttpServletRequest> getRequest() {
        return this.request;
    }
}

