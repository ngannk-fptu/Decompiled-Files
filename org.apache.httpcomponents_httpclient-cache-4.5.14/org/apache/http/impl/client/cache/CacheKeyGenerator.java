/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Consts
 *  org.apache.http.Header
 *  org.apache.http.HeaderElement
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.client.utils.URIBuilder
 *  org.apache.http.client.utils.URIUtils
 *  org.apache.http.client.utils.URLEncodedUtils
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client.cache;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
class CacheKeyGenerator {
    private static final URI BASE_URI = URI.create("http://example.com/");

    CacheKeyGenerator() {
    }

    static URIBuilder getRequestUriBuilder(HttpRequest request) throws URISyntaxException {
        URI uri;
        if (request instanceof HttpUriRequest && (uri = ((HttpUriRequest)request).getURI()) != null) {
            return new URIBuilder(uri);
        }
        return new URIBuilder(request.getRequestLine().getUri());
    }

    static URI getRequestUri(HttpRequest request, HttpHost target) throws URISyntaxException {
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)target, (String)"Target");
        URIBuilder uriBuilder = CacheKeyGenerator.getRequestUriBuilder(request);
        String path = uriBuilder.getPath();
        if (path != null) {
            uriBuilder.setPathSegments(URLEncodedUtils.parsePathSegments((CharSequence)path));
        }
        if (!uriBuilder.isAbsolute()) {
            uriBuilder.setScheme(target.getSchemeName());
            uriBuilder.setHost(target.getHostName());
            uriBuilder.setPort(target.getPort());
        }
        return uriBuilder.build();
    }

    static URI normalize(URI requestUri) throws URISyntaxException {
        Args.notNull((Object)requestUri, (String)"URI");
        URIBuilder builder = new URIBuilder(requestUri.isAbsolute() ? URIUtils.resolve((URI)BASE_URI, (URI)requestUri) : requestUri);
        if (builder.getHost() != null) {
            if (builder.getScheme() == null) {
                builder.setScheme("http");
            }
            if (builder.getPort() <= -1) {
                if ("http".equalsIgnoreCase(builder.getScheme())) {
                    builder.setPort(80);
                } else if ("https".equalsIgnoreCase(builder.getScheme())) {
                    builder.setPort(443);
                }
            }
        }
        builder.setFragment(null);
        return builder.build();
    }

    public String getURI(HttpHost host, HttpRequest req) {
        try {
            URI uri = CacheKeyGenerator.normalize(CacheKeyGenerator.getRequestUri(req, host));
            return uri.toASCIIString();
        }
        catch (URISyntaxException ex) {
            return req.getRequestLine().getUri();
        }
    }

    public String canonicalizeUri(String uri) {
        try {
            URI normalized = CacheKeyGenerator.normalize(URIUtils.resolve((URI)BASE_URI, (String)uri));
            return normalized.toASCIIString();
        }
        catch (URISyntaxException ex) {
            return uri;
        }
    }

    protected String getFullHeaderValue(Header[] headers) {
        if (headers == null) {
            return "";
        }
        StringBuilder buf = new StringBuilder("");
        boolean first = true;
        for (Header hdr : headers) {
            if (!first) {
                buf.append(", ");
            }
            buf.append(hdr.getValue().trim());
            first = false;
        }
        return buf.toString();
    }

    public String getVariantURI(HttpHost host, HttpRequest req, HttpCacheEntry entry) {
        if (!entry.hasVariants()) {
            return this.getURI(host, req);
        }
        return this.getVariantKey(req, entry) + this.getURI(host, req);
    }

    public String getVariantKey(HttpRequest req, HttpCacheEntry entry) {
        StringBuilder buf;
        ArrayList<String> variantHeaderNames = new ArrayList<String>();
        for (Header varyHdr : entry.getHeaders("Vary")) {
            for (HeaderElement elt : varyHdr.getElements()) {
                variantHeaderNames.add(elt.getName());
            }
        }
        Collections.sort(variantHeaderNames);
        try {
            buf = new StringBuilder("{");
            boolean first = true;
            for (String headerName : variantHeaderNames) {
                if (!first) {
                    buf.append("&");
                }
                buf.append(URLEncoder.encode(headerName, Consts.UTF_8.name()));
                buf.append("=");
                buf.append(URLEncoder.encode(this.getFullHeaderValue(req.getHeaders(headerName)), Consts.UTF_8.name()));
                first = false;
            }
            buf.append("}");
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("couldn't encode to UTF-8", uee);
        }
        return buf.toString();
    }
}

