/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.impl.client.CloseableHttpClient
 */
package com.atlassian.plugins.navlink.consumer.http;

import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpClientFactory {
    public CloseableHttpClient createHttpClient();
}

