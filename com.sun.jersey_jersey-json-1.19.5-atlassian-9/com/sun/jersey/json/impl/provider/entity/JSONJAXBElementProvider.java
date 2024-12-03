/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.provider.jaxb.AbstractJAXBElementProvider
 *  com.sun.jersey.core.util.FeaturesAndProperties
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.Providers
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.core.provider.jaxb.AbstractJAXBElementProvider;
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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JSONJAXBElementProvider
extends AbstractJAXBElementProvider {
    boolean jacksonEntityProviderTakesPrecedence = false;

    JSONJAXBElementProvider(Providers ps) {
        super(ps);
    }

    JSONJAXBElementProvider(Providers ps, MediaType mt) {
        super(ps, mt);
    }

    @Context
    public void setConfiguration(FeaturesAndProperties fp) {
        super.setConfiguration(fp);
        this.jacksonEntityProviderTakesPrecedence = fp.getFeature("com.sun.jersey.api.json.POJOMappingFeature");
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return !this.jacksonEntityProviderTakesPrecedence && super.isReadable(type, genericType, annotations, mediaType);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return !this.jacksonEntityProviderTakesPrecedence && super.isWriteable(type, genericType, annotations, mediaType);
    }

    protected final JAXBElement<?> readFrom(Class<?> type, MediaType mediaType, Unmarshaller u, InputStream entityStream) throws JAXBException {
        Charset c = JSONJAXBElementProvider.getCharset((MediaType)mediaType);
        try {
            return JSONJAXBContext.getJSONUnmarshaller(u, this.getStoredJAXBContext(type)).unmarshalJAXBElementFromJSON(new InputStreamReader(entityStream, c), type);
        }
        catch (JsonFormatException e) {
            throw new WebApplicationException((Throwable)e, Response.Status.BAD_REQUEST);
        }
    }

    protected final void writeTo(JAXBElement<?> t, MediaType mediaType, Charset c, Marshaller m, OutputStream entityStream) throws JAXBException {
        JSONMarshaller jsonMarshaller = JSONJAXBContext.getJSONMarshaller(m, this.getStoredJAXBContext(t.getDeclaredType()));
        if (this.isFormattedOutput()) {
            jsonMarshaller.setProperty("com.sun.jersey.api.json.JSONMarshaller.formatted", true);
        }
        jsonMarshaller.marshallToJSON(t, new OutputStreamWriter(entityStream, c));
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends JSONJAXBElementProvider {
        public General(@Context Providers ps) {
            super(ps);
        }

        protected boolean isSupported(MediaType m) {
            return m.getSubtype().endsWith("+json");
        }
    }

    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public static final class App
    extends JSONJAXBElementProvider {
        public App(@Context Providers ps) {
            super(ps, MediaType.APPLICATION_JSON_TYPE);
        }
    }
}

