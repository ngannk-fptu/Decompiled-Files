/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.LoadingCache
 *  com.sun.jersey.api.client.ClientHandler
 *  com.sun.jersey.api.client.ClientRequest
 *  com.sun.jersey.api.client.ClientResponse
 *  com.sun.jersey.api.client.ClientResponse$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.authentication;

import com.google.common.cache.LoadingCache;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureAdTokenRefresher {
    public static final String AZURE_AD_TOKEN_CACHE_KEY = "AZURE_AD_TOKEN";
    private static final Logger log = LoggerFactory.getLogger(AzureAdTokenRefresher.class);
    private final LoadingCache<String, String> tokenCache;

    public AzureAdTokenRefresher(LoadingCache<String, String> tokenCache) {
        this.tokenCache = tokenCache;
    }

    public ClientResponse handle(ClientRequest request, Supplier<ClientHandler> next) {
        this.setTokenInRequest(request);
        ClientResponse response = next.get().handle(request);
        if (response.getClientResponseStatus() == ClientResponse.Status.UNAUTHORIZED) {
            if (log.isDebugEnabled()) {
                log.debug("Got a 401 response from Microsoft Graph, retrying the request. Response body: {}", response.getEntity(String.class));
            }
            this.tokenCache.invalidate((Object)AZURE_AD_TOKEN_CACHE_KEY);
            this.setTokenInRequest(request);
            return next.get().handle(request);
        }
        return response;
    }

    private void setTokenInRequest(ClientRequest cr) {
        String newAzureAdToken = (String)this.tokenCache.getUnchecked((Object)AZURE_AD_TOKEN_CACHE_KEY);
        cr.getHeaders().putSingle((Object)"Authorization", (Object)newAzureAdToken);
    }
}

