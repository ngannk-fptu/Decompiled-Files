/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.PropertyException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.Unmarshaller$Listener
 *  javax.xml.bind.UnmarshallerHandler
 *  javax.xml.bind.ValidationEventHandler
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.BaseJSONUnmarshaller;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class JSONUnmarshallerImpl
extends BaseJSONUnmarshaller
implements Unmarshaller {
    public JSONUnmarshallerImpl(JAXBContext jaxbContext, JSONConfiguration jsonConfig) throws JAXBException {
        super(jaxbContext, jsonConfig);
    }

    public Object unmarshal(File file) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(file);
    }

    public Object unmarshal(InputStream inputStream) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(inputStream);
    }

    public Object unmarshal(Reader reader) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(reader);
    }

    public Object unmarshal(URL url) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(url);
    }

    public Object unmarshal(InputSource inputSource) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(inputSource);
    }

    public Object unmarshal(Node node) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(node);
    }

    public <T> JAXBElement<T> unmarshal(Node node, Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(node, type);
    }

    public Object unmarshal(Source source) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(source);
    }

    public <T> JAXBElement<T> unmarshal(Source source, Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(source, type);
    }

    public Object unmarshal(XMLStreamReader xmlStreamReader) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlStreamReader);
    }

    public <T> JAXBElement<T> unmarshal(XMLStreamReader xmlStreamReader, Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlStreamReader, type);
    }

    public Object unmarshal(XMLEventReader xmlEventReader) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlEventReader);
    }

    public <T> JAXBElement<T> unmarshal(XMLEventReader xmlEventReader, Class<T> type) throws JAXBException {
        return this.jaxbUnmarshaller.unmarshal(xmlEventReader, type);
    }

    public UnmarshallerHandler getUnmarshallerHandler() {
        return this.jaxbUnmarshaller.getUnmarshallerHandler();
    }

    public void setValidating(boolean validating) throws JAXBException {
        this.jaxbUnmarshaller.setValidating(validating);
    }

    public boolean isValidating() throws JAXBException {
        return this.jaxbUnmarshaller.isValidating();
    }

    public void setEventHandler(ValidationEventHandler validationEventHandler) throws JAXBException {
        this.jaxbUnmarshaller.setEventHandler(validationEventHandler);
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        return this.jaxbUnmarshaller.getEventHandler();
    }

    public void setProperty(String key, Object value) throws PropertyException {
        this.jaxbUnmarshaller.setProperty(key, value);
    }

    public Object getProperty(String key) throws PropertyException {
        return this.jaxbUnmarshaller.getProperty(key);
    }

    public void setSchema(Schema schema) {
        this.jaxbUnmarshaller.setSchema(schema);
    }

    public Schema getSchema() {
        return this.jaxbUnmarshaller.getSchema();
    }

    public void setAdapter(XmlAdapter xmlAdapter) {
        this.jaxbUnmarshaller.setAdapter(xmlAdapter);
    }

    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        this.jaxbUnmarshaller.setAdapter(type, adapter);
    }

    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return (A)this.jaxbUnmarshaller.getAdapter(type);
    }

    public void setAttachmentUnmarshaller(AttachmentUnmarshaller attachmentUnmarshaller) {
        this.jaxbUnmarshaller.setAttachmentUnmarshaller(attachmentUnmarshaller);
    }

    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return this.jaxbUnmarshaller.getAttachmentUnmarshaller();
    }

    public void setListener(Unmarshaller.Listener listener) {
        this.jaxbUnmarshaller.setListener(listener);
    }

    public Unmarshaller.Listener getListener() {
        return this.jaxbUnmarshaller.getListener();
    }
}

