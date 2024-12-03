/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.AbstractResource
 *  org.springframework.core.io.Resource
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class EncodedResourceResolver
extends AbstractResourceResolver {
    public static final List<String> DEFAULT_CODINGS = Arrays.asList("br", "gzip");
    private final List<String> contentCodings = new ArrayList<String>(DEFAULT_CODINGS);
    private final Map<String, String> extensions = new LinkedHashMap<String, String>();

    public EncodedResourceResolver() {
        this.extensions.put("gzip", ".gz");
        this.extensions.put("br", ".br");
    }

    public void setContentCodings(List<String> codings) {
        Assert.notEmpty(codings, (String)"At least one content coding expected");
        this.contentCodings.clear();
        this.contentCodings.addAll(codings);
    }

    public List<String> getContentCodings() {
        return Collections.unmodifiableList(this.contentCodings);
    }

    public void setExtensions(Map<String, String> extensions) {
        extensions.forEach(this::registerExtension);
    }

    public Map<String, String> getExtensions() {
        return Collections.unmodifiableMap(this.extensions);
    }

    public void registerExtension(String coding, String extension) {
        this.extensions.put(coding, extension.startsWith(".") ? extension : "." + extension);
    }

    @Override
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource resource = chain.resolveResource(request, requestPath, locations);
        if (resource == null || request == null) {
            return resource;
        }
        String acceptEncoding = this.getAcceptEncoding(request);
        if (acceptEncoding == null) {
            return resource;
        }
        for (String coding : this.contentCodings) {
            if (!acceptEncoding.contains(coding)) continue;
            try {
                String extension = this.getExtension(coding);
                EncodedResource encoded = new EncodedResource(resource, coding, extension);
                if (!encoded.exists()) continue;
                return encoded;
            }
            catch (IOException ex) {
                if (!this.logger.isTraceEnabled()) continue;
                this.logger.trace((Object)("No " + coding + " resource for [" + resource.getFilename() + "]"), (Throwable)ex);
            }
        }
        return resource;
    }

    @Nullable
    private String getAcceptEncoding(HttpServletRequest request) {
        String header = request.getHeader("Accept-Encoding");
        return header != null ? header.toLowerCase() : null;
    }

    private String getExtension(String coding) {
        String extension = this.extensions.get(coding);
        if (extension == null) {
            throw new IllegalStateException("No file extension associated with content coding " + coding);
        }
        return extension;
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourceUrlPath, locations);
    }

    static final class EncodedResource
    extends AbstractResource
    implements HttpResource {
        private final Resource original;
        private final String coding;
        private final Resource encoded;

        EncodedResource(Resource original, String coding, String extension) throws IOException {
            this.original = original;
            this.coding = coding;
            this.encoded = original.createRelative(original.getFilename() + extension);
        }

        public boolean exists() {
            return this.encoded.exists();
        }

        public boolean isReadable() {
            return this.encoded.isReadable();
        }

        public boolean isOpen() {
            return this.encoded.isOpen();
        }

        public boolean isFile() {
            return this.encoded.isFile();
        }

        public URL getURL() throws IOException {
            return this.encoded.getURL();
        }

        public URI getURI() throws IOException {
            return this.encoded.getURI();
        }

        public File getFile() throws IOException {
            return this.encoded.getFile();
        }

        public InputStream getInputStream() throws IOException {
            return this.encoded.getInputStream();
        }

        public ReadableByteChannel readableChannel() throws IOException {
            return this.encoded.readableChannel();
        }

        public long contentLength() throws IOException {
            return this.encoded.contentLength();
        }

        public long lastModified() throws IOException {
            return this.encoded.lastModified();
        }

        public Resource createRelative(String relativePath) throws IOException {
            return this.encoded.createRelative(relativePath);
        }

        @Nullable
        public String getFilename() {
            return this.original.getFilename();
        }

        public String getDescription() {
            return this.encoded.getDescription();
        }

        @Override
        public HttpHeaders getResponseHeaders() {
            HttpHeaders headers = this.original instanceof HttpResource ? ((HttpResource)this.original).getResponseHeaders() : new HttpHeaders();
            headers.add("Content-Encoding", this.coding);
            headers.add("Vary", "Accept-Encoding");
            return headers;
        }
    }
}

