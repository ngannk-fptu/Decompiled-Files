/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.message.source;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codecs;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.SourceReaderFactory;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class ProtocolSourceMessage
extends Message {
    private final Message sm;

    public ProtocolSourceMessage(Source source, SOAPVersion soapVersion) {
        XMLStreamReader reader = SourceReaderFactory.createSourceReader(source, true);
        StreamSOAPCodec codec = Codecs.createSOAPEnvelopeXmlCodec(soapVersion);
        this.sm = codec.decode(reader);
    }

    @Override
    public boolean hasHeaders() {
        return this.sm.hasHeaders();
    }

    @Override
    public String getPayloadLocalPart() {
        return this.sm.getPayloadLocalPart();
    }

    @Override
    public String getPayloadNamespaceURI() {
        return this.sm.getPayloadNamespaceURI();
    }

    @Override
    public boolean hasPayload() {
        return this.sm.hasPayload();
    }

    @Override
    public Source readPayloadAsSource() {
        return this.sm.readPayloadAsSource();
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.sm.readPayload();
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        this.sm.writePayloadTo(sw);
    }

    @Override
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        this.sm.writeTo(sw);
    }

    @Override
    public Message copy() {
        return this.sm.copy().copyFrom(this.sm);
    }

    @Override
    public Source readEnvelopeAsSource() {
        return this.sm.readEnvelopeAsSource();
    }

    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        return this.sm.readAsSOAPMessage();
    }

    @Override
    public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
        return this.sm.readAsSOAPMessage(packet, inbound);
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        return this.sm.readPayloadAsJAXB(unmarshaller);
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        return this.sm.readPayloadAsJAXB(bridge);
    }

    @Override
    public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
        return this.sm.readPayloadAsJAXB(bridge);
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        this.sm.writeTo(contentHandler, errorHandler);
    }

    @Override
    public SOAPVersion getSOAPVersion() {
        return this.sm.getSOAPVersion();
    }

    @Override
    public MessageHeaders getHeaders() {
        return this.sm.getHeaders();
    }
}

