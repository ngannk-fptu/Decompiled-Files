/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.provider.jaxb.AbstractRootElementProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.json.impl.reader.JsonFormatException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JSONRootElementProvider
extends AbstractRootElementProvider {
    boolean jacksonEntityProviderTakesPrecedence = false;

    JSONRootElementProvider(Providers ps) {
        super(ps);
    }

    JSONRootElementProvider(Providers ps, MediaType mt) {
        super(ps, mt);
    }

    @Override
    @Context
    public void setConfiguration(FeaturesAndProperties fp) {
        super.setConfiguration(fp);
        this.consumeFeaturesAndProperties(fp);
    }

    protected void consumeFeaturesAndProperties(FeaturesAndProperties fp) {
        this.jacksonEntityProviderTakesPrecedence = fp.getFeature("com.sun.jersey.api.json.POJOMappingFeature");
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return !this.jacksonEntityProviderTakesPrecedence && super.isReadable(type, genericType, annotations, mediaType);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return !this.jacksonEntityProviderTakesPrecedence && super.isWriteable(type, genericType, annotations, mediaType);
    }

    @Override
    protected final Object readFrom(Class<Object> type, MediaType mediaType, Unmarshaller u, InputStream entityStream) throws JAXBException {
        Charset c = JSONRootElementProvider.getCharset(mediaType);
        try {
            return JSONJAXBContext.getJSONUnmarshaller(u, this.getJAXBContext(type)).unmarshalFromJSON(new InputStreamReader(entityStream, c), type);
        }
        catch (JsonFormatException e) {
            throw new WebApplicationException((Throwable)e, Response.Status.BAD_REQUEST);
        }
    }

    @Override
    protected void writeTo(Object t, MediaType mediaType, Charset c, Marshaller m, OutputStream entityStream) throws JAXBException {
        JSONMarshaller jsonMarshaller = JSONJAXBContext.getJSONMarshaller(m, this.getJAXBContext(t.getClass()));
        if (this.isFormattedOutput()) {
            jsonMarshaller.setProperty("com.sun.jersey.api.json.JSONMarshaller.formatted", true);
        }
        jsonMarshaller.marshallToJSON(t, new OutputStreamWriter(entityStream, c));
    }

    @Produces(value={"application/vnd.sun.wadl+json"})
    @Consumes(value={"application/vnd.sun.wadl+json"})
    public static final class Wadl
    extends JSONRootElementProvider {
        public Wadl(@Context Providers ps) {
            super(ps, MediaTypes.WADL_JSON);
        }

        @Override
        protected void consumeFeaturesAndProperties(FeaturesAndProperties fp) {
        }
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends JSONRootElementProvider {
        public General(@Context Providers ps) {
            super(ps);
        }

        @Override
        protected boolean isSupported(MediaType m) {
            return !this.jacksonEntityProviderTakesPrecedence && m.getSubtype().endsWith("+json");
        }
    }

    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public static final class App
    extends JSONRootElementProvider {
        public App(@Context Providers ps) {
            super(ps, MediaType.APPLICATION_JSON_TYPE);
        }
    }
}

