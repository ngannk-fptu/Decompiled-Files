/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.EntityHolder;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

public final class EntityHolderReader
implements MessageBodyReader<Object> {
    private static final Logger LOGGER = Logger.getLogger(EntityHolderReader.class.getName());
    private final MessageBodyWorkers bodyWorker;

    public EntityHolderReader(@Context MessageBodyWorkers bodyWorker) {
        this.bodyWorker = bodyWorker;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type != EntityHolder.class) {
            return false;
        }
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType pt = (ParameterizedType)genericType;
        Type t = pt.getActualTypeArguments()[0];
        return t instanceof Class || t instanceof ParameterizedType;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        if (!entityStream.markSupported()) {
            entityStream = new BufferedInputStream(entityStream, ReaderWriter.BUFFER_SIZE);
        }
        entityStream.mark(1);
        if (entityStream.read() == -1) {
            return new EntityHolder();
        }
        entityStream.reset();
        ParameterizedType pt = (ParameterizedType)genericType;
        Type t = pt.getActualTypeArguments()[0];
        Class entityClass = t instanceof Class ? (Class)t : (Class)((ParameterizedType)t).getRawType();
        Type entityGenericType = t instanceof Class ? entityClass : t;
        MessageBodyReader br = this.bodyWorker.getMessageBodyReader(entityClass, entityGenericType, annotations, mediaType);
        if (br == null) {
            LOGGER.severe("A message body reader for the type, " + type + ", could not be found");
            throw new WebApplicationException();
        }
        Object o = br.readFrom(entityClass, entityGenericType, annotations, mediaType, httpHeaders, entityStream);
        return new EntityHolder(o);
    }
}

