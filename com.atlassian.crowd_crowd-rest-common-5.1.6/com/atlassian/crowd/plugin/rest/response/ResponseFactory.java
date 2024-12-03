/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.crowd.plugin.rest.response;

import java.net.URI;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

public class ResponseFactory {
    private static final CacheControl NO_CACHE = new CacheControl();

    private ResponseFactory() {
    }

    public static CacheControl never() {
        return NO_CACHE;
    }

    private static Response.ResponseBuilder applyDefaults(Response.ResponseBuilder builder) {
        return builder.cacheControl(ResponseFactory.never()).header("Vary", (Object)"X-AUSERNAME").header("Vary", (Object)"Cookie");
    }

    public static Response.ResponseBuilder ok() {
        return ResponseFactory.applyDefaults(Response.ok());
    }

    public static Response.ResponseBuilder ok(Object entity) {
        return ResponseFactory.applyDefaults(Response.ok((Object)entity));
    }

    public static Response.ResponseBuilder ok(Object entity, CacheControl cacheControl) {
        return ResponseFactory.applyDefaults(Response.ok((Object)entity)).cacheControl(cacheControl);
    }

    public static Response.ResponseBuilder created(URI location) {
        return ResponseFactory.applyDefaults(Response.created((URI)location));
    }

    public static Response.ResponseBuilder noContent() {
        return ResponseFactory.applyDefaults(Response.noContent());
    }

    public static Response.ResponseBuilder status(Response.Status status) {
        return ResponseFactory.applyDefaults(Response.status((Response.Status)status));
    }

    public static Response.ResponseBuilder serverError() {
        return ResponseFactory.applyDefaults(Response.serverError());
    }

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
    }
}

