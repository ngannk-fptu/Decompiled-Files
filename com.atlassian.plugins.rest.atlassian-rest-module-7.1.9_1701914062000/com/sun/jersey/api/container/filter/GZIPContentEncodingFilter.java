/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.core.EntityTag;

public class GZIPContentEncodingFilter
implements ContainerRequestFilter,
ContainerResponseFilter {
    private static final String ENTITY_TAG_GZIP_SUFFIX_VALUE = "-gzip";
    private static final String ENTITY_TAG_GZIP_SUFFIX_HEADER_VALUE = "-gzip\"";

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String contentEncoding = request.getRequestHeaders().getFirst("Content-Encoding");
        if (contentEncoding != null && contentEncoding.trim().equals("gzip")) {
            request.getRequestHeaders().remove("Content-Encoding");
            try {
                request.setEntityInputStream(new GZIPInputStream(request.getEntityInputStream()));
            }
            catch (IOException ex) {
                throw new ContainerException(ex);
            }
        }
        String acceptEncoding = request.getRequestHeaders().getFirst("Accept-Encoding");
        String entityTag = request.getRequestHeaders().getFirst("If-None-Match");
        if (acceptEncoding != null && acceptEncoding.contains("gzip") && entityTag != null) {
            if (entityTag.endsWith(ENTITY_TAG_GZIP_SUFFIX_HEADER_VALUE)) {
                int gzipsuffixbeginIndex = entityTag.lastIndexOf(ENTITY_TAG_GZIP_SUFFIX_HEADER_VALUE);
                StringBuilder sb = new StringBuilder();
                sb.append(entityTag.substring(0, gzipsuffixbeginIndex));
                sb.append('\"');
                request.getRequestHeaders().putSingle("If-None-Match", sb.toString());
            } else {
                request.getRequestHeaders().remove("If-None-Match");
            }
        }
        return request;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        response.getHttpHeaders().add("Vary", "Accept-Encoding");
        String acceptEncoding = request.getRequestHeaders().getFirst("Accept-Encoding");
        String contentEncoding = (String)response.getHttpHeaders().getFirst("Content-Encoding");
        if (acceptEncoding != null && contentEncoding == null && acceptEncoding.contains("gzip")) {
            EntityTag entityTag;
            if (response.getHttpHeaders().containsKey("ETag") && (entityTag = (EntityTag)response.getHttpHeaders().getFirst("ETag")) != null) {
                response.getHttpHeaders().putSingle("ETag", new EntityTag(entityTag.getValue() + ENTITY_TAG_GZIP_SUFFIX_VALUE, entityTag.isWeak()));
            }
            if (response.getEntity() != null) {
                response.getHttpHeaders().add("Content-Encoding", "gzip");
                response.setContainerResponseWriter(new Adapter(response.getContainerResponseWriter()));
            }
        }
        return response;
    }

    private static final class Adapter
    implements ContainerResponseWriter {
        private final ContainerResponseWriter crw;
        private GZIPOutputStream gos;

        Adapter(ContainerResponseWriter crw) {
            this.crw = crw;
        }

        @Override
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse response) throws IOException {
            this.gos = new GZIPOutputStream(this.crw.writeStatusAndHeaders(-1L, response));
            return this.gos;
        }

        @Override
        public void finish() throws IOException {
            this.gos.finish();
            this.crw.finish();
        }
    }
}

