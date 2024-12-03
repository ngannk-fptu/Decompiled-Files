/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider
 *  com.sun.jersey.spi.MessageBodyWorkers
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.GenericEntity
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyWriter
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.json.impl.ImplMessages;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class JSONWithPaddingProvider
extends AbstractMessageReaderWriterProvider<JSONWithPadding> {
    private static final Logger LOGGER = Logger.getLogger(JSONWithPaddingProvider.class.getName());
    private final Map<String, Set<String>> javascriptTypes = new HashMap<String, Set<String>>();
    @Context
    MessageBodyWorkers bodyWorker;

    public JSONWithPaddingProvider() {
        this.javascriptTypes.put("application", new HashSet<String>(Arrays.asList("x-javascript", "ecmascript", "javascript")));
        this.javascriptTypes.put("text", new HashSet<String>(Arrays.asList("ecmascript", "jscript")));
    }

    private boolean isJavascript(MediaType m) {
        Set<String> subtypes = this.javascriptTypes.get(m.getType());
        if (subtypes == null) {
            return false;
        }
        return subtypes.contains(m.getSubtype());
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return false;
    }

    public JSONWithPadding readFrom(Class<JSONWithPadding> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        throw new UnsupportedOperationException("Not supported by design.");
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == JSONWithPadding.class;
    }

    public void writeTo(JSONWithPadding t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        boolean isJavaScript;
        MediaType workerMediaType;
        MessageBodyWriter bw;
        Object jsonEntity = t.getJsonSource();
        Type entityGenericType = jsonEntity.getClass();
        Class entityType = jsonEntity.getClass();
        boolean genericEntityUsed = jsonEntity instanceof GenericEntity;
        if (genericEntityUsed) {
            GenericEntity ge = (GenericEntity)jsonEntity;
            jsonEntity = ge.getEntity();
            entityGenericType = ge.getType();
            entityType = ge.getRawType();
        }
        if ((bw = this.bodyWorker.getMessageBodyWriter(entityType, entityGenericType, annotations, workerMediaType = (isJavaScript = this.isJavascript(mediaType)) ? MediaType.APPLICATION_JSON_TYPE : mediaType)) == null) {
            if (!genericEntityUsed) {
                LOGGER.severe(ImplMessages.ERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(jsonEntity, workerMediaType));
            } else {
                LOGGER.severe(ImplMessages.ERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(jsonEntity, workerMediaType));
            }
            throw new WebApplicationException(500);
        }
        if (isJavaScript) {
            entityStream.write(t.getCallbackName().getBytes());
            entityStream.write(40);
        }
        bw.writeTo(jsonEntity, entityType, entityGenericType, annotations, workerMediaType, httpHeaders, entityStream);
        if (isJavaScript) {
            entityStream.write(41);
        }
    }
}

