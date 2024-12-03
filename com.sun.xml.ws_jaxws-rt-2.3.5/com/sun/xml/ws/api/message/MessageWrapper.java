/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.api.Bridge
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.MessageMetadata;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.message.saaj.SAAJMessage;
import com.sun.xml.ws.message.stream.StreamMessage;
import com.sun.xml.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

class MessageWrapper
extends StreamMessage {
    Packet packet;
    Message delegate;
    StreamMessage streamDelegate;

    @Override
    public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        this.streamDelegate.writePayloadTo(contentHandler, errorHandler, fragment);
    }

    @Override
    public String getBodyPrologue() {
        return this.streamDelegate.getBodyPrologue();
    }

    @Override
    public String getBodyEpilogue() {
        return this.streamDelegate.getBodyEpilogue();
    }

    MessageWrapper(Packet p, Message m) {
        super(m.getSOAPVersion());
        this.packet = p;
        this.delegate = m;
        this.streamDelegate = m instanceof StreamMessage ? (StreamMessage)m : null;
        this.setMessageMedadata(p);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    boolean isProtocolMessage() {
        return this.delegate.isProtocolMessage();
    }

    @Override
    void setIsProtocolMessage() {
        this.delegate.setIsProtocolMessage();
    }

    @Override
    public boolean hasHeaders() {
        return this.delegate.hasHeaders();
    }

    @Override
    public AttachmentSet getAttachments() {
        return this.delegate.getAttachments();
    }

    public String toString() {
        return "{MessageWrapper: " + this.delegate.toString() + "}";
    }

    @Override
    public boolean isOneWay(WSDLPort port) {
        return this.delegate.isOneWay(port);
    }

    @Override
    public String getPayloadLocalPart() {
        return this.delegate.getPayloadLocalPart();
    }

    @Override
    public String getPayloadNamespaceURI() {
        return this.delegate.getPayloadNamespaceURI();
    }

    @Override
    public boolean hasPayload() {
        return this.delegate.hasPayload();
    }

    @Override
    public boolean isFault() {
        return this.delegate.isFault();
    }

    @Override
    public QName getFirstDetailEntryName() {
        return this.delegate.getFirstDetailEntryName();
    }

    @Override
    public Source readEnvelopeAsSource() {
        return this.delegate.readEnvelopeAsSource();
    }

    @Override
    public Source readPayloadAsSource() {
        return this.delegate.readPayloadAsSource();
    }

    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        if (!(this.delegate instanceof SAAJMessage)) {
            this.delegate = this.toSAAJ(this.packet, null);
        }
        return this.delegate.readAsSOAPMessage();
    }

    @Override
    public SOAPMessage readAsSOAPMessage(Packet p, boolean inbound) throws SOAPException {
        if (!(this.delegate instanceof SAAJMessage)) {
            this.delegate = this.toSAAJ(p, inbound);
        }
        return this.delegate.readAsSOAPMessage();
    }

    @Override
    public Object readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(unmarshaller);
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(bridge);
    }

    @Override
    public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
        return this.delegate.readPayloadAsJAXB(bridge);
    }

    @Override
    public XMLStreamReader readPayload() {
        try {
            return this.delegate.readPayload();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void consume() {
        this.delegate.consume();
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        this.delegate.writePayloadTo(sw);
    }

    @Override
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        this.delegate.writeTo(sw);
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        this.delegate.writeTo(contentHandler, errorHandler);
    }

    @Override
    public Message copy() {
        return this.delegate.copy().copyFrom(this.delegate);
    }

    @Override
    public String getID(WSBinding binding) {
        return this.delegate.getID(binding);
    }

    @Override
    public String getID(AddressingVersion av, SOAPVersion sv) {
        return this.delegate.getID(av, sv);
    }

    @Override
    public SOAPVersion getSOAPVersion() {
        return this.delegate.getSOAPVersion();
    }

    @Override
    @NotNull
    public MessageHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override
    public void setMessageMedadata(MessageMetadata metadata) {
        super.setMessageMedadata(metadata);
        this.delegate.setMessageMedadata(metadata);
    }
}

