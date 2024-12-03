/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 */
package com.atlassian.internal.integration.jira.rest;

import com.atlassian.internal.integration.jira.rest.SimpleRestError;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestUtils {
    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    public static CacheControl noCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        return cacheControl;
    }

    public static Response.ResponseBuilder noCache(Response.ResponseBuilder builder) {
        return builder.cacheControl(RestUtils.noCache()).header("Vary", (Object)"X-AUSERNAME").header("Vary", (Object)"Cookie");
    }

    public static Response.ResponseBuilder ok(Object entity) {
        return RestUtils.noCache(Response.ok((Object)entity));
    }

    public static Response.ResponseBuilder serverError(Object entity) {
        return RestUtils.noCache(Response.serverError().entity(entity));
    }

    public static Response.ResponseBuilder readOnlyError(Exception exception) {
        SimpleRestError error = new SimpleRestError(405, "READ_ONLY", exception.getMessage());
        return RestUtils.noCache(Response.status((int)405).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)error));
    }
}

