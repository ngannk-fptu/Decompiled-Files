/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import com.atlassian.confluence.plugins.tasklist.rest.ResourceErrorBean;
import com.atlassian.confluence.plugins.tasklist.rest.ResourceErrorType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ResourceException
extends WebApplicationException {
    public ResourceException(@Nonnull Throwable cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType) {
        this(cause, status, errorType, null);
    }

    public ResourceException(@Nonnull Throwable cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(cause, ResourceException.makeResponse(cause.getMessage(), status, errorType, errorData));
    }

    public ResourceException(@Nonnull String cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType) {
        this(cause, status, errorType, null);
    }

    public ResourceException(@Nonnull String cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(ResourceException.makeResponse(cause, status, errorType, errorData));
    }

    static Response makeResponse(@Nonnull String cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        return Response.status((Response.Status)status).entity((Object)new ResourceErrorBean(status.getStatusCode(), errorType.getValue(), errorData, cause)).build();
    }
}

