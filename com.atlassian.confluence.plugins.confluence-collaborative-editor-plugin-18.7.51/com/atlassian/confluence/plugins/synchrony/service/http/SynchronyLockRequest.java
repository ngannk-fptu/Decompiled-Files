/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyLockingApiRequest;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;

public class SynchronyLockRequest
implements SynchronyLockingApiRequest {
    private static final int LOCK_SOCKET_TIMEOUT = Integer.getInteger("collab.editing.synchrony.lock.socket.timeout", 5000);
    private final String url;
    private final String token;
    private final Map<String, Object> data;

    public SynchronyLockRequest(String url, String token, Collection<Long> contentIds, Long timeout) {
        this.url = url;
        this.token = token;
        this.data = ImmutableMap.of((Object)"point-list", contentIds, (Object)"timeout-ms", (Object)timeout);
    }

    public SynchronyLockRequest(String url, String token, Long timeout) {
        this.url = url;
        this.token = token;
        this.data = ImmutableMap.of((Object)"confluence-all", (Object)true, (Object)"timeout-ms", (Object)timeout);
    }

    @Override
    public HttpUriRequest getHttpRequest() {
        String json;
        HttpPost post = new HttpPost(this.url);
        post.addHeader("x-token", this.token);
        post.addHeader("Content-Type", "application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.writeValueAsString(this.data);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        post.setEntity((HttpEntity)new StringEntity(json, ContentType.APPLICATION_JSON));
        post.setConfig(RequestConfig.custom().setSocketTimeout(LOCK_SOCKET_TIMEOUT).build());
        return post;
    }

    public String getUrl() {
        return this.url;
    }

    public String getToken() {
        return this.token;
    }

    public Map<String, Object> getData() {
        return this.data;
    }
}

