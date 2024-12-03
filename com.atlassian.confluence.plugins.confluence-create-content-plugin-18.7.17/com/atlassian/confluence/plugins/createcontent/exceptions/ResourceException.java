/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$StatusType
 */
package com.atlassian.confluence.plugins.createcontent.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceErrorBean;
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
        super(cause, ResourceException.makeResponse(cause.getMessage(), (Response.StatusType)status, errorType, errorData));
    }

    public ResourceException(@Nonnull String cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType) {
        this(cause, status, errorType, null);
    }

    public ResourceException(@Nonnull String cause, @Nonnull Response.Status status, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(ResourceException.makeResponse(cause, (Response.StatusType)status, errorType, errorData));
    }

    static Response makeResponse(@Nonnull String cause, @Nonnull Response.StatusType status, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        return Response.status((Response.StatusType)status).entity((Object)new ResourceErrorBean(status.getStatusCode(), errorType, errorData, cause)).build();
    }
}

