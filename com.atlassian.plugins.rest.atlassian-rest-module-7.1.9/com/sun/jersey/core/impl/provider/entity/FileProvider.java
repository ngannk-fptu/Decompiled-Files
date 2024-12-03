/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public final class FileProvider
extends AbstractMessageReaderWriterProvider<File> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return File.class == type;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public File readFrom(Class<File> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        File f = File.createTempFile("rep", "tmp");
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));){
            FileProvider.writeTo(entityStream, out);
        }
        return f;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return File.class.isAssignableFrom(type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(File t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(t), ReaderWriter.BUFFER_SIZE);){
            FileProvider.writeTo(in, entityStream);
        }
    }

    @Override
    public long getSize(File t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return t.length();
    }
}

