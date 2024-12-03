/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.client.ClientHandlerException
 *  com.sun.jersey.api.client.ClientRequest
 *  com.sun.jersey.api.client.ClientResponse
 *  com.sun.jersey.api.client.filter.ClientFilter
 */
package com.atlassian.crowd.directory.authentication;

import com.atlassian.crowd.directory.authentication.AzureAdTokenRefresher;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

public class AzureAdRefreshTokenFilter
extends ClientFilter {
    private final AzureAdTokenRefresher azureAdTokenRefresher;

    public AzureAdRefreshTokenFilter(AzureAdTokenRefresher azureAdTokenRefresher) {
        this.azureAdTokenRefresher = azureAdTokenRefresher;
    }

    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        return this.azureAdTokenRefresher.handle(cr, () -> ((AzureAdRefreshTokenFilter)this).getNext());
    }
}

