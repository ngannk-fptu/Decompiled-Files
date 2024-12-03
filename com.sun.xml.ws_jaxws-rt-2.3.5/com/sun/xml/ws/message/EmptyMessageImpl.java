/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.message;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.AttachmentSetImpl;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class EmptyMessageImpl
extends AbstractMessageImpl {
    private final MessageHeaders headers;
    private final AttachmentSet attachmentSet;

    public EmptyMessageImpl(SOAPVersion version) {
        super(version);
        this.headers = new HeaderList(version);
        this.attachmentSet = new AttachmentSetImpl();
    }

    public EmptyMessageImpl(MessageHeaders headers, @NotNull AttachmentSet attachmentSet, SOAPVersion version) {
        super(version);
        if (headers == null) {
            headers = new HeaderList(version);
        }
        this.attachmentSet = attachmentSet;
        this.headers = headers;
    }

    private EmptyMessageImpl(EmptyMessageImpl that) {
        super(that);
        this.headers = new HeaderList(that.headers);
        this.attachmentSet = that.attachmentSet;
        this.copyFrom(that);
    }

    @Override
    public boolean hasHeaders() {
        return this.headers.hasHeaders();
    }

    @Override
    public MessageHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public String getPayloadLocalPart() {
        return null;
    }

    @Override
    public String getPayloadNamespaceURI() {
        return null;
    }

    @Override
    public boolean hasPayload() {
        return false;
    }

    @Override
    public Source readPayloadAsSource() {
        return null;
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return null;
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
    }

    @Override
    public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
    }

    @Override
    public Message copy() {
        return new EmptyMessageImpl(this).copyFrom(this);
    }
}

