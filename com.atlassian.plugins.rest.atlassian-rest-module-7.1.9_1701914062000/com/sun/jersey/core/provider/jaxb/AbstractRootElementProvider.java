/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.core.provider.jaxb;

import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public abstract class AbstractRootElementProvider
extends AbstractJAXBProvider<Object> {
    public AbstractRootElementProvider(Providers ps) {
        super(ps);
    }

    public AbstractRootElementProvider(Providers ps, MediaType mt) {
        super(ps, mt);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (type.getAnnotation(XmlRootElement.class) != null || type.getAnnotation(XmlType.class) != null) && this.isSupported(mediaType);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.getAnnotation(XmlRootElement.class) != null && this.isSupported(mediaType);
    }

    @Override
    public final Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            return this.readFrom(type, mediaType, this.getUnmarshaller(type, mediaType), entityStream);
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected Object readFrom(Class<Object> type, MediaType mediaType, Unmarshaller u, InputStream entityStream) throws JAXBException {
        if (type.isAnnotationPresent(XmlRootElement.class)) {
            return u.unmarshal(entityStream);
        }
        return u.unmarshal((Source)new StreamSource(entityStream), type).getValue();
    }

    @Override
    public final void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            Marshaller m = this.getMarshaller(type, mediaType);
            Charset c = AbstractRootElementProvider.getCharset(mediaType);
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

    protected void writeTo(Object t, MediaType mediaType, Charset c, Marshaller m, OutputStream entityStream) throws JAXBException {
        m.marshal(t, entityStream);
    }
}

