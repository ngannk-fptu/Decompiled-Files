/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 *  org.apache.http.conn.HttpClientConnectionManager
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.apache.internal.impl;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface ConnectionManagerAwareHttpClient
extends HttpClient {
    public HttpClientConnectionManager getHttpClientConnectionManager();
}

