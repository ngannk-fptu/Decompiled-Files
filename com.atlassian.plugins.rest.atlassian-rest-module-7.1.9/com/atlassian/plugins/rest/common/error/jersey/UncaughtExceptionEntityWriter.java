/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.error.jersey;

import com.atlassian.plugins.rest.common.error.jersey.UncaughtExceptionEntity;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(value={"text/plain"})
public class UncaughtExceptionEntityWriter
implements MessageBodyWriter<UncaughtExceptionEntity> {
    @Override
    public long getSize(UncaughtExceptionEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return UncaughtExceptionEntity.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(UncaughtExceptionEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        String plainText = t.toString();
        entityStream.write(plainText.getBytes("utf-8"));
    }
}

