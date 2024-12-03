/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.jaxb.AbstractJAXBElementProvider;
import com.sun.jersey.spi.inject.Injectable;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;

public class XMLJAXBElementProvider
extends AbstractJAXBElementProvider {
    private final Injectable<SAXParserFactory> spf;

    public XMLJAXBElementProvider(Injectable<SAXParserFactory> spf, Providers ps) {
        super(ps);
        this.spf = spf;
    }

    public XMLJAXBElementProvider(Injectable<SAXParserFactory> spf, Providers ps, MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }

    @Override
    protected final JAXBElement<?> readFrom(Class<?> type, MediaType mediaType, Unmarshaller u, InputStream entityStream) throws JAXBException {
        return u.unmarshal((Source)XMLJAXBElementProvider.getSAXSource(this.spf.getValue(), entityStream), type);
    }

    @Override
    protected final void writeTo(JAXBElement<?> t, MediaType mediaType, Charset c, Marshaller m, OutputStream entityStream) throws JAXBException {
        m.marshal(t, entityStream);
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends XMLJAXBElementProvider {
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
    extends XMLJAXBElementProvider {
        public Text(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }

    @Produces(value={"application/xml"})
    @Consumes(value={"application/xml"})
    public static final class App
    extends XMLJAXBElementProvider {
        public App(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
}

