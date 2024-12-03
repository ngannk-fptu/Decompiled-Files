/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpRequestWrapper
 *  org.apache.http.client.utils.DateUtils
 *  org.apache.http.message.BasicHeader
 *  org.apache.http.message.BasicHttpResponse
 */
package org.apache.http.impl.client.cache;

import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.cache.CacheEntity;
import org.apache.http.impl.client.cache.CacheValidityPolicy;
import org.apache.http.impl.client.cache.Proxies;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
class CachedHttpResponseGenerator {
    private final CacheValidityPolicy validityStrategy;

    CachedHttpResponseGenerator(CacheValidityPolicy validityStrategy) {
        this.validityStrategy = validityStrategy;
    }

    CachedHttpResponseGenerator() {
        this(new CacheValidityPolicy());
    }

    CloseableHttpResponse generateResponse(HttpRequestWrapper request, HttpCacheEntry entry) {
        long age;
        Date now = new Date();
        BasicHttpResponse response = new BasicHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, entry.getStatusCode(), entry.getReasonPhrase());
        response.setHeaders(entry.getAllHeaders());
        if (this.responseShouldContainEntity(request, entry)) {
            CacheEntity entity = new CacheEntity(entry);
            this.addMissingContentLengthHeader((HttpResponse)response, entity);
            response.setEntity((HttpEntity)entity);
        }
        if ((age = this.validityStrategy.getCurrentAgeSecs(entry, now)) > 0L) {
            if (age >= Integer.MAX_VALUE) {
                response.setHeader("Age", "2147483648");
            } else {
                response.setHeader("Age", "" + (int)age);
            }
        }
        return Proxies.enhanceResponse((HttpResponse)response);
    }

    CloseableHttpResponse generateNotModifiedResponse(HttpCacheEntry entry) {
        Header varyHeader;
        Header cacheControlHeader;
        Header expiresHeader;
        Header contentLocationHeader;
        BasicHttpResponse response = new BasicHttpResponse((ProtocolVersion)HttpVersion.HTTP_1_1, 304, "Not Modified");
        Header dateHeader = entry.getFirstHeader("Date");
        if (dateHeader == null) {
            dateHeader = new BasicHeader("Date", DateUtils.formatDate((Date)new Date()));
        }
        response.addHeader(dateHeader);
        Header etagHeader = entry.getFirstHeader("ETag");
        if (etagHeader != null) {
            response.addHeader(etagHeader);
        }
        if ((contentLocationHeader = entry.getFirstHeader("Content-Location")) != null) {
            response.addHeader(contentLocationHeader);
        }
        if ((expiresHeader = entry.getFirstHeader("Expires")) != null) {
            response.addHeader(expiresHeader);
        }
        if ((cacheControlHeader = entry.getFirstHeader("Cache-Control")) != null) {
            response.addHeader(cacheControlHeader);
        }
        if ((varyHeader = entry.getFirstHeader("Vary")) != null) {
            response.addHeader(varyHeader);
        }
        return Proxies.enhanceResponse((HttpResponse)response);
    }

    private void addMissingContentLengthHeader(HttpResponse response, HttpEntity entity) {
        if (this.transferEncodingIsPresent(response)) {
            return;
        }
        BasicHeader contentLength = new BasicHeader("Content-Length", Long.toString(entity.getContentLength()));
        response.setHeader((Header)contentLength);
    }

    private boolean transferEncodingIsPresent(HttpResponse response) {
        Header hdr = response.getFirstHeader("Transfer-Encoding");
        return hdr != null;
    }

    private boolean responseShouldContainEntity(HttpRequestWrapper request, HttpCacheEntry cacheEntry) {
        return request.getRequestLine().getMethod().equals("GET") && cacheEntry.getResource() != null;
    }
}

