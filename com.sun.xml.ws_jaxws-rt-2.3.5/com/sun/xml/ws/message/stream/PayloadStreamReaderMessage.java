/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.xml.ws.message.stream;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.stream.StreamMessage;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class PayloadStreamReaderMessage
extends AbstractMessageImpl {
    private final StreamMessage message;

    public PayloadStreamReaderMessage(XMLStreamReader reader, SOAPVersion soapVer) {
        this(null, reader, new AttachmentSetImpl(), soapVer);
    }

    public PayloadStreamReaderMessage(@Nullable MessageHeaders headers, @NotNull XMLStreamReader reader, @NotNull AttachmentSet attSet, @NotNull SOAPVersion soapVersion) {
        super(soapVersion);
        this.message = new StreamMessage(headers, attSet, reader, soapVersion);
    }

    @Override
    public boolean hasHeaders() {
        return this.message.hasHeaders();
    }

    @Override
    public AttachmentSet getAttachments() {
        return this.message.getAttachments();
    }

    @Override
    public String getPayloadLocalPart() {
        return this.message.getPayloadLocalPart();
    }

    @Override
    public String getPayloadNamespaceURI() {
        return this.message.getPayloadNamespaceURI();
    }

    @Override
    public boolean hasPayload() {
        return true;
    }

    @Override
    public Source readPayloadAsSource() {
        return this.message.readPayloadAsSource();
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.message.readPayload();
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        this.message.writePayloadTo(sw);
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        return (T)this.message.readPayloadAsJAXB(unmarshaller);
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        this.message.writeTo(contentHandler, errorHandler);
    }

    @Override
    protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        this.message.writePayloadTo(contentHandler, errorHandler, fragment);
    }

    @Override
    public Message copy() {
        return this.message.copy().copyFrom(this.message);
    }

    @Override
    @NotNull
    public MessageHeaders getHeaders() {
        return this.message.getHeaders();
    }
}

