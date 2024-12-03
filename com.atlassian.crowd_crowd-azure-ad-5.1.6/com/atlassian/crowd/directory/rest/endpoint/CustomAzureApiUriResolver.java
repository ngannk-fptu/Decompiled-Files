/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.crowd.directory.rest.endpoint;

import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;
import javax.ws.rs.core.UriBuilder;

public class CustomAzureApiUriResolver
implements AzureApiUriResolver {
    private final String graphApi;
    private final String authorityApi;

    public CustomAzureApiUriResolver(String graphApi, String authorityApi) {
        this.graphApi = graphApi;
        this.authorityApi = authorityApi;
    }

    @Override
    public String getGraphApiUrl() {
        return this.graphApi;
    }

    @Override
    public String getAuthorityApiUrl(String tenantId) {
        return UriBuilder.fromUri((String)this.authorityApi).path(tenantId).build(new Object[0]).toString();
    }
}

