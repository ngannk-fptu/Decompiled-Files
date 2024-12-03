/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container.filter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

public class LoggingFilter
implements ContainerRequestFilter,
ContainerResponseFilter {
    public static final String FEATURE_LOGGING_DISABLE_ENTITY = "com.sun.jersey.config.feature.logging.DisableEntitylogging";
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private final Logger logger;
    @Context
    private HttpContext hc;
    @Context
    private ResourceConfig rc;
    private long id = 0L;

    public LoggingFilter() {
        this(LOGGER);
    }

    public LoggingFilter(Logger logger2) {
        this.logger = logger2;
    }

    private synchronized void setId() {
        if (this.hc.getProperties().get("request-id") == null) {
            this.hc.getProperties().put("request-id", Long.toString(++this.id));
        }
    }

    private StringBuilder prefixId(StringBuilder b) {
        b.append(this.hc.getProperties().get("request-id").toString()).append(" ");
        return b;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        this.setId();
        StringBuilder b = new StringBuilder();
        this.printRequestLine(b, request);
        this.printRequestHeaders(b, request.getRequestHeaders());
        if (this.rc.getFeature(FEATURE_LOGGING_DISABLE_ENTITY)) {
            this.logger.info(b.toString());
            return request;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = request.getEntityInputStream();
        try {
            Object requestEntity;
            if (in.available() > 0) {
                ReaderWriter.writeTo(in, out);
                requestEntity = out.toByteArray();
                this.printEntity(b, (byte[])requestEntity);
                request.setEntityInputStream(new ByteArrayInputStream((byte[])requestEntity));
            }
            requestEntity = request;
            return requestEntity;
        }
        catch (IOException ex) {
            throw new ContainerException(ex);
        }
        finally {
            this.logger.info(b.toString());
        }
    }

    private void printRequestLine(StringBuilder b, ContainerRequest request) {
        this.prefixId(b).append(NOTIFICATION_PREFIX).append("Server in-bound request").append('\n');
        this.prefixId(b).append(REQUEST_PREFIX).append(request.getMethod()).append(" ").append(request.getRequestUri().toASCIIString()).append('\n');
    }

    private void printRequestHeaders(StringBuilder b, MultivaluedMap<String, String> headers) {
        for (Map.Entry e : headers.entrySet()) {
            String header = (String)e.getKey();
            for (String value : (List)e.getValue()) {
                this.prefixId(b).append(REQUEST_PREFIX).append(header).append(": ").append(value).append('\n');
            }
        }
        this.prefixId(b).append(REQUEST_PREFIX).append('\n');
    }

    private void printEntity(StringBuilder b, byte[] entity) throws IOException {
        if (entity.length == 0) {
            return;
        }
        b.append(new String(entity)).append("\n");
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        this.setId();
        response.setContainerResponseWriter(new Adapter(response.getContainerResponseWriter()));
        return response;
    }

    private void printResponseLine(StringBuilder b, ContainerResponse response) {
        this.prefixId(b).append(NOTIFICATION_PREFIX).append("Server out-bound response").append('\n');
        this.prefixId(b).append(RESPONSE_PREFIX).append(Integer.toString(response.getStatus())).append('\n');
    }

    private void printResponseHeaders(StringBuilder b, MultivaluedMap<String, Object> headers) {
        for (Map.Entry e : headers.entrySet()) {
            String header = (String)e.getKey();
            for (Object value : (List)e.getValue()) {
                this.prefixId(b).append(RESPONSE_PREFIX).append(header).append(": ").append(ContainerResponse.getHeaderValue(value)).append('\n');
            }
        }
        this.prefixId(b).append(RESPONSE_PREFIX).append('\n');
    }

    private final class Adapter
    implements ContainerResponseWriter {
        private final ContainerResponseWriter crw;
        private final boolean disableEntity;
        private long contentLength;
        private ContainerResponse response;
        private ByteArrayOutputStream baos;
        private StringBuilder b = new StringBuilder();

        Adapter(ContainerResponseWriter crw) {
            this.crw = crw;
            this.disableEntity = LoggingFilter.this.rc.getFeature(LoggingFilter.FEATURE_LOGGING_DISABLE_ENTITY);
        }

        @Override
        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse response) throws IOException {
            LoggingFilter.this.printResponseLine(this.b, response);
            LoggingFilter.this.printResponseHeaders(this.b, response.getHttpHeaders());
            if (this.disableEntity) {
                LoggingFilter.this.logger.info(this.b.toString());
                return this.crw.writeStatusAndHeaders(contentLength, response);
            }
            this.contentLength = contentLength;
            this.response = response;
            this.baos = new ByteArrayOutputStream();
            return this.baos;
        }

        @Override
        public void finish() throws IOException {
            if (!this.disableEntity) {
                byte[] entity = this.baos.toByteArray();
                LoggingFilter.this.printEntity(this.b, entity);
                LoggingFilter.this.logger.info(this.b.toString());
                OutputStream out = this.crw.writeStatusAndHeaders(this.contentLength, this.response);
                out.write(entity);
            }
            this.crw.finish();
        }
    }
}

