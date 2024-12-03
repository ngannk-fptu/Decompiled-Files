/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.common.rest.util;

import com.atlassian.applinks.internal.rest.model.RestError;
import com.atlassian.applinks.internal.rest.model.RestErrors;
import java.net.URI;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

public final class RestResponses {
    private RestResponses() {
        throw new AssertionError((Object)("Do not instantiate " + RestResponses.class.getSimpleName()));
    }

    @Nonnull
    public static Response error(@Nonnull Response.Status status, RestError ... errors) {
        return Response.status((Response.Status)status).entity((Object)new RestErrors(status, Arrays.asList(errors))).build();
    }

    @Nonnull
    public static Response error(@Nonnull Response.Status status, @Nonnull String errorMessage) {
        return RestResponses.error(status, new RestError(errorMessage));
    }

    @Nonnull
    public static Response error(@Nonnull Response.Status status, @Nullable String context, @Nonnull String errorMessage) {
        return RestResponses.error(status, new RestError(context, errorMessage, null));
    }

    @Nonnull
    public static Response badRequest(@Nullable String context, @Nonnull String errorMessage) {
        return RestResponses.error(Response.Status.BAD_REQUEST, context, errorMessage);
    }

    @Nonnull
    public static Response badRequest(@Nonnull String errorMessage) {
        return RestResponses.error(Response.Status.BAD_REQUEST, errorMessage);
    }

    @Nonnull
    public static Response noContent() {
        return Response.noContent().build();
    }

    @Nonnull
    public static Response notFound(@Nullable String context, @Nonnull String errorMessage) {
        return RestResponses.error(Response.Status.NOT_FOUND, context, errorMessage);
    }

    @Nonnull
    public static Response notFound(@Nonnull String errorMessage) {
        return RestResponses.notFound(null, errorMessage);
    }

    @Nonnull
    public static Response created(@Nonnull Object entity) {
        return Response.created((URI)URI.create("")).entity(entity).build();
    }
}

