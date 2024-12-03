/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Marshaller$Listener
 *  javax.xml.bind.PropertyException
 *  javax.xml.bind.ValidationEventHandler
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  javax.xml.bind.attachment.AttachmentMarshaller
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.BaseJSONMarshaller;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public final class JSONMarshallerImpl
extends BaseJSONMarshaller
implements Marshaller {
    public JSONMarshallerImpl(JAXBContext jaxbContext, JSONConfiguration jsonConfig) throws JAXBException {
        super(jaxbContext, jsonConfig);
    }

    public void marshal(Object jaxbObject, Result result) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, result);
    }

    public void marshal(Object jaxbObject, OutputStream os) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, os);
    }

    public void marshal(Object jaxbObject, File file) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, file);
    }

    public void marshal(Object jaxbObject, Writer writer) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, writer);
    }

    public void marshal(Object jaxbObject, ContentHandler handler) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, handler);
    }

    public void marshal(Object jaxbObject, Node node) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, node);
    }

    public void marshal(Object jaxbObject, XMLStreamWriter writer) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, writer);
    }

    public void marshal(Object jaxbObject, XMLEventWriter writer) throws JAXBException {
        this.jaxbMarshaller.marshal(jaxbObject, writer);
    }

    public Node getNode(Object jaxbObject) throws JAXBException {
        return this.jaxbMarshaller.getNode(jaxbObject);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null.");
        }
        if (name.equals("com.sun.jersey.api.json.JSONMarshaller.formatted")) {
            if (!(value instanceof Boolean)) {
                throw new PropertyException("property " + name + " must be an instance of type boolean, not " + value.getClass().getName());
            }
            this.jsonConfig = JSONConfiguration.createJSONConfigurationWithFormatted(this.jsonConfig, (Boolean)value);
        } else {
            this.jaxbMarshaller.setProperty(name, value);
        }
    }

    public Object getProperty(String key) throws PropertyException {
        return this.jaxbMarshaller.getProperty(key);
    }

    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        this.jaxbMarshaller.setEventHandler(handler);
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.jaxbMarshaller.getEventHandler();
    }

    public void setAdapter(XmlAdapter adapter) {
        this.jaxbMarshaller.setAdapter(adapter);
    }

    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        this.jaxbMarshaller.setAdapter(type, adapter);
    }

    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return (A)this.jaxbMarshaller.getAdapter(type);
    }

    public void setAttachmentMarshaller(AttachmentMarshaller marshaller) {
        this.jaxbMarshaller.setAttachmentMarshaller(marshaller);
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        return this.jaxbMarshaller.getAttachmentMarshaller();
    }

    public void setSchema(Schema schema) {
        this.jaxbMarshaller.setSchema(schema);
    }

    public Schema getSchema() {
        return this.jaxbMarshaller.getSchema();
    }

    public void setListener(Marshaller.Listener listener) {
        this.jaxbMarshaller.setListener(listener);
    }

    public Marshaller.Listener getListener() {
        return this.jaxbMarshaller.getListener();
    }
}

