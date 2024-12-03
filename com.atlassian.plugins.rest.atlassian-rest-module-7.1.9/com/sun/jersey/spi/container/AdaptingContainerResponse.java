/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class AdaptingContainerResponse
extends ContainerResponse {
    protected final ContainerResponse acr;

    protected AdaptingContainerResponse(ContainerResponse acr) {
        super(acr);
        this.acr = acr;
    }

    @Override
    public void write() throws IOException {
        this.acr.write();
    }

    @Override
    public void reset() {
        this.acr.reset();
    }

    @Override
    public ContainerRequest getContainerRequest() {
        return this.acr.getContainerRequest();
    }

    @Override
    public void setContainerRequest(ContainerRequest request) {
        this.acr.setContainerRequest(request);
    }

    @Override
    public ContainerResponseWriter getContainerResponseWriter() {
        return this.acr.getContainerResponseWriter();
    }

    @Override
    public void setContainerResponseWriter(ContainerResponseWriter responseWriter) {
        this.acr.setContainerResponseWriter(responseWriter);
    }

    @Override
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.acr.getMessageBodyWorkers();
    }

    @Override
    public void mapMappableContainerException(MappableContainerException e) {
        this.acr.mapMappableContainerException(e);
    }

    @Override
    public void mapWebApplicationException(WebApplicationException e) {
        this.acr.mapWebApplicationException(e);
    }

    @Override
    public boolean mapException(Throwable e) {
        return this.acr.mapException(e);
    }

    @Override
    public Response getResponse() {
        return this.acr.getResponse();
    }

    @Override
    public void setResponse(Response response) {
        this.acr.setResponse(response);
    }

    @Override
    public boolean isResponseSet() {
        return this.acr.isResponseSet();
    }

    @Override
    public Throwable getMappedThrowable() {
        return this.acr.getMappedThrowable();
    }

    @Override
    public Response.StatusType getStatusType() {
        return this.acr.getStatusType();
    }

    @Override
    public void setStatusType(Response.StatusType statusType) {
        this.acr.setStatusType(statusType);
    }

    @Override
    public int getStatus() {
        return this.acr.getStatus();
    }

    @Override
    public void setStatus(int status) {
        this.acr.setStatus(status);
    }

    @Override
    public Object getEntity() {
        return this.acr.getEntity();
    }

    @Override
    public Type getEntityType() {
        return this.acr.getEntityType();
    }

    @Override
    public Object getOriginalEntity() {
        return this.acr.getOriginalEntity();
    }

    @Override
    public void setEntity(Object entity) {
        this.acr.setEntity(entity);
    }

    @Override
    public void setEntity(Object entity, Type entityType) {
        this.acr.setEntity(entity, entityType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.acr.getAnnotations();
    }

    @Override
    public void setAnnotations(Annotation[] annotations) {
        this.acr.setAnnotations(annotations);
    }

    @Override
    public MultivaluedMap<String, Object> getHttpHeaders() {
        return this.acr.getHttpHeaders();
    }

    @Override
    public MediaType getMediaType() {
        return this.acr.getMediaType();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.acr.getOutputStream();
    }

    @Override
    public boolean isCommitted() {
        return this.acr.isCommitted();
    }
}

