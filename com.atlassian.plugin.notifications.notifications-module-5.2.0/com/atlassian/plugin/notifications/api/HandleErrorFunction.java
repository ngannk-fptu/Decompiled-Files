/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.plugin.notifications.api;

import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.google.common.base.Function;
import java.util.Set;
import javax.annotation.Nullable;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

public class HandleErrorFunction
implements Function<ErrorCollection, Response> {
    public static final CacheControl NO_CACHE = new CacheControl();

    public Response apply(@Nullable ErrorCollection input) {
        if (input == null) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).cacheControl(NO_CACHE).build();
        }
        Set<ErrorCollection.Reason> reasons = input.getReasons();
        if (reasons.contains((Object)ErrorCollection.Reason.NOT_FOUND)) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)input).cacheControl(NO_CACHE).build();
        }
        if (reasons.contains((Object)ErrorCollection.Reason.FORBIDDEN) || reasons.contains((Object)ErrorCollection.Reason.NOT_LOGGED_IN)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity((Object)input).cacheControl(NO_CACHE).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)input).cacheControl(NO_CACHE).build();
    }

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
    }
}

