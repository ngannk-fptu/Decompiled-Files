/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Produces(value={"application/octet-stream", "*/*"})
@Consumes(value={"application/octet-stream", "*/*"})
public class DataSourceProvider
extends AbstractMessageReaderWriterProvider<DataSource> {
    public DataSourceProvider() {
        Class<DataSource> c = DataSource.class;
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return DataSource.class == type;
    }

    public DataSource readFrom(Class<DataSource> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        ByteArrayDataSource ds = new ByteArrayDataSource(entityStream, mediaType == null ? null : mediaType.toString());
        return ds;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return DataSource.class.isAssignableFrom(type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeTo(DataSource t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try (InputStream in = t.getInputStream();){
            DataSourceProvider.writeTo(in, entityStream);
        }
    }

    public static class ByteArrayDataSource
    implements DataSource {
        private byte[] data;
        private int len = -1;
        private String type;
        private String name = "";

        public ByteArrayDataSource(InputStream is, String type) throws IOException {
            DSByteArrayOutputStream os = new DSByteArrayOutputStream();
            ReaderWriter.writeTo(is, os);
            this.data = os.getBuf();
            this.len = os.getCount();
            if (this.data.length - this.len > 262144) {
                this.data = os.toByteArray();
                this.len = this.data.length;
            }
            this.type = type;
        }

        public InputStream getInputStream() throws IOException {
            if (this.data == null) {
                throw new IOException("no data");
            }
            if (this.len < 0) {
                this.len = this.data.length;
            }
            return new ByteArrayInputStream(this.data, 0, this.len);
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("cannot do this");
        }

        public String getContentType() {
            return this.type;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        static class DSByteArrayOutputStream
        extends ByteArrayOutputStream {
            DSByteArrayOutputStream() {
            }

            public byte[] getBuf() {
                return this.buf;
            }

            public int getCount() {
                return this.count;
            }
        }
    }
}

