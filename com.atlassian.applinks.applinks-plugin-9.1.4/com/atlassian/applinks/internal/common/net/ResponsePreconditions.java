/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.common.net;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public class ResponsePreconditions {
    @Nonnull
    public static Response checkStatus(@Nonnull Response response, Response.Status ... expectedStatuses) throws ResponseStatusException {
        return ResponsePreconditions.checkStatus(response, (Iterable<Response.Status>)ImmutableSet.copyOf((Object[])expectedStatuses));
    }

    @Nonnull
    public static Response checkStatus(@Nonnull Response response, @Nonnull Iterable<Response.Status> expectedStatuses) throws ResponseStatusException {
        Objects.requireNonNull(expectedStatuses, "expectedStatuses");
        Objects.requireNonNull(response, "response");
        Response.Status responseStatus = Response.Status.fromStatusCode((int)response.getStatusCode());
        if (!Iterables.contains(expectedStatuses, (Object)responseStatus)) {
            throw new ResponseStatusException("Unexpected status: " + response.getStatusCode(), response);
        }
        return response;
    }

    @Nonnull
    public static Response checkStatusOk(@Nonnull Response response) throws ResponseStatusException {
        return ResponsePreconditions.checkStatus(response, Response.Status.OK);
    }

    @Nonnull
    public static Response fail(@Nonnull Response response) throws ResponseStatusException {
        throw new ResponseStatusException("Unexpected status: " + response.getStatusCode(), response);
    }
}

