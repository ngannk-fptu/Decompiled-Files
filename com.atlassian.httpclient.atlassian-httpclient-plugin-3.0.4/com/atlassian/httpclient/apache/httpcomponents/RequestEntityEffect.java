/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  io.atlassian.fugue.Effect
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.EntityByteArrayInputStream;
import com.atlassian.httpclient.api.Request;
import com.google.common.io.ByteStreams;
import io.atlassian.fugue.Effect;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;

public class RequestEntityEffect
implements Effect<HttpRequestBase> {
    private final Request request;

    public RequestEntityEffect(Request request) {
        this.request = request;
    }

    public void apply(HttpRequestBase httpRequestBase) {
        if (!(httpRequestBase instanceof HttpEntityEnclosingRequestBase)) {
            throw new UnsupportedOperationException("HTTP method " + (Object)((Object)this.request.getMethod()) + " does not support sending an entity");
        }
        ((HttpEntityEnclosingRequestBase)httpRequestBase).setEntity(this.getHttpEntity(this.request));
    }

    private HttpEntity getHttpEntity(Request request) {
        AbstractHttpEntity entity = null;
        if (request.hasEntity()) {
            InputStream entityStream = request.getEntityStream();
            if (entityStream instanceof ByteArrayInputStream) {
                byte[] bytes;
                if (entityStream instanceof EntityByteArrayInputStream) {
                    bytes = ((EntityByteArrayInputStream)entityStream).getBytes();
                } else {
                    try {
                        bytes = ByteStreams.toByteArray((InputStream)entityStream);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                entity = new ByteArrayEntity(bytes);
            } else {
                long contentLength = (Long)request.getContentLength().getOrElse((Object)-1L);
                entity = new InputStreamEntity(entityStream, contentLength);
            }
        }
        return entity;
    }
}

