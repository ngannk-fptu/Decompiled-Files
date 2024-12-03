/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpUriRequest
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyLockingApiRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;

public class SynchronyUnlockRequest
implements SynchronyLockingApiRequest {
    private static final int UNLOCK_SOCKET_TIMEOUT = Integer.getInteger("collab.editing.synchrony.lock.socket.timeout", 3000);
    private final String url;
    private final String token;

    public SynchronyUnlockRequest(String url, String token) {
        this.url = url;
        this.token = token;
    }

    @Override
    public HttpUriRequest getHttpRequest() {
        HttpDelete delete = new HttpDelete(this.url);
        delete.addHeader("x-token", this.token);
        delete.addHeader("Content-Type", "application/json");
        delete.setConfig(RequestConfig.custom().setSocketTimeout(UNLOCK_SOCKET_TIMEOUT).build());
        return delete;
    }

    public String getUrl() {
        return this.url;
    }

    public String getToken() {
        return this.token;
    }
}

