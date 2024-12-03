/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.core.util.ReaderWriter
 *  javax.annotation.Nullable
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyWriter
 *  javax.ws.rs.ext.Provider
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.plugin.rest.entity.ErrorEntity;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.ArrayUtils;

@Provider
@Produces(value={"text/plain; charset=utf-8", "text/csv"})
public class ErrorEntityMessageBodyWriter
implements MessageBodyWriter<ErrorEntity> {
    private static final MediaType textPlainUtf8Type = new MediaType("text", "plain", (Map)ImmutableMap.of((Object)"charset", (Object)"utf-8"));

    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return type == ErrorEntity.class && (textPlainUtf8Type.equals((Object)mediaType) || this.isTextCsv(mediaType)) && aClass == ErrorEntity.class;
    }

    public long getSize(@Nullable ErrorEntity errorEntity, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return Optional.ofNullable(errorEntity).map(ErrorEntity::getMessage).map(message -> this.toMessageBytes((String)message, mediaType)).map(bytes -> ((byte[])bytes).length).orElse(0).intValue();
    }

    public void writeTo(@Nullable ErrorEntity errorEntity, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream entityStream) throws IOException, WebApplicationException {
        if (errorEntity != null && errorEntity.getMessage() != null) {
            entityStream.write(this.toMessageBytes(errorEntity.getMessage(), mediaType));
        }
    }

    private byte[] toMessageBytes(String message, MediaType mediaType) {
        if (this.isTextCsv(mediaType)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return message.getBytes(ReaderWriter.getCharset((MediaType)mediaType));
    }

    private boolean isTextCsv(MediaType mediaType) {
        return mediaType != null && "text".equals(mediaType.getType()) && "csv".equals(mediaType.getSubtype());
    }
}

