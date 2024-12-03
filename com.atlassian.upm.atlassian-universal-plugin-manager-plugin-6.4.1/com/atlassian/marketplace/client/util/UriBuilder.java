/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.http.client.utils.URIBuilder
 */
package com.atlassian.marketplace.client.util;

import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;

public class UriBuilder {
    private final URIBuilder builder;

    private UriBuilder(URIBuilder builder) {
        this.builder = (URIBuilder)Preconditions.checkNotNull((Object)builder);
    }

    public static UriBuilder fromUri(URI uri) {
        return new UriBuilder(new URIBuilder((URI)Preconditions.checkNotNull((Object)uri)));
    }

    public static UriBuilder fromUri(String uri) {
        try {
            return new UriBuilder(new URIBuilder((String)Preconditions.checkNotNull((Object)uri)));
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public URI build() {
        try {
            return this.builder.build();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public UriBuilder host(String host) {
        this.builder.setHost((String)Preconditions.checkNotNull((Object)host));
        return this;
    }

    public UriBuilder path(String addPath) {
        Preconditions.checkNotNull((Object)addPath);
        String old = this.builder.getPath();
        if (!old.endsWith("/") && !addPath.startsWith("/")) {
            old = old + "/";
        }
        this.builder.setPath(old + addPath);
        return this;
    }

    public UriBuilder queryParam(String name, Object ... value) {
        for (Object v : (Object[])Preconditions.checkNotNull((Object)value)) {
            this.builder.addParameter((String)Preconditions.checkNotNull((Object)name), String.valueOf(Preconditions.checkNotNull((Object)v)));
        }
        return this;
    }
}

