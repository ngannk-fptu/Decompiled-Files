/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.AbstractResource
 *  org.springframework.core.io.Resource
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.ResourceResolverChain;

@Deprecated
public class GzipResourceResolver
extends AbstractResourceResolver {
    @Override
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource resource = chain.resolveResource(request, requestPath, locations);
        if (resource == null || request != null && !this.isGzipAccepted(request)) {
            return resource;
        }
        try {
            GzippedResource gzipped = new GzippedResource(resource);
            if (gzipped.exists()) {
                return gzipped;
            }
        }
        catch (IOException ex) {
            this.logger.trace((Object)("No gzip resource for [" + resource.getFilename() + "]"), (Throwable)ex);
        }
        return resource;
    }

    private boolean isGzipAccepted(HttpServletRequest request) {
        String value = request.getHeader("Accept-Encoding");
        return value != null && value.toLowerCase().contains("gzip");
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourceUrlPath, locations);
    }

    static final class GzippedResource
    extends AbstractResource
    implements HttpResource {
        private final Resource original;
        private final Resource gzipped;

        public GzippedResource(Resource original) throws IOException {
            this.original = original;
            this.gzipped = original.createRelative(original.getFilename() + ".gz");
        }

        public InputStream getInputStream() throws IOException {
            return this.gzipped.getInputStream();
        }

        public boolean exists() {
            return this.gzipped.exists();
        }

        public boolean isReadable() {
            return this.gzipped.isReadable();
        }

        public boolean isOpen() {
            return this.gzipped.isOpen();
        }

        public boolean isFile() {
            return this.gzipped.isFile();
        }

        public URL getURL() throws IOException {
            return this.gzipped.getURL();
        }

        public URI getURI() throws IOException {
            return this.gzipped.getURI();
        }

        public File getFile() throws IOException {
            return this.gzipped.getFile();
        }

        public long contentLength() throws IOException {
            return this.gzipped.contentLength();
        }

        public long lastModified() throws IOException {
            return this.gzipped.lastModified();
        }

        public Resource createRelative(String relativePath) throws IOException {
            return this.gzipped.createRelative(relativePath);
        }

        @Nullable
        public String getFilename() {
            return this.original.getFilename();
        }

        public String getDescription() {
            return this.gzipped.getDescription();
        }

        @Override
        public HttpHeaders getResponseHeaders() {
            HttpHeaders headers = this.original instanceof HttpResource ? ((HttpResource)this.original).getResponseHeaders() : new HttpHeaders();
            headers.add("Content-Encoding", "gzip");
            headers.add("Vary", "Accept-Encoding");
            return headers;
        }
    }
}

