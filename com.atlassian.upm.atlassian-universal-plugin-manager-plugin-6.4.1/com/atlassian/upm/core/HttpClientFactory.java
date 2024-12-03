/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 */
package com.atlassian.upm.core;

import org.apache.http.client.HttpClient;

public interface HttpClientFactory {
    public HttpClient createClient();
}

