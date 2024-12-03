/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.MessageException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.TraceInformation;
import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.factory.ResponseImpl;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;

public class ContainerResponse
implements HttpResponseContext {
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
    private static final Logger LOGGER = Logger.getLogger(ContainerResponse.class.getName());
    private static final RuntimeDelegate rd = RuntimeDelegate.getInstance();
    private final WebApplication wa;
    private ContainerRequest request;
    private ContainerResponseWriter responseWriter;
    private Response response;
    private Throwable mappedThrowable;
    private Response.StatusType statusType;
    private MultivaluedMap<String, Object> headers;
    private Object originalEntity;
    private Object entity;
    private Type entityType;
    private boolean isCommitted;
    private CommittingOutputStream out;
    private Annotation[] annotations = EMPTY_ANNOTATIONS;

    public ContainerResponse(WebApplication wa, ContainerRequest request, ContainerResponseWriter responseWriter) {
        this.wa = wa;
        this.request = request;
        this.responseWriter = responseWriter;
        this.statusType = Response.Status.NO_CONTENT;
    }

    ContainerResponse(ContainerResponse acr) {
        this.wa = acr.wa;
    }

    public static String getHeaderValue(Object headerValue) {
        RuntimeDelegate.HeaderDelegate<?> hp = rd.createHeaderDelegate(headerValue.getClass());
        return hp != null ? hp.toString(headerValue) : headerValue.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void write() throws IOException {
        MessageBodyWriter<?> writer;
        MediaType contentType;
        String varyHeader;
        if (this.isCommitted) {
            return;
        }
        if (this.request.isTracingEnabled()) {
            this.configureTrace(this.responseWriter);
        }
        if (this.entity == null) {
            this.isCommitted = true;
            this.responseWriter.writeStatusAndHeaders(-1L, this);
            this.responseWriter.finish();
            return;
        }
        if (!this.getHttpHeaders().containsKey("Vary") && (varyHeader = (String)this.request.getProperties().get("Vary")) != null) {
            this.getHttpHeaders().add("Vary", varyHeader);
        }
        if ((contentType = this.getMediaType()) == null) {
            contentType = this.getMessageBodyWorkers().getMessageBodyWriterMediaType(this.entity.getClass(), this.entityType, this.annotations, this.request.getAcceptableMediaTypes());
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
            }
            this.getHttpHeaders().putSingle("Content-Type", contentType);
        }
        if ((writer = this.getMessageBodyWorkers().getMessageBodyWriter(this.entity.getClass(), this.entityType, this.annotations, contentType)) == null) {
            String message = "A message body writer for Java class " + this.entity.getClass().getName() + ", and Java type " + this.entityType + ", and MIME media type " + contentType + " was not found.\n";
            Map<MediaType, List<MessageBodyWriter>> m = this.getMessageBodyWorkers().getWriters(contentType);
            LOGGER.severe(message + "The registered message body writers compatible with the MIME media type are:\n" + this.getMessageBodyWorkers().writersToString(m));
            if (!this.request.getMethod().equals("HEAD")) throw new WebApplicationException((Throwable)new MessageException(message), 500);
            this.writeHttpHead(-1L);
        } else {
            long size = writer.getSize(this.entity, this.entity.getClass(), this.entityType, this.annotations, contentType);
            if (this.request.getMethod().equals("HEAD")) {
                this.writeHttpHead(size);
            } else {
                if (this.request.isTracingEnabled()) {
                    this.request.trace(String.format("matched message body writer: %s, \"%s\" -> %s", ReflectionHelper.objectToString(this.entity), contentType, ReflectionHelper.objectToString(writer)));
                }
                if (this.out == null) {
                    this.out = new CommittingOutputStream(size);
                }
                writer.writeTo(this.entity, this.entity.getClass(), this.entityType, this.annotations, contentType, this.getHttpHeaders(), this.out);
                if (!this.isCommitted) {
                    this.isCommitted = true;
                    this.responseWriter.writeStatusAndHeaders(-1L, this);
                }
            }
        }
        this.responseWriter.finish();
    }

    private void writeHttpHead(long size) throws IOException {
        if (size != -1L) {
            this.getHttpHeaders().putSingle("Content-Length", Long.toString(size));
        }
        this.isCommitted = true;
        this.responseWriter.writeStatusAndHeaders(size, this);
        if (this.entity instanceof InputStream) {
            ((InputStream)this.entity).close();
        }
    }

    private void configureTrace(final ContainerResponseWriter crw) {
        final TraceInformation ti = (TraceInformation)this.request.getProperties().get(TraceInformation.class.getName());
        this.setContainerResponseWriter(new ContainerResponseWriter(){

            @Override
            public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse response) throws IOException {
                ti.addTraceHeaders();
                return crw.writeStatusAndHeaders(contentLength, response);
            }

            @Override
            public void finish() throws IOException {
                crw.finish();
            }
        });
    }

    public void reset() {
        this.setResponse(Responses.noContent().build());
    }

    public ContainerRequest getContainerRequest() {
        return this.request;
    }

    public void setContainerRequest(ContainerRequest request) {
        this.request = request;
    }

    public ContainerResponseWriter getContainerResponseWriter() {
        return this.responseWriter;
    }

    public void setContainerResponseWriter(ContainerResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.wa.getMessageBodyWorkers();
    }

    public void mapMappableContainerException(MappableContainerException e) {
        Throwable cause = e.getCause();
        if (cause instanceof WebApplicationException) {
            this.mapWebApplicationException((WebApplicationException)cause);
        } else if (!this.mapException(cause)) {
            if (cause instanceof RuntimeException) {
                LOGGER.log(Level.SEVERE, "The RuntimeException could not be mapped to a response, re-throwing to the HTTP container", cause);
                throw (RuntimeException)cause;
            }
            LOGGER.log(Level.SEVERE, "The exception contained within MappableContainerException could not be mapped to a response, re-throwing to the HTTP container", cause);
            throw e;
        }
    }

    public void mapWebApplicationException(WebApplicationException e) {
        if (e.getResponse().getEntity() != null) {
            this.wa.getResponseListener().onError(Thread.currentThread().getId(), e);
            this.onException(e, e.getResponse(), false);
        } else if (!this.mapException(e)) {
            this.onException(e, e.getResponse(), false);
        }
    }

    public boolean mapException(Throwable e) {
        ExceptionMapper em = this.wa.getExceptionMapperContext().find(e.getClass());
        if (em == null) {
            this.wa.getResponseListener().onError(Thread.currentThread().getId(), e);
            return false;
        }
        this.wa.getResponseListener().onMappedException(Thread.currentThread().getId(), e, em);
        if (this.request.isTracingEnabled()) {
            this.request.trace(String.format("matched exception mapper: %s -> %s", ReflectionHelper.objectToString(e), ReflectionHelper.objectToString(em)));
        }
        try {
            Response r = em.toResponse(e);
            if (r == null) {
                r = Response.noContent().build();
            }
            this.onException(e, r, true);
        }
        catch (MappableContainerException ex) {
            throw ex;
        }
        catch (RuntimeException ex) {
            LOGGER.severe("Exception mapper " + em + " for Throwable " + e + " threw a RuntimeException when attempting to obtain the response");
            Response r = Response.serverError().build();
            this.onException(ex, r, false);
        }
        return true;
    }

    private void onException(Throwable e, Response r, boolean mapped) {
        Object m;
        if (this.request.isTracingEnabled()) {
            Response.Status s = Response.Status.fromStatusCode(r.getStatus());
            if (s != null) {
                this.request.trace(String.format("mapped exception to response: %s -> %d (%s)", ReflectionHelper.objectToString(e), r.getStatus(), s.getReasonPhrase()));
            } else {
                this.request.trace(String.format("mapped exception to response: %s -> %d", ReflectionHelper.objectToString(e), r.getStatus()));
            }
        }
        if (!mapped && r.getStatus() >= 500) {
            this.logException(e, r, Level.SEVERE);
        } else if (LOGGER.isLoggable(Level.FINE)) {
            this.logException(e, r, Level.FINE);
        }
        this.setResponse(r);
        this.mappedThrowable = e;
        if (this.getEntity() != null && this.getHttpHeaders().getFirst("Content-Type") == null && (m = this.request.getProperties().get("com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type")) != null) {
            this.request.getProperties().remove("com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type");
            this.getHttpHeaders().putSingle("Content-Type", m);
        }
    }

    private void logException(Throwable e, Response r, Level l) {
        Response.Status s = Response.Status.fromStatusCode(r.getStatus());
        if (s != null) {
            LOGGER.log(l, "Mapped exception to response: " + r.getStatus() + " (" + s.getReasonPhrase() + ")", e);
        } else {
            LOGGER.log(l, "Mapped exception to response: " + r.getStatus(), e);
        }
    }

    @Override
    public Response getResponse() {
        if (this.response == null) {
            this.setResponse(null);
        }
        return this.response;
    }

    @Override
    public void setResponse(Response response) {
        this.isCommitted = false;
        this.out = null;
        response = response != null ? response : Responses.noContent().build();
        this.response = response;
        this.mappedThrowable = null;
        if (response instanceof ResponseImpl) {
            ResponseImpl responseImpl = (ResponseImpl)response;
            this.setStatusType(responseImpl.getStatusType());
            this.setHeaders(response.getMetadata());
            this.setEntity(responseImpl.getEntity(), responseImpl.getEntityType());
        } else {
            this.setStatus(response.getStatus());
            this.setHeaders(response.getMetadata());
            this.setEntity(response.getEntity());
        }
    }

    @Override
    public boolean isResponseSet() {
        return this.response != null;
    }

    @Override
    public Throwable getMappedThrowable() {
        return this.mappedThrowable;
    }

    @Override
    public Response.StatusType getStatusType() {
        return this.statusType;
    }

    @Override
    public void setStatusType(Response.StatusType statusType) {
        this.statusType = statusType;
    }

    @Override
    public int getStatus() {
        return this.statusType.getStatusCode();
    }

    @Override
    public void setStatus(int status) {
        this.statusType = ResponseImpl.toStatusType(status);
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    @Override
    public Type getEntityType() {
        return this.entityType;
    }

    @Override
    public Object getOriginalEntity() {
        return this.originalEntity;
    }

    @Override
    public void setEntity(Object entity) {
        this.setEntity(entity, entity == null ? null : entity.getClass());
    }

    public void setEntity(Object entity, Type entityType) {
        this.originalEntity = this.entity = entity;
        this.entityType = entityType;
        if (this.entity instanceof GenericEntity) {
            GenericEntity ge = (GenericEntity)this.entity;
            this.entity = ge.getEntity();
            this.entityType = ge.getType();
        }
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations;
    }

    @Override
    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations != null ? annotations : EMPTY_ANNOTATIONS;
    }

    @Override
    public MultivaluedMap<String, Object> getHttpHeaders() {
        if (this.headers == null) {
            this.headers = new OutBoundHeaders();
        }
        return this.headers;
    }

    @Override
    public MediaType getMediaType() {
        Object mediaTypeHeader = this.getHttpHeaders().getFirst("Content-Type");
        if (mediaTypeHeader instanceof MediaType) {
            return (MediaType)mediaTypeHeader;
        }
        if (mediaTypeHeader != null) {
            return MediaType.valueOf(mediaTypeHeader.toString());
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.out == null) {
            this.out = new CommittingOutputStream(-1L);
        }
        return this.out;
    }

    @Override
    public boolean isCommitted() {
        return this.isCommitted;
    }

    private void setHeaders(MultivaluedMap<String, Object> headers) {
        this.headers = headers;
        Object location = headers.getFirst("Location");
        if (location != null && location instanceof URI) {
            URI locationUri = (URI)location;
            if (!locationUri.isAbsolute()) {
                URI base = this.statusType.getStatusCode() == Response.Status.CREATED.getStatusCode() ? this.request.getAbsolutePath() : this.request.getBaseUri();
                location = UriBuilder.fromUri(base).path(locationUri.getRawPath()).replaceQuery(locationUri.getRawQuery()).fragment(locationUri.getRawFragment()).build(new Object[0]);
            }
            headers.putSingle("Location", location);
        }
    }

    private final class CommittingOutputStream
    extends OutputStream {
        private final long size;
        private OutputStream o;

        CommittingOutputStream(long size) {
            this.size = size;
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.commitWrite();
            this.o.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.commitWrite();
            this.o.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            this.commitWrite();
            this.o.write(b);
        }

        @Override
        public void flush() throws IOException {
            this.commitWrite();
            this.o.flush();
        }

        @Override
        public void close() throws IOException {
            this.commitClose();
            this.o.close();
        }

        private void commitWrite() throws IOException {
            if (!ContainerResponse.this.isCommitted) {
                if (ContainerResponse.this.getStatus() == 204) {
                    ContainerResponse.this.setStatus(200);
                }
                ContainerResponse.this.isCommitted = true;
                this.o = ContainerResponse.this.responseWriter.writeStatusAndHeaders(this.size, ContainerResponse.this);
            }
        }

        private void commitClose() throws IOException {
            if (!ContainerResponse.this.isCommitted) {
                ContainerResponse.this.isCommitted = true;
                this.o = ContainerResponse.this.responseWriter.writeStatusAndHeaders(-1L, ContainerResponse.this);
            }
        }
    }
}

