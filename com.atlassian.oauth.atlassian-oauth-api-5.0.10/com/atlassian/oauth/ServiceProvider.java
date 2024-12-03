/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.oauth;

import java.net.URI;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class ServiceProvider {
    private final URI requestTokenUri;
    private final URI authorizeUri;
    private final URI accessTokenUri;

    public ServiceProvider(URI requestTokenUri, URI authorizeUri, URI accessTokenUri) {
        this.requestTokenUri = requestTokenUri;
        this.authorizeUri = authorizeUri;
        this.accessTokenUri = accessTokenUri;
    }

    public URI getRequestTokenUri() {
        return this.requestTokenUri;
    }

    public URI getAuthorizeUri() {
        return this.authorizeUri;
    }

    public URI getAccessTokenUri() {
        return this.accessTokenUri;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("requestTokenUri", (Object)this.requestTokenUri).append("authorizeUri", (Object)this.authorizeUri).append("accessTokenUri", (Object)this.accessTokenUri).toString();
    }
}

