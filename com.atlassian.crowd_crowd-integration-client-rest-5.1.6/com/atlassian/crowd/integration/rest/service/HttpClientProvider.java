/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.client.ClientProperties
 *  org.apache.http.impl.client.CloseableHttpClient
 */
package com.atlassian.crowd.integration.rest.service;

import com.atlassian.crowd.service.client.ClientProperties;
import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpClientProvider {
    public CloseableHttpClient getClient(ClientProperties var1);
}

