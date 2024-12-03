/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest;

import com.atlassian.crowd.directory.rest.AzureAdPagingWrapper;
import com.atlassian.crowd.directory.rest.AzureAdRestClient;
import com.atlassian.crowd.directory.rest.endpoint.AzureApiUriResolver;

public interface AzureAdRestClientFactory {
    public AzureAdRestClient create(String var1, String var2, String var3, AzureApiUriResolver var4, long var5, long var7);

    default public AzureAdPagingWrapper create(AzureAdRestClient restClient) {
        return new AzureAdPagingWrapper(restClient);
    }
}

