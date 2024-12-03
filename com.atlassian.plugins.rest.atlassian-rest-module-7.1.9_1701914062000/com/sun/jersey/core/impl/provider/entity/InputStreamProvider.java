/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Produces(value={"application/octet-stream", "*/*"})
@Consumes(value={"application/octet-stream", "*/*"})
public final class InputStreamProvider
extends AbstractMessageReaderWriterProvider<InputStream> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return InputStream.class == type;
    }

    @Override
    public InputStream readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        return entityStream;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return InputStream.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(InputStream t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (t instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream)t).available();
        }
        return -1L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(InputStream t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            InputStreamProvider.writeTo(t, entityStream);
        }
        finally {
            t.close();
        }
    }
}

