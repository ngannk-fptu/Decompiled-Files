/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.common.net;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public final class HttpUtils {
    private HttpUtils() {
    }

    @Nonnull
    public static String toStatusString(int statusCode) {
        Response.Status status = Response.Status.fromStatusCode((int)statusCode);
        return status != null ? statusCode + ": " + status.getReasonPhrase() : Integer.toString(statusCode);
    }
}

