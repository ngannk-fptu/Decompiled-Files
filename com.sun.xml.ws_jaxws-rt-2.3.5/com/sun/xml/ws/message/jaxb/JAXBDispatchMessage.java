/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.ws.WebServiceException
 *  org.jvnet.staxex.util.MtomStreamWriter
 */
package com.sun.xml.ws.message.jaxb;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.PayloadElementSniffer;
import com.sun.xml.ws.message.jaxb.AttachmentMarshallerImpl;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.XMLStreamWriterUtil;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.jvnet.staxex.util.MtomStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class JAXBDispatchMessage
extends AbstractMessageImpl {
    private final Object jaxbObject;
    private final XMLBridge bridge;
    private final JAXBContext rawContext;
    private QName payloadQName;

    private JAXBDispatchMessage(JAXBDispatchMessage that) {
        super(that);
        this.jaxbObject = that.jaxbObject;
        this.rawContext = that.rawContext;
        this.bridge = that.bridge;
        this.copyFrom(that);
    }

    public JAXBDispatchMessage(JAXBContext rawContext, Object jaxbObject, SOAPVersion soapVersion) {
        super(soapVersion);
        this.bridge = null;
        this.rawContext = rawContext;
        this.jaxbObject = jaxbObject;
    }

    public JAXBDispatchMessage(BindingContext context, Object jaxbObject, SOAPVersion soapVersion) {
        super(soapVersion);
        this.bridge = context.createFragmentBridge();
        this.rawContext = null;
        this.jaxbObject = jaxbObject;
    }

    @Override
    protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasHeaders() {
        return false;
    }

    @Override
    public MessageHeaders getHeaders() {
        return null;
    }

    @Override
    public String getPayloadLocalPart() {
        if (this.payloadQName == null) {
            this.readPayloadElement();
        }
        return this.payloadQName.getLocalPart();
    }

    @Override
    public String getPayloadNamespaceURI() {
        if (this.payloadQName == null) {
            this.readPayloadElement();
        }
        return this.payloadQName.getNamespaceURI();
    }

    private void readPayloadElement() {
        PayloadElementSniffer sniffer = new PayloadElementSniffer();
        try {
            if (this.rawContext != null) {
                Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", (Object)Boolean.FALSE);
                m.marshal(this.jaxbObject, (ContentHandler)sniffer);
            } else {
                this.bridge.marshal(this.jaxbObject, sniffer, null);
            }
        }
        catch (JAXBException e) {
            this.payloadQName = sniffer.getPayloadQName();
        }
    }

    @Override
    public boolean hasPayload() {
        return true;
    }

    @Override
    public Source readPayloadAsSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Message copy() {
        return new JAXBDispatchMessage(this).copyFrom(this);
    }

    @Override
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        try {
            OutputStream os;
            AttachmentMarshallerImpl am = sw instanceof MtomStreamWriter ? ((MtomStreamWriter)sw).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
            String encoding = XMLStreamWriterUtil.getEncoding(sw);
            OutputStream outputStream = os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
            if (this.rawContext != null) {
                Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", (Object)Boolean.FALSE);
                m.setAttachmentMarshaller((AttachmentMarshaller)am);
                if (os != null) {
                    m.marshal(this.jaxbObject, os);
                } else {
                    m.marshal(this.jaxbObject, sw);
                }
            } else if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
                this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), am);
            } else {
                this.bridge.marshal(this.jaxbObject, sw, (AttachmentMarshaller)am);
            }
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

