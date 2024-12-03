/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 *  org.apache.http.conn.HttpClientConnectionManager
 */
package com.amazonaws.http.apache.client.impl;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;

public interface ConnectionManagerAwareHttpClient
extends HttpClient {
    public HttpClientConnectionManager getHttpClientConnectionManager();
}

