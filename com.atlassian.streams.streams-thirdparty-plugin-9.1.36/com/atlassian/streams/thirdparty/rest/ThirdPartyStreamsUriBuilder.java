/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  com.sun.jersey.api.uri.UriBuilderImpl
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.streams.thirdparty.rest;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.streams.thirdparty.rest.resources.ThirdPartyStreamsCollectionResource;
import com.atlassian.streams.thirdparty.rest.resources.ThirdPartyStreamsResource;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.uri.UriBuilderImpl;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

public class ThirdPartyStreamsUriBuilder {
    private final ApplicationProperties applicationProperties;

    public ThirdPartyStreamsUriBuilder(ApplicationProperties applicationProperties) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    public final URI buildActivityUri(Long activityId) {
        return this.newBaseUriBuilder().path(ThirdPartyStreamsResource.class).build(new Object[]{activityId});
    }

    public final URI buildAbsoluteActivityUri(Long activityId) {
        return this.makeAbsolute(this.buildActivityUri(activityId));
    }

    public final URI buildActivityCollectionUri() {
        return this.buildActivityCollectionUri(10, 0);
    }

    public final URI buildActivityCollectionUri(int maxResults, int startIndex) {
        UriBuilder uriBuilder = this.newBaseUriBuilder().path(ThirdPartyStreamsCollectionResource.class);
        if (maxResults != 10) {
            uriBuilder = uriBuilder.queryParam("max-results", new Object[]{maxResults});
        }
        if (startIndex != 0) {
            uriBuilder = uriBuilder.queryParam("start-index", new Object[]{startIndex});
        }
        return uriBuilder.build(new Object[0]);
    }

    public final URI makeAbsolute(URI uri) {
        if (uri.isAbsolute()) {
            return uri;
        }
        return URI.create(this.applicationProperties.getBaseUrl()).resolve(uri).normalize();
    }

    protected UriBuilder newBaseUriBuilder() {
        return this.newApplicationBaseUriBuilder().path("/rest/activities/1.0");
    }

    private UriBuilder newApplicationBaseUriBuilder() {
        URI base = URI.create(this.applicationProperties.getBaseUrl()).normalize();
        return new UriBuilderImpl().path(base.getPath());
    }
}

