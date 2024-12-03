/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.provider;

import com.sun.jersey.core.util.ReaderWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

public abstract class AbstractMessageReaderWriterProvider<T>
implements MessageBodyReader<T>,
MessageBodyWriter<T> {
    public static final Charset UTF8 = ReaderWriter.UTF8;

    public static final void writeTo(InputStream in, OutputStream out) throws IOException {
        ReaderWriter.writeTo(in, out);
    }

    public static final void writeTo(Reader in, Writer out) throws IOException {
        ReaderWriter.writeTo(in, out);
    }

    public static final Charset getCharset(MediaType m) {
        return ReaderWriter.getCharset(m);
    }

    public static final String readFromAsString(InputStream in, MediaType type) throws IOException {
        return ReaderWriter.readFromAsString(in, type);
    }

    public static final void writeToAsString(String s, OutputStream out, MediaType type) throws IOException {
        ReaderWriter.writeToAsString(s, out, type);
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }
}

