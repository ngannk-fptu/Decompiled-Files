/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpRequest
 *  org.apache.http.ProtocolException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.methods.HttpRequestWrapper
 */
package org.apache.http.impl.client.cache;

import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.impl.client.cache.Variant;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class ConditionalRequestBuilder {
    ConditionalRequestBuilder() {
    }

    public HttpRequestWrapper buildConditionalRequest(HttpRequestWrapper request, HttpCacheEntry cacheEntry) throws ProtocolException {
        Header lastModified;
        HttpRequestWrapper newRequest = HttpRequestWrapper.wrap((HttpRequest)request.getOriginal());
        newRequest.setHeaders(request.getAllHeaders());
        Header eTag = cacheEntry.getFirstHeader("ETag");
        if (eTag != null) {
            newRequest.setHeader("If-None-Match", eTag.getValue());
        }
        if ((lastModified = cacheEntry.getFirstHeader("Last-Modified")) != null) {
            newRequest.setHeader("If-Modified-Since", lastModified.getValue());
        }
        boolean mustRevalidate = false;
        block0: for (Header h : cacheEntry.getHeaders("Cache-Control")) {
            for (HeaderElement elt : h.getElements()) {
                if (!"must-revalidate".equalsIgnoreCase(elt.getName()) && !"proxy-revalidate".equalsIgnoreCase(elt.getName())) continue;
                mustRevalidate = true;
                continue block0;
            }
        }
        if (mustRevalidate) {
            newRequest.addHeader("Cache-Control", "max-age=0");
        }
        return newRequest;
    }

    public HttpRequestWrapper buildConditionalRequestFromVariants(HttpRequestWrapper request, Map<String, Variant> variants) {
        HttpRequestWrapper newRequest = HttpRequestWrapper.wrap((HttpRequest)request.getOriginal());
        newRequest.setHeaders(request.getAllHeaders());
        StringBuilder etags = new StringBuilder();
        boolean first = true;
        for (String etag : variants.keySet()) {
            if (!first) {
                etags.append(",");
            }
            first = false;
            etags.append(etag);
        }
        newRequest.setHeader("If-None-Match", etags.toString());
        return newRequest;
    }

    public HttpRequestWrapper buildUnconditionalRequest(HttpRequestWrapper request, HttpCacheEntry entry) {
        HttpRequestWrapper newRequest = HttpRequestWrapper.wrap((HttpRequest)request.getOriginal());
        newRequest.setHeaders(request.getAllHeaders());
        newRequest.addHeader("Cache-Control", "no-cache");
        newRequest.addHeader("Pragma", "no-cache");
        newRequest.removeHeaders("If-Range");
        newRequest.removeHeaders("If-Match");
        newRequest.removeHeaders("If-None-Match");
        newRequest.removeHeaders("If-Unmodified-Since");
        newRequest.removeHeaders("If-Modified-Since");
        return newRequest;
    }
}

