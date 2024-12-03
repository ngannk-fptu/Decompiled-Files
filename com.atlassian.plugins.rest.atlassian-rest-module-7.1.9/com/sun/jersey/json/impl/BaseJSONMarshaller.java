/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.PropertyException
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.api.json.JSONConfigurated;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONMarshaller;
import com.sun.jersey.json.impl.Stax2JsonFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLStreamWriter;

public class BaseJSONMarshaller
implements JSONMarshaller,
JSONConfigurated {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    protected final Marshaller jaxbMarshaller;
    private final JAXBContext jaxbContext;
    protected JSONConfiguration jsonConfig;

    public BaseJSONMarshaller(JAXBContext jaxbContext, JSONConfiguration jsonConfig) throws JAXBException {
        this(jaxbContext.createMarshaller(), jaxbContext, jsonConfig);
    }

    public BaseJSONMarshaller(Marshaller jaxbMarshaller, JAXBContext jaxbContext, JSONConfiguration jsonConfig) {
        this.jsonConfig = jsonConfig;
        this.jaxbContext = jaxbContext;
        this.jaxbMarshaller = jaxbMarshaller;
    }

    @Override
    public JSONConfiguration getJSONConfiguration() {
        return this.jsonConfig;
    }

    @Override
    public void marshallToJSON(Object o, OutputStream outputStream) throws JAXBException {
        if (outputStream == null) {
            throw new IllegalArgumentException("The output stream is null");
        }
        this.marshallToJSON(o, new OutputStreamWriter(outputStream, UTF8));
    }

    @Override
    public void marshallToJSON(Object o, Writer writer) throws JAXBException {
        if (o == null) {
            throw new IllegalArgumentException("The JAXB element is null");
        }
        if (writer == null) {
            throw new IllegalArgumentException("The writer is null");
        }
        this.jaxbMarshaller.marshal(o, this.getXMLStreamWriter(writer, o.getClass()));
    }

    private XMLStreamWriter getXMLStreamWriter(Writer writer, Class<?> expectedType) throws JAXBException {
        try {
            return Stax2JsonFactory.createWriter(writer, this.jsonConfig, expectedType, this.jaxbContext);
        }
        catch (IOException ex) {
            throw new JAXBException((Throwable)ex);
        }
    }

    @Override
    public void setProperty(String key, Object value) throws PropertyException {
    }
}

