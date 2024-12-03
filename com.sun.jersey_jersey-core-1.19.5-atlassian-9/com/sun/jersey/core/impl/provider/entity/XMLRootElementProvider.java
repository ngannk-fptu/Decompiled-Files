/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.ext.Providers
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.jaxb.AbstractRootElementProvider;
import com.sun.jersey.spi.inject.Injectable;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

public class XMLRootElementProvider
extends AbstractRootElementProvider {
    private final Injectable<SAXParserFactory> spf;

    XMLRootElementProvider(Injectable<SAXParserFactory> spf, Providers ps) {
        super(ps);
        this.spf = spf;
    }

    XMLRootElementProvider(Injectable<SAXParserFactory> spf, Providers ps, MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }

    @Override
    protected Object readFrom(Class<Object> type, MediaType mediaType, Unmarshaller u, InputStream entityStream) throws JAXBException {
        SAXSource s = XMLRootElementProvider.getSAXSource(this.spf.getValue(), entityStream);
        if (type.isAnnotationPresent(XmlRootElement.class)) {
            return u.unmarshal((Source)s);
        }
        return u.unmarshal((Source)s, type).getValue();
    }

    @Produces(value={"*/*"})
    @Consumes(value={"*/*"})
    public static final class General
    extends XMLRootElementProvider {
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
    extends XMLRootElementProvider {
        public Text(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }

    @Produces(value={"application/xml"})
    @Consumes(value={"application/xml"})
    public static final class App
    extends XMLRootElementProvider {
        public App(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
}

