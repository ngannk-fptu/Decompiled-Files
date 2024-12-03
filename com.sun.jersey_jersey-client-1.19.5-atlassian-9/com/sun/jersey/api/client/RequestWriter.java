/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.MessageBodyWorkers
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.GenericEntity
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyWriter
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class RequestWriter {
    private static final Logger LOGGER = Logger.getLogger(RequestWriter.class.getName());
    protected static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
    private MessageBodyWorkers workers;

    public RequestWriter() {
    }

    public RequestWriter(MessageBodyWorkers workers) {
        this.workers = workers;
    }

    @Context
    public void setMessageBodyWorkers(MessageBodyWorkers workers) {
        this.workers = workers;
    }

    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.workers;
    }

    protected RequestEntityWriter getRequestEntityWriter(ClientRequest ro) {
        return new RequestEntityWriterImpl(ro);
    }

    protected void writeRequestEntity(ClientRequest ro, RequestEntityWriterListener listener) throws IOException {
        Object entity = ro.getEntity();
        if (entity == null) {
            return;
        }
        Type entityType = null;
        if (entity instanceof GenericEntity) {
            GenericEntity ge = (GenericEntity)entity;
            entityType = ge.getType();
            entity = ge.getEntity();
        } else {
            entityType = entity.getClass();
        }
        Class<?> entityClass = entity.getClass();
        MultivaluedMap<String, Object> headers = ro.getHeaders();
        MediaType mediaType = this.getMediaType(entityClass, entityType, headers);
        MessageBodyWriter bw = this.workers.getMessageBodyWriter(entityClass, entityType, EMPTY_ANNOTATIONS, mediaType);
        if (bw == null) {
            throw new ClientHandlerException("A message body writer for Java type, " + entity.getClass() + ", and MIME media type, " + mediaType + ", was not found");
        }
        long size = headers.containsKey((Object)"Content-Encoding") ? -1L : bw.getSize(entity, entityClass, entityType, EMPTY_ANNOTATIONS, mediaType);
        listener.onRequestEntitySize(size);
        OutputStream out = ro.getAdapter().adapt(ro, listener.onGetOutputStream());
        try {
            bw.writeTo(entity, entityClass, entityType, EMPTY_ANNOTATIONS, mediaType, headers, out);
            out.flush();
        }
        catch (IOException ex) {
            try {
                out.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw ex;
        }
        catch (RuntimeException ex) {
            try {
                out.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            throw ex;
        }
        out.close();
    }

    private MediaType getMediaType(Class entityClass, Type entityType, MultivaluedMap<String, Object> headers) {
        Object mediaTypeHeader = headers.getFirst((Object)"Content-Type");
        if (mediaTypeHeader instanceof MediaType) {
            return (MediaType)mediaTypeHeader;
        }
        if (mediaTypeHeader != null) {
            return MediaType.valueOf((String)mediaTypeHeader.toString());
        }
        List mediaTypes = this.workers.getMessageBodyWriterMediaTypes(entityClass, entityType, EMPTY_ANNOTATIONS);
        MediaType mediaType = this.getMediaType(mediaTypes);
        headers.putSingle((Object)"Content-Type", (Object)mediaType);
        return mediaType;
    }

    private MediaType getMediaType(List<MediaType> mediaTypes) {
        if (mediaTypes.isEmpty()) {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        MediaType mediaType = mediaTypes.get(0);
        if (mediaType.isWildcardType() || mediaType.isWildcardSubtype()) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        return mediaType;
    }

    private final class RequestEntityWriterImpl
    implements RequestEntityWriter {
        private final ClientRequest cr;
        private final Object entity;
        private final Type entityType;
        private MediaType mediaType;
        private final long size;
        private final MessageBodyWriter bw;

        public RequestEntityWriterImpl(ClientRequest cr) {
            this.cr = cr;
            Object e = cr.getEntity();
            if (e == null) {
                throw new IllegalArgumentException("The entity of the client request is null");
            }
            if (e instanceof GenericEntity) {
                GenericEntity ge = (GenericEntity)e;
                this.entity = ge.getEntity();
                this.entityType = ge.getType();
            } else {
                this.entity = e;
                this.entityType = this.entity.getClass();
            }
            Class<?> entityClass = this.entity.getClass();
            MultivaluedMap<String, Object> headers = cr.getHeaders();
            this.mediaType = RequestWriter.this.getMediaType(entityClass, this.entityType, (MultivaluedMap<String, Object>)headers);
            this.bw = RequestWriter.this.workers.getMessageBodyWriter(entityClass, this.entityType, EMPTY_ANNOTATIONS, this.mediaType);
            if (this.bw == null) {
                String message = "A message body writer for Java class " + this.entity.getClass().getName() + ", and Java type " + this.entityType + ", and MIME media type " + this.mediaType + " was not found";
                LOGGER.severe(message);
                Map m = RequestWriter.this.workers.getWriters(this.mediaType);
                LOGGER.severe("The registered message body writers compatible with the MIME media type are:\n" + RequestWriter.this.workers.writersToString(m));
                throw new ClientHandlerException(message);
            }
            this.size = headers.containsKey((Object)"Content-Encoding") ? -1L : this.bw.getSize(this.entity, entityClass, this.entityType, EMPTY_ANNOTATIONS, this.mediaType);
        }

        @Override
        public long getSize() {
            return this.size;
        }

        @Override
        public MediaType getMediaType() {
            return this.mediaType;
        }

        @Override
        public void writeRequestEntity(OutputStream out) throws IOException {
            out = this.cr.getAdapter().adapt(this.cr, out);
            try {
                this.bw.writeTo(this.entity, this.entity.getClass(), this.entityType, EMPTY_ANNOTATIONS, this.mediaType, this.cr.getMetadata(), out);
                out.flush();
            }
            finally {
                out.close();
            }
        }
    }

    protected static interface RequestEntityWriter {
        public long getSize();

        public MediaType getMediaType();

        public void writeRequestEntity(OutputStream var1) throws IOException;
    }

    protected static interface RequestEntityWriterListener {
        public void onRequestEntitySize(long var1) throws IOException;

        public OutputStream onGetOutputStream() throws IOException;
    }
}

