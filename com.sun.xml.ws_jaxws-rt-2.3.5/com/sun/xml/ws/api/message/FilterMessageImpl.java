/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.api.Bridge
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
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

public class FilterMessageImpl
extends Message {
    private final Message delegate;

    protected FilterMessageImpl(Message delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasHeaders() {
        return this.delegate.hasHeaders();
    }

    @Override
    @NotNull
    public MessageHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override
    @NotNull
    public AttachmentSet getAttachments() {
        return this.delegate.getAttachments();
    }

    @Override
    protected boolean hasAttachments() {
        return this.delegate.hasAttachments();
    }

    @Override
    public boolean isOneWay(@NotNull WSDLPort port) {
        return this.delegate.isOneWay(port);
    }

    @Override
    @Nullable
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
    @Nullable
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
        return this.delegate.readAsSOAPMessage();
    }

    @Override
    public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
        return this.delegate.readAsSOAPMessage(packet, inbound);
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
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
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.delegate.readPayload();
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
    @NotNull
    public String getID(@NotNull WSBinding binding) {
        return this.delegate.getID(binding);
    }

    @Override
    @NotNull
    public String getID(AddressingVersion av, SOAPVersion sv) {
        return this.delegate.getID(av, sv);
    }

    @Override
    public SOAPVersion getSOAPVersion() {
        return this.delegate.getSOAPVersion();
    }
}

