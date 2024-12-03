/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.provider.jaxb.AbstractListElementProvider
 *  com.sun.jersey.core.util.FeaturesAndProperties
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.ext.Providers
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.provider.jaxb.AbstractListElementProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.json.impl.JSONHelper;
import com.sun.jersey.json.impl.Stax2JsonFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class JSONListElementProvider
extends AbstractListElementProvider {
    boolean jacksonEntityProviderTakesPrecedence = false;

    JSONListElementProvider(Providers ps) {
        super(ps);
    }

    JSONListElementProvider(Providers ps, MediaType mt) {
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

    public final void writeList(Class<?> elementType, Collection<?> t, MediaType mediaType, Charset c, Marshaller m, OutputStream entityStream) throws JAXBException, IOException {
        OutputStreamWriter osw = new OutputStreamWriter(entityStream, c);
        JSONConfiguration origJsonConfig = JSONConfiguration.DEFAULT;
        if (m instanceof JSONConfigurated) {
            origJsonConfig = ((JSONConfigurated)m).getJSONConfiguration();
        }
        JSONConfiguration unwrappingJsonConfig = JSONConfiguration.createJSONConfigurationWithRootUnwrapping(origJsonConfig, true);
        XMLStreamWriter jxsw = Stax2JsonFactory.createWriter(osw, unwrappingJsonConfig, elementType, this.getStoredJAXBContext(elementType), true);
        String invisibleRootName = this.getRootElementName(elementType);
        String elementName = this.getElementName(elementType);
        try {
            if (!origJsonConfig.isRootUnwrapping()) {
                osw.append(String.format("{\"%s\":", elementName));
                osw.flush();
            }
            jxsw.writeStartDocument();
            jxsw.writeStartElement(invisibleRootName);
            for (Object o : t) {
                m.marshal(o, jxsw);
            }
            jxsw.writeEndElement();
            jxsw.writeEndDocument();
            jxsw.flush();
            if (!origJsonConfig.isRootUnwrapping()) {
                osw.append("}");
                osw.flush();
            }
        }
        catch (XMLStreamException ex) {
            Logger.getLogger(JSONListElementProvider.class.getName()).log(Level.SEVERE, null, ex);
            throw new JAXBException(ex.getMessage(), (Throwable)ex);
        }
    }

    protected final XMLStreamReader getXMLStreamReader(Class<?> elementType, MediaType mediaType, Unmarshaller u, InputStream entityStream) throws XMLStreamException {
        JSONConfiguration c = JSONConfiguration.DEFAULT;
        Charset charset = JSONListElementProvider.getCharset((MediaType)mediaType);
        if (u instanceof JSONConfigurated) {
            c = ((JSONConfigurated)u).getJSONConfiguration();
        }
        try {
            return Stax2JsonFactory.createReader(new InputStreamReader(entityStream, charset), c, JSONHelper.getRootElementName(elementType), elementType, this.getStoredJAXBContext(elementType), true);
        }
        catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends JSONListElementProvider {
        public General(@Context Providers ps) {
            super(ps);
        }

        protected boolean isSupported(MediaType m) {
            return !this.jacksonEntityProviderTakesPrecedence && m.getSubtype().endsWith("+json");
        }
    }

    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public static final class App
    extends JSONListElementProvider {
        public App(@Context Providers ps) {
            super(ps, MediaType.APPLICATION_JSON_TYPE);
        }
    }
}

