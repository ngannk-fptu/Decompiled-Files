/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeMultipart
 *  javax.mail.internet.ParseException
 *  javax.mail.util.ByteArrayDataSource
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public final class MimeMultipartProvider
extends AbstractMessageReaderWriterProvider<MimeMultipart> {
    public MimeMultipartProvider() {
        Class<MimeMultipart> c = MimeMultipart.class;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MimeMultipart.class;
    }

    @Override
    public MimeMultipart readFrom(Class<MimeMultipart> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        if (mediaType == null) {
            mediaType = new MediaType("multipart", "form-data");
        }
        ByteArrayDataSource ds = new ByteArrayDataSource(entityStream, mediaType.toString());
        try {
            return new MimeMultipart((DataSource)ds);
        }
        catch (ParseException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (MessagingException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MimeMultipart.class;
    }

    @Override
    public void writeTo(MimeMultipart t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            t.writeTo(entityStream);
        }
        catch (MessagingException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

