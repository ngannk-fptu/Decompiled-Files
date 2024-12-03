/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory$CacheInvalidationOutOfSequenceEvent
 *  com.atlassian.event.api.EventPublisher
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugin.cacheanalytics.rest;

import com.atlassian.confluence.cache.hazelcast.AsyncInvalidationCacheFactory;
import com.atlassian.confluence.plugin.cacheanalytics.CacheStatisticsEventFactory;
import com.atlassian.event.api.EventPublisher;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/integrationTest")
public class IntegrationTestResource {
    private final EventPublisher eventPublisher;
    private final CacheStatisticsEventFactory cacheStatisticsEventFactory;

    public IntegrationTestResource(EventPublisher eventPublisher, CacheStatisticsEventFactory cacheStatisticsEventFactory) {
        this.eventPublisher = eventPublisher;
        this.cacheStatisticsEventFactory = cacheStatisticsEventFactory;
    }

    @Path(value="/publishCacheStatisticsEvent")
    @POST
    public Response publishCacheStatisticsEvent() {
        this.cacheStatisticsEventFactory.createEvents().forEach(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    @Path(value="/publishCacheInvalidationOutOfSequenceEvent")
    @POST
    public Response publishCacheInvalidationOutOfSequenceEvent() {
        this.eventPublisher.publish((Object)new AsyncInvalidationCacheFactory.CacheInvalidationOutOfSequenceEvent("foo"));
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }
}

