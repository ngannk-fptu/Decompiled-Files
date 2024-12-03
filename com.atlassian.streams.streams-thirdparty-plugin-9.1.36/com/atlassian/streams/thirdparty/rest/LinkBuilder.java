/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.thirdparty.rest;

import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.rest.ThirdPartyStreamsUriBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.util.Map;

public class LinkBuilder {
    private final ThirdPartyStreamsUriBuilder uriBuilder;

    public LinkBuilder(ThirdPartyStreamsUriBuilder uriBuilder) {
        this.uriBuilder = (ThirdPartyStreamsUriBuilder)Preconditions.checkNotNull((Object)uriBuilder, (Object)"uriBuilder");
    }

    public Map<String, URI> build(Iterable<Activity> activities, ActivityQuery query) {
        int startIndex = query.getStartIndex();
        int maxResults = query.getMaxResults();
        ImmutableMap.Builder links = ImmutableMap.builder();
        links.put((Object)"self", (Object)this.uriBuilder.buildActivityCollectionUri(maxResults, startIndex));
        if (startIndex != 0) {
            links.put((Object)"prev", (Object)this.uriBuilder.buildActivityCollectionUri(maxResults, Math.max(0, startIndex - maxResults)));
        }
        if (Iterables.size(activities) == maxResults) {
            links.put((Object)"next", (Object)this.uriBuilder.buildActivityCollectionUri(maxResults, startIndex + maxResults));
        }
        return links.build();
    }

    public Map<String, URI> build(Activity activity) {
        ImmutableMap.Builder links = ImmutableMap.builder();
        for (Long activityId : activity.getActivityId()) {
            links.put((Object)"self", (Object)this.uriBuilder.buildActivityUri(activityId));
            links.put((Object)"activities", (Object)this.uriBuilder.buildActivityCollectionUri());
        }
        return links.build();
    }
}

