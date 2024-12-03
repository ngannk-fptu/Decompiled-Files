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
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Produces(value={"application/xml", "text/xml", "*/*"})
@Consumes(value={"application/xml", "text/xml", "*/*"})
public final class DocumentProvider
extends AbstractMessageReaderWriterProvider<Document> {
    private final Injectable<DocumentBuilderFactory> dbf;
    private final Injectable<TransformerFactory> tf;

    public DocumentProvider(@Context Injectable<DocumentBuilderFactory> dbf, @Context Injectable<TransformerFactory> tf) {
        this.dbf = dbf;
        this.tf = tf;
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Document.class == type;
    }

    public Document readFrom(Class<Document> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            return this.dbf.getValue().newDocumentBuilder().parse(entityStream);
        }
        catch (SAXException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (ParserConfigurationException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Document.class.isAssignableFrom(type);
    }

    public void writeTo(Document t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            StreamResult sr = new StreamResult(entityStream);
            this.tf.getValue().newTransformer().transform(new DOMSource(t), sr);
        }
        catch (TransformerException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

