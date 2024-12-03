/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FragmentContentHandler
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.unmarshaller.DOMScanner
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message;

import com.sun.istack.FragmentContentHandler;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.ws.streaming.DOMStreamReader;
import com.sun.xml.ws.util.DOMUtil;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class DOMMessage
extends AbstractMessageImpl {
    private MessageHeaders headers;
    private final Element payload;

    public DOMMessage(SOAPVersion ver, Element payload) {
        this(ver, null, payload);
    }

    public DOMMessage(SOAPVersion ver, MessageHeaders headers, Element payload) {
        this(ver, headers, payload, null);
    }

    public DOMMessage(SOAPVersion ver, MessageHeaders headers, Element payload, AttachmentSet attachments) {
        super(ver);
        this.headers = headers;
        this.payload = payload;
        this.attachmentSet = attachments;
        assert (payload != null);
    }

    private DOMMessage(DOMMessage that) {
        super(that);
        this.headers = HeaderList.copy(that.headers);
        this.payload = that.payload;
        this.copyFrom(that);
    }

    @Override
    public boolean hasHeaders() {
        return this.getHeaders().hasHeaders();
    }

    @Override
    public MessageHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HeaderList(this.getSOAPVersion());
        }
        return this.headers;
    }

    @Override
    public String getPayloadLocalPart() {
        return this.payload.getLocalName();
    }

    @Override
    public String getPayloadNamespaceURI() {
        return this.payload.getNamespaceURI();
    }

    @Override
    public boolean hasPayload() {
        return true;
    }

    @Override
    public Source readPayloadAsSource() {
        return new DOMSource(this.payload);
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)new AttachmentUnmarshallerImpl(this.getAttachments()));
        }
        try {
            Object object = unmarshaller.unmarshal((Node)this.payload);
            return (T)object;
        }
        finally {
            unmarshaller.setAttachmentUnmarshaller(null);
        }
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        return (T)bridge.unmarshal((Node)this.payload, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        DOMStreamReader dss = new DOMStreamReader();
        dss.setCurrentNode(this.payload);
        dss.nextTag();
        assert (dss.getEventType() == 1);
        return dss;
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) {
        try {
            if (this.payload != null) {
                DOMUtil.serializeNode(this.payload, sw);
            }
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        if (fragment) {
            contentHandler = new FragmentContentHandler(contentHandler);
        }
        DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(this.payload);
    }

    @Override
    public Message copy() {
        return new DOMMessage(this).copyFrom(this);
    }
}

