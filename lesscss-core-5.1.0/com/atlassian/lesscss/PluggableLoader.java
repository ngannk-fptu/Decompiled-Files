/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.google.common.base.Charsets
 *  com.google.common.base.Objects
 *  com.google.common.io.ByteStreams
 *  com.google.common.io.CharStreams
 */
package com.atlassian.lesscss;

import com.atlassian.lesscss.DataUriUtils;
import com.atlassian.lesscss.Loader;
import com.atlassian.lesscss.spi.UriResolver;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class PluggableLoader
implements Loader {
    private final Iterable<UriResolver> uriResolvers;

    public PluggableLoader(Iterable<UriResolver> uriResolvers) {
        this.uriResolvers = uriResolvers;
    }

    @Override
    public URI resolve(URI baseUri, String path) {
        URI resolved = baseUri.resolve(path);
        if (Objects.equal((Object)baseUri.getScheme(), (Object)resolved.getScheme())) {
            return resolved;
        }
        for (UriResolver uriResolver : this.uriResolvers) {
            if (!uriResolver.supports(resolved)) continue;
            return resolved;
        }
        return null;
    }

    @Override
    public String load(URI uri) throws IOException {
        for (UriResolver uriResolver : this.uriResolvers) {
            if (!uriResolver.supports(uri)) continue;
            if (!uriResolver.exists(uri)) {
                return null;
            }
            try (InputStreamReader reader = new InputStreamReader(uriResolver.open(uri), Charsets.UTF_8);){
                String string = CharStreams.toString((Readable)reader);
                return string;
            }
        }
        throw new IOException("Unsupported uri: " + uri.toASCIIString());
    }

    @Override
    public String dataUri(String mimeType, URI uri) throws IOException {
        if (mimeType == null) {
            mimeType = DataUriUtils.guessMimeType(uri.toASCIIString());
        }
        for (UriResolver uriResolver : this.uriResolvers) {
            if (!uriResolver.supports(uri)) continue;
            if (!uriResolver.exists(uri)) {
                return null;
            }
            try (InputStream is = uriResolver.open(uri);){
                String string = DataUriUtils.dataUri(mimeType, ByteStreams.toByteArray((InputStream)is));
                return string;
            }
        }
        throw new IOException("Unsupported uri: " + uri.toASCIIString());
    }
}

