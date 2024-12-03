/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.rometools.rome.feed.atom.Feed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.EntityTag
 *  javax.ws.rs.core.Request
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 */
package com.atlassian.gadgets.publisher.internal.rest;

import com.atlassian.gadgets.publisher.internal.GadgetSpecSyndication;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.rometools.rome.feed.atom.Feed;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path(value="/g/feed")
public class GadgetSpecSyndicationResource {
    private final GadgetSpecSyndication syndication;

    public GadgetSpecSyndicationResource(GadgetSpecSyndication syndication) {
        this.syndication = syndication;
    }

    @GET
    @Produces(value={"application/atom+xml"})
    @AnonymousAllowed
    public Response get(@Context Request request) {
        Feed feed = this.syndication.getFeed();
        Response.ResponseBuilder builder = request.evaluatePreconditions(feed.getUpdated(), this.computeETag(feed));
        if (builder != null) {
            return builder.build();
        }
        return Response.ok((Object)feed).lastModified(feed.getUpdated()).tag(this.computeETag(feed)).build();
    }

    private EntityTag computeETag(Feed feed) {
        return this.computeETag(feed.getUpdated());
    }

    private EntityTag computeETag(Date date) {
        return new EntityTag(Long.toString(date.getTime()));
    }
}

