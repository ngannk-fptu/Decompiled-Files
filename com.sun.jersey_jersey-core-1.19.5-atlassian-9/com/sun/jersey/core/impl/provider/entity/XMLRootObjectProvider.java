/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Providers
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.UnmarshalException
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.spi.inject.Injectable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;

public class XMLRootObjectProvider
extends AbstractJAXBProvider<Object> {
    private final Injectable<SAXParserFactory> spf;

    XMLRootObjectProvider(Injectable<SAXParserFactory> spf, Providers ps) {
        super(ps);
        this.spf = spf;
    }

    XMLRootObjectProvider(Injectable<SAXParserFactory> spf, Providers ps, MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }

    @Override
    protected JAXBContext getStoredJAXBContext(Class type) throws JAXBException {
        return null;
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        try {
            return Object.class == type && this.isSupported(mediaType) && this.getUnmarshaller(type, mediaType) != null;
        }
        catch (JAXBException cause) {
            throw new RuntimeException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), cause);
        }
    }

    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            return this.getUnmarshaller(type, mediaType).unmarshal((Source)XMLRootObjectProvider.getSAXSource(this.spf.getValue(), entityStream));
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType mediaType) {
        return false;
    }

    public void writeTo(Object arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4, MultivaluedMap<String, Object> arg5, OutputStream arg6) throws IOException, WebApplicationException {
        throw new IllegalArgumentException();
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends XMLRootObjectProvider {
        public General(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps);
        }

        @Override
        protected boolean isSupported(MediaType m) {
            return m.getSubtype().endsWith("+xml");
        }
    }

    @Produces(value={"text/xml"})
    @Consumes(value={"text/xml"})
    public static final class Text
    extends XMLRootObjectProvider {
        public Text(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }

    @Produces(value={"application/xml"})
    @Consumes(value={"application/xml"})
    public static final class App
    extends XMLRootObjectProvider {
        public App(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
}

