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
 *  javax.ws.rs.ext.MessageBodyReader
 *  javax.ws.rs.ext.MessageBodyWriter
 */
package com.sun.jersey.core.impl.provider.entity;

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
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class SourceProvider {

    @Produces(value={"application/xml", "text/xml", "*/*"})
    @Consumes(value={"application/xml", "text/xml", "*/*"})
    public static final class SourceWriter
    implements MessageBodyWriter<Source> {
        private final Injectable<SAXParserFactory> spf;
        private final Injectable<TransformerFactory> tf;

        public SourceWriter(@Context Injectable<SAXParserFactory> spf, @Context Injectable<TransformerFactory> tf) {
            this.spf = spf;
            this.tf = tf;
        }

        public boolean isWriteable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
            return Source.class.isAssignableFrom(t);
        }

        public long getSize(Source o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1L;
        }

        public void writeTo(Source o, Class<?> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
            try {
                if (o instanceof StreamSource) {
                    StreamSource s = (StreamSource)o;
                    InputSource is = new InputSource(s.getInputStream());
                    o = new SAXSource(this.spf.getValue().newSAXParser().getXMLReader(), is);
                }
                StreamResult sr = new StreamResult(entityStream);
                this.tf.getValue().newTransformer().transform(o, sr);
            }
            catch (SAXException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (ParserConfigurationException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (TransformerException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Produces(value={"application/xml", "text/xml", "*/*"})
    @Consumes(value={"application/xml", "text/xml", "*/*"})
    public static final class DOMSourceReader
    implements MessageBodyReader<DOMSource> {
        private final Injectable<DocumentBuilderFactory> dbf;

        public DOMSourceReader(@Context Injectable<DocumentBuilderFactory> dbf) {
            this.dbf = dbf;
        }

        public boolean isReadable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
            return DOMSource.class == t;
        }

        public DOMSource readFrom(Class<DOMSource> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
            try {
                Document d = this.dbf.getValue().newDocumentBuilder().parse(entityStream);
                return new DOMSource(d);
            }
            catch (SAXParseException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
            }
            catch (SAXException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (ParserConfigurationException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Produces(value={"application/xml", "text/xml", "*/*"})
    @Consumes(value={"application/xml", "text/xml", "*/*"})
    public static final class SAXSourceReader
    implements MessageBodyReader<SAXSource> {
        private final Injectable<SAXParserFactory> spf;

        public SAXSourceReader(@Context Injectable<SAXParserFactory> spf) {
            this.spf = spf;
        }

        public boolean isReadable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
            return SAXSource.class == t;
        }

        public SAXSource readFrom(Class<SAXSource> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
            try {
                return new SAXSource(this.spf.getValue().newSAXParser().getXMLReader(), new InputSource(entityStream));
            }
            catch (SAXParseException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
            }
            catch (SAXException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (ParserConfigurationException ex) {
                throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Produces(value={"application/xml", "text/xml", "*/*"})
    @Consumes(value={"application/xml", "text/xml", "*/*"})
    public static final class StreamSourceReader
    implements MessageBodyReader<StreamSource> {
        public boolean isReadable(Class<?> t, Type gt, Annotation[] as, MediaType mediaType) {
            return StreamSource.class == t;
        }

        public StreamSource readFrom(Class<StreamSource> t, Type gt, Annotation[] as, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
            return new StreamSource(entityStream);
        }
    }
}

