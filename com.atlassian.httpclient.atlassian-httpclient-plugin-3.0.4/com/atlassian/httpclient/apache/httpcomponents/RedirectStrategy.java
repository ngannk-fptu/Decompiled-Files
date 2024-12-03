/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents;

import java.net.URI;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class RedirectStrategy
extends DefaultRedirectStrategy {
    final String[] REDIRECT_METHODS = new String[]{"HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"};

    @Override
    public boolean isRedirectable(String method) {
        for (String m : this.REDIRECT_METHODS) {
            if (!m.equalsIgnoreCase(method)) continue;
            return true;
        }
        return false;
    }

    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = this.getLocationURI(request, response, context);
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        if (method.equalsIgnoreCase("GET")) {
            return new HttpGet(uri);
        }
        if (method.equalsIgnoreCase("POST")) {
            HttpPost post = new HttpPost(uri);
            if (request instanceof HttpEntityEnclosingRequest) {
                post.setEntity(((HttpEntityEnclosingRequest)request).getEntity());
            }
            return post;
        }
        if (method.equalsIgnoreCase("PUT")) {
            return new HttpPut(uri);
        }
        if (method.equalsIgnoreCase("DELETE")) {
            return new HttpDelete(uri);
        }
        if (method.equalsIgnoreCase("PATCH")) {
            return new HttpPatch(uri);
        }
        return new HttpGet(uri);
    }
}

