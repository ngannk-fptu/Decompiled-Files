/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.api.Bridge
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.MessageWritable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.encoding.TagInfoset;
import com.sun.xml.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.ws.message.XMLReaderImpl;
import com.sun.xml.ws.message.saaj.SAAJMessage;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public abstract class AbstractMessageImpl
extends Message {
    protected final SOAPVersion soapVersion;
    @NotNull
    protected TagInfoset envelopeTag;
    @NotNull
    protected TagInfoset headerTag;
    @NotNull
    protected TagInfoset bodyTag;
    protected static final AttributesImpl EMPTY_ATTS;
    protected static final LocatorImpl NULL_LOCATOR;
    protected static final List<TagInfoset> DEFAULT_TAGS;

    static void create(SOAPVersion v, List c) {
        int base = v.ordinal() * 3;
        c.add(base, new TagInfoset(v.nsUri, "Envelope", "S", EMPTY_ATTS, "S", v.nsUri));
        c.add(base + 1, new TagInfoset(v.nsUri, "Header", "S", EMPTY_ATTS, new String[0]));
        c.add(base + 2, new TagInfoset(v.nsUri, "Body", "S", EMPTY_ATTS, new String[0]));
    }

    protected AbstractMessageImpl(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }

    @Override
    public SOAPVersion getSOAPVersion() {
        return this.soapVersion;
    }

    protected AbstractMessageImpl(AbstractMessageImpl that) {
        this.soapVersion = that.soapVersion;
        this.copyFrom(that);
    }

    @Override
    public Source readEnvelopeAsSource() {
        return new SAXSource(new XMLReaderImpl(this), XMLReaderImpl.THE_SOURCE);
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)new AttachmentUnmarshallerImpl(this.getAttachments()));
        }
        try {
            Object object = unmarshaller.unmarshal(this.readPayloadAsSource());
            return (T)object;
        }
        finally {
            unmarshaller.setAttachmentUnmarshaller(null);
        }
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        return (T)bridge.unmarshal(this.readPayloadAsSource(), (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
    }

    @Override
    public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(this.readPayloadAsSource(), (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
    }

    public void writeToBodyStart(XMLStreamWriter w) throws XMLStreamException {
        String soapNsUri = this.soapVersion.nsUri;
        w.writeStartDocument();
        w.writeStartElement("S", "Envelope", soapNsUri);
        w.writeNamespace("S", soapNsUri);
        if (this.hasHeaders()) {
            w.writeStartElement("S", "Header", soapNsUri);
            MessageHeaders headers = this.getHeaders();
            for (Header h : headers.asList()) {
                h.writeTo(w);
            }
            w.writeEndElement();
        }
        w.writeStartElement("S", "Body", soapNsUri);
    }

    @Override
    public void writeTo(XMLStreamWriter w) throws XMLStreamException {
        this.writeToBodyStart(w);
        this.writePayloadTo(w);
        w.writeEndElement();
        w.writeEndElement();
        w.writeEndDocument();
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        String soapNsUri = this.soapVersion.nsUri;
        contentHandler.setDocumentLocator(NULL_LOCATOR);
        contentHandler.startDocument();
        contentHandler.startPrefixMapping("S", soapNsUri);
        contentHandler.startElement(soapNsUri, "Envelope", "S:Envelope", EMPTY_ATTS);
        if (this.hasHeaders()) {
            contentHandler.startElement(soapNsUri, "Header", "S:Header", EMPTY_ATTS);
            MessageHeaders headers = this.getHeaders();
            for (Header h : headers.asList()) {
                h.writeTo(contentHandler, errorHandler);
            }
            contentHandler.endElement(soapNsUri, "Header", "S:Header");
        }
        contentHandler.startElement(soapNsUri, "Body", "S:Body", EMPTY_ATTS);
        this.writePayloadTo(contentHandler, errorHandler, true);
        contentHandler.endElement(soapNsUri, "Body", "S:Body");
        contentHandler.endElement(soapNsUri, "Envelope", "S:Envelope");
    }

    protected abstract void writePayloadTo(ContentHandler var1, ErrorHandler var2, boolean var3) throws SAXException;

    public Message toSAAJ(Packet p, Boolean inbound) throws SOAPException {
        SAAJMessage message = SAAJFactory.read(p);
        if (message instanceof MessageWritable) {
            ((MessageWritable)((Object)message)).setMTOMConfiguration(p.getMtomFeature());
        }
        if (inbound != null) {
            this.transportHeaders(p, inbound, message.readAsSOAPMessage());
        }
        return message;
    }

    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        return SAAJFactory.read(this.soapVersion, this);
    }

    @Override
    public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
        SOAPMessage msg = SAAJFactory.read(this.soapVersion, this, packet);
        this.transportHeaders(packet, inbound, msg);
        return msg;
    }

    private void transportHeaders(Packet packet, boolean inbound, SOAPMessage msg) throws SOAPException {
        Map<String, List<String>> headers = AbstractMessageImpl.getTransportHeaders(packet, inbound);
        if (headers != null) {
            AbstractMessageImpl.addSOAPMimeHeaders(msg.getMimeHeaders(), headers);
        }
        if (msg.saveRequired()) {
            msg.saveChanges();
        }
    }

    static {
        NULL_LOCATOR = new LocatorImpl();
        EMPTY_ATTS = new AttributesImpl();
        ArrayList tagList = new ArrayList();
        AbstractMessageImpl.create(SOAPVersion.SOAP_11, tagList);
        AbstractMessageImpl.create(SOAPVersion.SOAP_12, tagList);
        DEFAULT_TAGS = Collections.unmodifiableList(tagList);
    }
}

