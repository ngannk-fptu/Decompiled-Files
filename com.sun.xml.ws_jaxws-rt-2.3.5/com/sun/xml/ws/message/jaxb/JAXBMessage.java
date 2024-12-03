/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FragmentContentHandler
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferResult
 *  com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.util.JAXBResult
 *  javax.xml.ws.WebServiceException
 *  org.jvnet.staxex.util.MtomStreamWriter
 */
package com.sun.xml.ws.message.jaxb;

import com.sun.istack.FragmentContentHandler;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.StreamingSOAP;
import com.sun.xml.ws.encoding.TagInfoset;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.RootElementSniffer;
import com.sun.xml.ws.message.jaxb.AttachmentMarshallerImpl;
import com.sun.xml.ws.message.jaxb.JAXBBridgeSource;
import com.sun.xml.ws.message.stream.StreamMessage;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.BindingContextFactory;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.ws.util.xml.XMLReaderComposite;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.jvnet.staxex.util.MtomStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class JAXBMessage
extends AbstractMessageImpl
implements StreamingSOAP {
    private MessageHeaders headers;
    private final Object jaxbObject;
    private final XMLBridge bridge;
    private final JAXBContext rawContext;
    private String nsUri;
    private String localName;
    private XMLStreamBuffer infoset;

    public static Message create(BindingContext context, Object jaxbObject, SOAPVersion soapVersion, MessageHeaders headers, AttachmentSet attachments) {
        if (!context.hasSwaRef()) {
            return new JAXBMessage(context, jaxbObject, soapVersion, headers, attachments);
        }
        try {
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            Marshaller m = context.createMarshaller();
            AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(attachments);
            m.setAttachmentMarshaller((AttachmentMarshaller)am);
            am.cleanup();
            m.marshal(jaxbObject, xsb.createFromXMLStreamWriter());
            return new StreamMessage(headers, attachments, (XMLStreamReader)xsb.readAsXMLStreamReader(), soapVersion);
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    public static Message create(BindingContext context, Object jaxbObject, SOAPVersion soapVersion) {
        return JAXBMessage.create(context, jaxbObject, soapVersion, null, null);
    }

    public static Message create(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
        return JAXBMessage.create(BindingContextFactory.create(context), jaxbObject, soapVersion, null, null);
    }

    public static Message createRaw(JAXBContext context, Object jaxbObject, SOAPVersion soapVersion) {
        return new JAXBMessage(context, jaxbObject, soapVersion, null, null);
    }

    private JAXBMessage(BindingContext context, Object jaxbObject, SOAPVersion soapVer, MessageHeaders headers, AttachmentSet attachments) {
        super(soapVer);
        this.bridge = context.createFragmentBridge();
        this.rawContext = null;
        this.jaxbObject = jaxbObject;
        this.headers = headers;
        this.attachmentSet = attachments;
    }

    private JAXBMessage(JAXBContext rawContext, Object jaxbObject, SOAPVersion soapVer, MessageHeaders headers, AttachmentSet attachments) {
        super(soapVer);
        this.rawContext = rawContext;
        this.bridge = null;
        this.jaxbObject = jaxbObject;
        this.headers = headers;
        this.attachmentSet = attachments;
    }

    public static Message create(XMLBridge bridge, Object jaxbObject, SOAPVersion soapVer) {
        if (!bridge.context().hasSwaRef()) {
            return new JAXBMessage(bridge, jaxbObject, soapVer);
        }
        try {
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            AttachmentSetImpl attachments = new AttachmentSetImpl();
            AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(attachments);
            bridge.marshal(jaxbObject, xsb.createFromXMLStreamWriter(), (AttachmentMarshaller)am);
            am.cleanup();
            return new StreamMessage(null, attachments, (XMLStreamReader)xsb.readAsXMLStreamReader(), soapVer);
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private JAXBMessage(XMLBridge bridge, Object jaxbObject, SOAPVersion soapVer) {
        super(soapVer);
        this.bridge = bridge;
        this.rawContext = null;
        this.jaxbObject = jaxbObject;
        QName tagName = bridge.getTypeInfo().tagName;
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
        this.attachmentSet = new AttachmentSetImpl();
    }

    public JAXBMessage(JAXBMessage that) {
        super(that);
        this.headers = that.headers;
        if (this.headers != null) {
            this.headers = new HeaderList(this.headers);
        }
        this.attachmentSet = that.attachmentSet;
        this.jaxbObject = that.jaxbObject;
        this.bridge = that.bridge;
        this.rawContext = that.rawContext;
        this.copyFrom(that);
    }

    @Override
    public boolean hasHeaders() {
        return this.headers != null && this.headers.hasHeaders();
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
        if (this.localName == null) {
            this.sniff();
        }
        return this.localName;
    }

    @Override
    public String getPayloadNamespaceURI() {
        if (this.nsUri == null) {
            this.sniff();
        }
        return this.nsUri;
    }

    @Override
    public boolean hasPayload() {
        return true;
    }

    private void sniff() {
        RootElementSniffer sniffer = new RootElementSniffer(false);
        try {
            if (this.rawContext != null) {
                Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", (Object)Boolean.TRUE);
                m.marshal(this.jaxbObject, (ContentHandler)sniffer);
            } else {
                this.bridge.marshal(this.jaxbObject, sniffer, null);
            }
        }
        catch (JAXBException e) {
            this.nsUri = sniffer.getNsUri();
            this.localName = sniffer.getLocalName();
        }
    }

    @Override
    public Source readPayloadAsSource() {
        return new JAXBBridgeSource(this.bridge, this.jaxbObject);
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        JAXBResult out = new JAXBResult(unmarshaller);
        try {
            out.getHandler().startDocument();
            if (this.rawContext != null) {
                Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", (Object)Boolean.TRUE);
                m.marshal(this.jaxbObject, (Result)out);
            } else {
                this.bridge.marshal(this.jaxbObject, (Result)out);
            }
            out.getHandler().endDocument();
        }
        catch (SAXException e) {
            throw new JAXBException((Throwable)e);
        }
        return (T)out.getResult();
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        try {
            StreamReaderBufferProcessor reader;
            if (this.infoset == null) {
                if (this.rawContext != null) {
                    XMLStreamBufferResult sbr = new XMLStreamBufferResult();
                    Marshaller m = this.rawContext.createMarshaller();
                    m.setProperty("jaxb.fragment", (Object)Boolean.TRUE);
                    m.marshal(this.jaxbObject, (Result)sbr);
                    this.infoset = sbr.getXMLStreamBuffer();
                } else {
                    MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
                    this.writePayloadTo(buffer.createFromXMLStreamWriter());
                    this.infoset = buffer;
                }
            }
            if ((reader = this.infoset.readAsXMLStreamReader()).getEventType() == 7) {
                XMLStreamReaderUtil.nextElementContent((XMLStreamReader)reader);
            }
            return reader;
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        try {
            if (fragment) {
                contentHandler = new FragmentContentHandler(contentHandler);
            }
            AttachmentMarshallerImpl am = new AttachmentMarshallerImpl(this.attachmentSet);
            if (this.rawContext != null) {
                Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", (Object)Boolean.TRUE);
                m.setAttachmentMarshaller((AttachmentMarshaller)am);
                m.marshal(this.jaxbObject, contentHandler);
            } else {
                this.bridge.marshal(this.jaxbObject, contentHandler, (AttachmentMarshaller)am);
            }
            am.cleanup();
        }
        catch (JAXBException e) {
            throw new WebServiceException(e.getMessage(), (Throwable)e);
        }
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        try {
            OutputStream os;
            AttachmentMarshallerImpl am = sw instanceof MtomStreamWriter ? ((MtomStreamWriter)sw).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
            String encoding = XMLStreamWriterUtil.getEncoding(sw);
            OutputStream outputStream = os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
            if (this.rawContext != null) {
                Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", (Object)Boolean.TRUE);
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

    @Override
    public Message copy() {
        return new JAXBMessage(this).copyFrom(this);
    }

    @Override
    public XMLStreamReader readEnvelope() {
        int base = this.soapVersion.ordinal() * 3;
        this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(base);
        this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(base + 2);
        ArrayList<XMLStreamReader> hReaders = new ArrayList<XMLStreamReader>();
        XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
        XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
        for (Header h : this.getHeaders().asList()) {
            try {
                hReaders.add(h.readHeader());
            }
            catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
        XMLReaderComposite soapHeader = null;
        if (hReaders.size() > 0) {
            this.headerTag = (TagInfoset)DEFAULT_TAGS.get(base + 1);
            XMLReaderComposite.ElemInfo hdrElem = new XMLReaderComposite.ElemInfo(this.headerTag, envElem);
            soapHeader = new XMLReaderComposite(hdrElem, hReaders.toArray(new XMLStreamReader[hReaders.size()]));
        }
        try {
            XMLStreamReader[] xMLStreamReaderArray;
            XMLStreamReader payload = this.readPayload();
            XMLReaderComposite soapBody = new XMLReaderComposite(bdyElem, new XMLStreamReader[]{payload});
            if (soapHeader != null) {
                XMLStreamReader[] xMLStreamReaderArray2 = new XMLStreamReader[2];
                xMLStreamReaderArray2[0] = soapHeader;
                xMLStreamReaderArray = xMLStreamReaderArray2;
                xMLStreamReaderArray2[1] = soapBody;
            } else {
                XMLStreamReader[] xMLStreamReaderArray3 = new XMLStreamReader[1];
                xMLStreamReaderArray = xMLStreamReaderArray3;
                xMLStreamReaderArray3[0] = soapBody;
            }
            XMLStreamReader[] soapContent = xMLStreamReaderArray;
            return new XMLReaderComposite(envElem, soapContent);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPayloadStreamReader() {
        return false;
    }

    @Override
    public QName getPayloadQName() {
        return new QName(this.getPayloadNamespaceURI(), this.getPayloadLocalPart());
    }

    @Override
    public XMLStreamReader readToBodyStarTag() {
        XMLStreamReader[] xMLStreamReaderArray;
        int base = this.soapVersion.ordinal() * 3;
        this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(base);
        this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(base + 2);
        ArrayList<XMLStreamReader> hReaders = new ArrayList<XMLStreamReader>();
        XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
        XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
        for (Header h : this.getHeaders().asList()) {
            try {
                hReaders.add(h.readHeader());
            }
            catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
        XMLReaderComposite soapHeader = null;
        if (hReaders.size() > 0) {
            this.headerTag = (TagInfoset)DEFAULT_TAGS.get(base + 1);
            XMLReaderComposite.ElemInfo hdrElem = new XMLReaderComposite.ElemInfo(this.headerTag, envElem);
            soapHeader = new XMLReaderComposite(hdrElem, hReaders.toArray(new XMLStreamReader[hReaders.size()]));
        }
        XMLReaderComposite soapBody = new XMLReaderComposite(bdyElem, new XMLStreamReader[0]);
        if (soapHeader != null) {
            XMLStreamReader[] xMLStreamReaderArray2 = new XMLStreamReader[2];
            xMLStreamReaderArray2[0] = soapHeader;
            xMLStreamReaderArray = xMLStreamReaderArray2;
            xMLStreamReaderArray2[1] = soapBody;
        } else {
            XMLStreamReader[] xMLStreamReaderArray3 = new XMLStreamReader[1];
            xMLStreamReaderArray = xMLStreamReaderArray3;
            xMLStreamReaderArray3[0] = soapBody;
        }
        XMLStreamReader[] soapContent = xMLStreamReaderArray;
        return new XMLReaderComposite(envElem, soapContent);
    }
}

