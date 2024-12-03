/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.crowd.directory.rest.endpoint;

import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import com.atlassian.crowd.directory.rest.endpoint.DefaultRegion;
import javax.ws.rs.core.UriBuilder;

public class BasicAzureApiUriResolver
implements AzureApiUriResolver {
    private final DefaultRegion region;

    public BasicAzureApiUriResolver(DefaultRegion region) {
        this.region = region;
    }

    @Override
    public String getGraphApiUrl() {
        return this.region.getGraphApiUrl();
    }

    @Override
    public String getAuthorityApiUrl(String tenantId) {
        return UriBuilder.fromUri((String)this.region.getBasicAuthorityApiUrl()).path(tenantId).build(new Object[0]).toString();
    }
}

