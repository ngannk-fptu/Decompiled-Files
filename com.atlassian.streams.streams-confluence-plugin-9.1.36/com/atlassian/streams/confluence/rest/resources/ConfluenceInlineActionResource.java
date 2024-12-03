/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.streams.confluence.rest.resources;

import com.atlassian.streams.confluence.ConfluenceWatchInlineActionHandler;
import com.atlassian.streams.confluence.ConfluenceWatchPageInlineActionHandler;
import com.atlassian.streams.confluence.ConfluenceWatchSpaceInlineActionHandler;
import com.google.common.base.Preconditions;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path(value="/actions")
public class ConfluenceInlineActionResource {
    private final ConfluenceWatchPageInlineActionHandler pageWatchHandler;
    private final ConfluenceWatchSpaceInlineActionHandler spaceWatchHandler;

    public ConfluenceInlineActionResource(ConfluenceWatchPageInlineActionHandler pageWatchHandler, ConfluenceWatchSpaceInlineActionHandler spaceWatchHandler) {
        this.pageWatchHandler = (ConfluenceWatchPageInlineActionHandler)Preconditions.checkNotNull((Object)pageWatchHandler, (Object)"pageWatchHandler");
        this.spaceWatchHandler = (ConfluenceWatchSpaceInlineActionHandler)Preconditions.checkNotNull((Object)spaceWatchHandler, (Object)"spaceWatchHandler");
    }

    @Path(value="page-watch/{key}")
    @POST
    public Response watchPage(@PathParam(value="key") Long key) {
        return this.watchEntity(this.pageWatchHandler, key);
    }

    @Path(value="space-watch/{key}")
    @POST
    public Response watchSpace(@PathParam(value="key") String key) {
        return this.watchEntity(this.spaceWatchHandler, key);
    }

    private <K> Response watchEntity(ConfluenceWatchInlineActionHandler<K> handler, K key) {
        boolean success = handler.startWatching(key);
        if (success) {
            return Response.noContent().build();
        }
        return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
    }
}

