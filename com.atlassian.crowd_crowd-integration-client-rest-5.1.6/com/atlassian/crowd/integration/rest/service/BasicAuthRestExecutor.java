/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.client.AuthenticationMethod
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.google.common.base.Preconditions
 *  org.apache.http.HttpHost
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.impl.client.CloseableHttpClient
 */
package com.atlassian.crowd.integration.rest.service;

import com.atlassian.crowd.integration.rest.service.RestExecutor;
import com.atlassian.crowd.service.client.AuthenticationMethod;
import com.atlassian.crowd.service.client.ClientProperties;
import com.google.common.base.Preconditions;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;

public class BasicAuthRestExecutor
extends RestExecutor {
    public static BasicAuthRestExecutor createFrom(ClientProperties clientProperties, CloseableHttpClient httpClient) {
        Preconditions.checkArgument((clientProperties.getAuthenticationMethod() == AuthenticationMethod.BASIC_AUTH ? 1 : 0) != 0, (Object)"Client properties should specify Basic auth as the authentication method");
        String baseUrl = BasicAuthRestExecutor.createBaseUrl(clientProperties.getBaseURL());
        HttpHost httpHost = BasicAuthRestExecutor.createHttpHost(clientProperties);
        CredentialsProvider credsProvider = BasicAuthRestExecutor.createBasicAuthCredentialsProvider(clientProperties, httpHost);
        return new BasicAuthRestExecutor(baseUrl, httpHost, credsProvider, httpClient);
    }

    private static CredentialsProvider createBasicAuthCredentialsProvider(ClientProperties clientProperties, HttpHost httpHost) {
        CredentialsProvider credsProvider = BasicAuthRestExecutor.createCredentialsProvider(clientProperties);
        Preconditions.checkNotNull((Object)clientProperties.getApplicationName(), (Object)"Missing required Crowd client application name");
        Preconditions.checkNotNull((Object)clientProperties.getApplicationPassword(), (Object)"Missing required Crowd client application password");
        credsProvider.setCredentials(new AuthScope(httpHost), (Credentials)new UsernamePasswordCredentials(clientProperties.getApplicationName(), clientProperties.getApplicationPassword()));
        return credsProvider;
    }

    BasicAuthRestExecutor(String baseUrl, HttpHost httpHost, CredentialsProvider credsProvider, CloseableHttpClient client) {
        super(baseUrl, httpHost, credsProvider, client);
    }
}

