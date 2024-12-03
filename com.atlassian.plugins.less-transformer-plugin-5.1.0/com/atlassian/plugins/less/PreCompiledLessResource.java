/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.AbstractTransformedDownloadableResource
 *  com.google.common.io.ByteStreams
 */
package com.atlassian.plugins.less;

import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.AbstractTransformedDownloadableResource;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

class PreCompiledLessResource
extends AbstractTransformedDownloadableResource {
    private final UriResolver uriResolver;
    private final URI uri;

    public PreCompiledLessResource(DownloadableResource originalResource, UriResolver uriResolver, URI uri) {
        super(originalResource);
        this.uriResolver = uriResolver;
        this.uri = uri;
    }

    public void streamResource(OutputStream out) throws DownloadException {
        try (InputStream is = this.uriResolver.open(this.uri);){
            ByteStreams.copy((InputStream)is, (OutputStream)out);
        }
        catch (IOException e) {
            throw new DownloadException((Exception)e);
        }
    }
}

