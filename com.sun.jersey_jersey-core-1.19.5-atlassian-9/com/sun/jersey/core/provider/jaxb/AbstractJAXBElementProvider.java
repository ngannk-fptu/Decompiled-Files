/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Providers
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.core.provider.jaxb;

import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

public abstract class AbstractJAXBElementProvider
extends AbstractJAXBProvider<JAXBElement<?>> {
    public AbstractJAXBElementProvider(Providers ps) {
        super(ps);
    }

    public AbstractJAXBElementProvider(Providers ps, MediaType mt) {
        super(ps, mt);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == JAXBElement.class && genericType instanceof ParameterizedType && this.isSupported(mediaType);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JAXBElement.class.isAssignableFrom(type) && this.isSupported(mediaType);
    }

    public final JAXBElement<?> readFrom(Class<JAXBElement<?>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        ParameterizedType pt = (ParameterizedType)genericType;
        Class ta = (Class)pt.getActualTypeArguments()[0];
        try {
            return this.readFrom(ta, mediaType, this.getUnmarshaller(ta, mediaType), entityStream);
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected abstract JAXBElement<?> readFrom(Class<?> var1, MediaType var2, Unmarshaller var3, InputStream var4) throws JAXBException;

    public final void writeTo(JAXBElement<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            Marshaller m = this.getMarshaller(t.getDeclaredType(), mediaType);
            Charset c = AbstractJAXBElementProvider.getCharset(mediaType);
            if (c != UTF8) {
                m.setProperty("jaxb.encoding", (Object)c.name());
            }
            this.setHeader(m, annotations);
            this.writeTo(t, mediaType, c, m, entityStream);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected abstract void writeTo(JAXBElement<?> var1, MediaType var2, Charset var3, Marshaller var4, OutputStream var5) throws JAXBException;
}

