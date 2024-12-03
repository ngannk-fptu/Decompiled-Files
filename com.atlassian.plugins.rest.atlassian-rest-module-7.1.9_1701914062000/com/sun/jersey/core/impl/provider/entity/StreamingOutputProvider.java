/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyWriter;

@Produces(value={"application/octet-stream", "*/*"})
public final class StreamingOutputProvider
implements MessageBodyWriter<StreamingOutput> {
    @Override
    public boolean isWriteable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
        return StreamingOutput.class.isAssignableFrom(t);
    }

    @Override
    public long getSize(StreamingOutput o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public void writeTo(StreamingOutput o, Class<?> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entity) throws IOException {
        o.write(entity);
    }
}

