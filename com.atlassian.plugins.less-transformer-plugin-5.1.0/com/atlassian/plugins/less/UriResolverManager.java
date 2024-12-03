/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.UriResolver
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.spi.UriResolver;
import java.net.URI;
import javax.annotation.Nullable;

public interface UriResolverManager {
    public Iterable<UriResolver> getResolvers();

    default public UriResolver getResolverOrThrow(URI uri) {
        UriResolver uriResolver = this.getResolver(uri);
        if (uriResolver == null) {
            throw new IllegalArgumentException("Unsupported URI " + uri.toASCIIString());
        }
        return uriResolver;
    }

    default public boolean isUriSupported(URI uri) {
        return this.getResolver(uri) != null;
    }

    @Nullable
    default public UriResolver getResolver(URI uri) {
        for (UriResolver uriResolver : this.getResolvers()) {
            if (!uriResolver.supports(uri)) continue;
            return uriResolver;
        }
        return null;
    }
}

