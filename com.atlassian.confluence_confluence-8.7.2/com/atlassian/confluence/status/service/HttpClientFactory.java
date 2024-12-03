/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.impl.client.CloseableHttpClient
 */
package com.atlassian.confluence.status.service;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;

interface HttpClientFactory {
    public CloseableHttpClient getInstance();

    public CloseableHttpClient getInstance(RequestConfig var1);
}

