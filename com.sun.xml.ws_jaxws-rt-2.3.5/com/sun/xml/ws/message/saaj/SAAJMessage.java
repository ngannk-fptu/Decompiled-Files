/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.FragmentContentHandler
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.istack.XMLStreamException2
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.bind.unmarshaller.DOMScanner
 *  javax.activation.DataHandler
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 *  javax.xml.soap.AttachmentPart
 *  javax.xml.soap.MimeHeader
 *  javax.xml.soap.SOAPBody
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.message.saaj;

import com.sun.istack.FragmentContentHandler;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.istack.XMLStreamException2;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentEx;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.ws.message.saaj.SAAJHeader;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.DOMStreamReader;
import com.sun.xml.ws.util.ASCIIUtility;
import com.sun.xml.ws.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

public class SAAJMessage
extends Message {
    private boolean parsedMessage;
    private boolean accessedMessage;
    private final SOAPMessage sm;
    private MessageHeaders headers;
    private List<Element> bodyParts;
    private Element payload;
    private String payloadLocalName;
    private String payloadNamespace;
    private SOAPVersion soapVersion;
    private NamedNodeMap bodyAttrs;
    private NamedNodeMap headerAttrs;
    private NamedNodeMap envelopeAttrs;
    private static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
    private static final LocatorImpl NULL_LOCATOR = new LocatorImpl();
    private XMLStreamReader soapBodyFirstChildReader;
    private SOAPElement soapBodyFirstChild;

    public SAAJMessage(SOAPMessage sm) {
        this.sm = sm;
    }

    private SAAJMessage(MessageHeaders headers, AttachmentSet as, SOAPMessage sm, SOAPVersion version) {
        this.sm = sm;
        this.parse();
        if (headers == null) {
            headers = new HeaderList(version);
        }
        this.headers = headers;
        this.attachmentSet = as;
    }

    private void parse() {
        if (!this.parsedMessage) {
            try {
                SOAPHeader header;
                this.access();
                if (this.headers == null) {
                    this.headers = new HeaderList(this.getSOAPVersion());
                }
                if ((header = this.sm.getSOAPHeader()) != null) {
                    this.headerAttrs = header.getAttributes();
                    Iterator iter = header.examineAllHeaderElements();
                    while (iter.hasNext()) {
                        this.headers.add(new SAAJHeader((SOAPHeaderElement)iter.next()));
                    }
                }
                this.attachmentSet = new SAAJAttachmentSet(this.sm);
                this.parsedMessage = true;
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
    }

    protected void access() {
        if (!this.accessedMessage) {
            try {
                this.envelopeAttrs = this.sm.getSOAPPart().getEnvelope().getAttributes();
                SOAPBody body = this.sm.getSOAPBody();
                this.bodyAttrs = body.getAttributes();
                this.soapVersion = SOAPVersion.fromNsUri(body.getNamespaceURI());
                this.bodyParts = DOMUtil.getChildElements((Node)body);
                Element element = this.payload = this.bodyParts.size() > 0 ? this.bodyParts.get(0) : null;
                if (this.payload != null) {
                    this.payloadLocalName = this.payload.getLocalName();
                    this.payloadNamespace = this.payload.getNamespaceURI();
                }
                this.accessedMessage = true;
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
    }

    @Override
    public boolean hasHeaders() {
        this.parse();
        return this.headers.hasHeaders();
    }

    @Override
    @NotNull
    public MessageHeaders getHeaders() {
        this.parse();
        return this.headers;
    }

    @Override
    @NotNull
    public AttachmentSet getAttachments() {
        if (this.attachmentSet == null) {
            this.attachmentSet = new SAAJAttachmentSet(this.sm);
        }
        return this.attachmentSet;
    }

    @Override
    protected boolean hasAttachments() {
        return !this.getAttachments().isEmpty();
    }

    @Override
    @Nullable
    public String getPayloadLocalPart() {
        this.soapBodyFirstChild();
        return this.payloadLocalName;
    }

    @Override
    public String getPayloadNamespaceURI() {
        this.soapBodyFirstChild();
        return this.payloadNamespace;
    }

    @Override
    public boolean hasPayload() {
        return this.soapBodyFirstChild() != null;
    }

    private void addAttributes(Element e, NamedNodeMap attrs) {
        if (attrs == null) {
            return;
        }
        String elPrefix = e.getPrefix();
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if ("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) {
                if (elPrefix == null && a.getLocalName().equals("xmlns") || elPrefix != null && "xmlns".equals(a.getPrefix()) && elPrefix.equals(a.getLocalName())) continue;
                e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
                continue;
            }
            e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
        }
    }

    @Override
    public Source readEnvelopeAsSource() {
        try {
            if (!this.parsedMessage) {
                SOAPEnvelope se = this.sm.getSOAPPart().getEnvelope();
                return new DOMSource((Node)se);
            }
            SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
            this.addAttributes((Element)msg.getSOAPPart().getEnvelope(), this.envelopeAttrs);
            SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
            this.addAttributes((Element)newBody, this.bodyAttrs);
            for (Element part : this.bodyParts) {
                Node n = newBody.getOwnerDocument().importNode(part, true);
                newBody.appendChild(n);
            }
            this.addAttributes((Element)msg.getSOAPHeader(), this.headerAttrs);
            for (Header header : this.headers.asList()) {
                header.writeTo(msg);
            }
            SOAPEnvelope se = msg.getSOAPPart().getEnvelope();
            return new DOMSource((Node)se);
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public SOAPMessage readAsSOAPMessage() throws SOAPException {
        if (!this.parsedMessage) {
            return this.sm;
        }
        SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
        this.addAttributes((Element)msg.getSOAPPart().getEnvelope(), this.envelopeAttrs);
        SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
        this.addAttributes((Element)newBody, this.bodyAttrs);
        for (Element part : this.bodyParts) {
            Node n = newBody.getOwnerDocument().importNode(part, true);
            newBody.appendChild(n);
        }
        this.addAttributes((Element)msg.getSOAPHeader(), this.headerAttrs);
        for (Header header : this.headers.asList()) {
            header.writeTo(msg);
        }
        for (Attachment att : this.getAttachments()) {
            AttachmentPart part = msg.createAttachmentPart();
            part.setDataHandler(att.asDataHandler());
            part.setContentId('<' + att.getContentId() + '>');
            this.addCustomMimeHeaders(att, part);
            msg.addAttachmentPart(part);
        }
        msg.saveChanges();
        return msg;
    }

    private void addCustomMimeHeaders(Attachment att, AttachmentPart part) {
        if (att instanceof AttachmentEx) {
            Iterator<AttachmentEx.MimeHeader> allMimeHeaders = ((AttachmentEx)att).getMimeHeaders();
            while (allMimeHeaders.hasNext()) {
                AttachmentEx.MimeHeader mh = allMimeHeaders.next();
                String name = mh.getName();
                if ("Content-Type".equalsIgnoreCase(name) || "Content-Id".equalsIgnoreCase(name)) continue;
                part.addMimeHeader(name, mh.getValue());
            }
        }
    }

    @Override
    public Source readPayloadAsSource() {
        this.access();
        return this.payload != null ? new DOMSource(this.payload) : null;
    }

    @Override
    public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        this.access();
        if (this.payload != null) {
            if (this.hasAttachments()) {
                unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)new AttachmentUnmarshallerImpl(this.getAttachments()));
            }
            return (T)unmarshaller.unmarshal((Node)this.payload);
        }
        return null;
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        this.access();
        if (this.payload != null) {
            return (T)bridge.unmarshal((Node)this.payload, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
        }
        return null;
    }

    @Override
    public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
        this.access();
        if (this.payload != null) {
            return bridge.unmarshal(this.payload, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
        }
        return null;
    }

    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        return this.soapBodyFirstChildReader();
    }

    @Override
    public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        this.access();
        try {
            for (Element part : this.bodyParts) {
                DOMUtil.serializeNode(part, sw);
            }
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public void writeTo(XMLStreamWriter writer) throws XMLStreamException {
        try {
            writer.writeStartDocument();
            if (!this.parsedMessage) {
                DOMUtil.serializeNode((Element)this.sm.getSOAPPart().getEnvelope(), writer);
            } else {
                SOAPEnvelope env = this.sm.getSOAPPart().getEnvelope();
                DOMUtil.writeTagWithAttributes((Element)env, writer);
                if (this.hasHeaders()) {
                    if (env.getHeader() != null) {
                        DOMUtil.writeTagWithAttributes((Element)env.getHeader(), writer);
                    } else {
                        writer.writeStartElement(env.getPrefix(), "Header", env.getNamespaceURI());
                    }
                    for (Header h : this.headers.asList()) {
                        h.writeTo(writer);
                    }
                    writer.writeEndElement();
                }
                DOMUtil.serializeNode((Element)this.sm.getSOAPBody(), writer);
                writer.writeEndElement();
            }
            writer.writeEndDocument();
            writer.flush();
        }
        catch (SOAPException ex) {
            throw new XMLStreamException2((Throwable)ex);
        }
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        String soapNsUri = this.soapVersion.nsUri;
        if (!this.parsedMessage) {
            DOMScanner ds = new DOMScanner();
            ds.setContentHandler(contentHandler);
            ds.scan((Document)this.sm.getSOAPPart());
        } else {
            contentHandler.setDocumentLocator(NULL_LOCATOR);
            contentHandler.startDocument();
            contentHandler.startPrefixMapping("S", soapNsUri);
            this.startPrefixMapping(contentHandler, this.envelopeAttrs, "S");
            contentHandler.startElement(soapNsUri, "Envelope", "S:Envelope", this.getAttributes(this.envelopeAttrs));
            if (this.hasHeaders()) {
                this.startPrefixMapping(contentHandler, this.headerAttrs, "S");
                contentHandler.startElement(soapNsUri, "Header", "S:Header", this.getAttributes(this.headerAttrs));
                MessageHeaders headers = this.getHeaders();
                for (Header h : headers.asList()) {
                    h.writeTo(contentHandler, errorHandler);
                }
                this.endPrefixMapping(contentHandler, this.headerAttrs, "S");
                contentHandler.endElement(soapNsUri, "Header", "S:Header");
            }
            this.startPrefixMapping(contentHandler, this.bodyAttrs, "S");
            contentHandler.startElement(soapNsUri, "Body", "S:Body", this.getAttributes(this.bodyAttrs));
            this.writePayloadTo(contentHandler, errorHandler, true);
            this.endPrefixMapping(contentHandler, this.bodyAttrs, "S");
            contentHandler.endElement(soapNsUri, "Body", "S:Body");
            this.endPrefixMapping(contentHandler, this.envelopeAttrs, "S");
            contentHandler.endElement(soapNsUri, "Envelope", "S:Envelope");
        }
    }

    private AttributesImpl getAttributes(NamedNodeMap attrs) {
        AttributesImpl atts = new AttributesImpl();
        if (attrs == null) {
            return EMPTY_ATTS;
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if ("xmlns".equals(a.getPrefix()) || "xmlns".equals(a.getLocalName())) continue;
            atts.addAttribute(SAAJMessage.fixNull(a.getNamespaceURI()), a.getLocalName(), a.getName(), a.getSchemaTypeInfo().getTypeName(), a.getValue());
        }
        return atts;
    }

    private void startPrefixMapping(ContentHandler contentHandler, NamedNodeMap attrs, String excludePrefix) throws SAXException {
        if (attrs == null) {
            return;
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if (!"xmlns".equals(a.getPrefix()) && !"xmlns".equals(a.getLocalName()) || SAAJMessage.fixNull(a.getPrefix()).equals(excludePrefix)) continue;
            contentHandler.startPrefixMapping(SAAJMessage.fixNull(a.getPrefix()), a.getNamespaceURI());
        }
    }

    private void endPrefixMapping(ContentHandler contentHandler, NamedNodeMap attrs, String excludePrefix) throws SAXException {
        if (attrs == null) {
            return;
        }
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr a = (Attr)attrs.item(i);
            if (!"xmlns".equals(a.getPrefix()) && !"xmlns".equals(a.getLocalName()) || SAAJMessage.fixNull(a.getPrefix()).equals(excludePrefix)) continue;
            contentHandler.endPrefixMapping(SAAJMessage.fixNull(a.getPrefix()));
        }
    }

    private static String fixNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        if (fragment) {
            contentHandler = new FragmentContentHandler(contentHandler);
        }
        DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(this.payload);
    }

    @Override
    public Message copy() {
        SAAJMessage result = null;
        try {
            this.access();
            if (!this.parsedMessage) {
                result = new SAAJMessage(this.readAsSOAPMessage());
            } else {
                SOAPMessage msg = this.soapVersion.getMessageFactory().createMessage();
                SOAPBody newBody = msg.getSOAPPart().getEnvelope().getBody();
                for (Element part : this.bodyParts) {
                    Node n = newBody.getOwnerDocument().importNode(part, true);
                    newBody.appendChild(n);
                }
                this.addAttributes((Element)newBody, this.bodyAttrs);
                result = new SAAJMessage(this.getHeaders(), this.getAttachments(), msg, this.soapVersion);
            }
            return result.copyFrom(this);
        }
        catch (SOAPException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public SOAPVersion getSOAPVersion() {
        return this.soapVersion;
    }

    protected XMLStreamReader getXMLStreamReader(SOAPElement soapElement) {
        return null;
    }

    protected XMLStreamReader createXMLStreamReader(SOAPElement soapElement) {
        DOMStreamReader dss = new DOMStreamReader();
        dss.setCurrentNode((Node)soapElement);
        return dss;
    }

    protected XMLStreamReader soapBodyFirstChildReader() {
        if (this.soapBodyFirstChildReader != null) {
            return this.soapBodyFirstChildReader;
        }
        this.soapBodyFirstChild();
        if (this.soapBodyFirstChild != null) {
            this.soapBodyFirstChildReader = this.getXMLStreamReader(this.soapBodyFirstChild);
            if (this.soapBodyFirstChildReader == null) {
                this.soapBodyFirstChildReader = this.createXMLStreamReader(this.soapBodyFirstChild);
            }
            if (this.soapBodyFirstChildReader.getEventType() == 7) {
                try {
                    while (this.soapBodyFirstChildReader.getEventType() != 1) {
                        this.soapBodyFirstChildReader.next();
                    }
                }
                catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
            return this.soapBodyFirstChildReader;
        }
        this.payloadLocalName = null;
        this.payloadNamespace = null;
        return null;
    }

    SOAPElement soapBodyFirstChild() {
        if (this.soapBodyFirstChild != null) {
            return this.soapBodyFirstChild;
        }
        try {
            boolean foundElement = false;
            for (Node n = this.sm.getSOAPBody().getFirstChild(); n != null && !foundElement; n = n.getNextSibling()) {
                if (n.getNodeType() != 1) continue;
                foundElement = true;
                if (!(n instanceof SOAPElement)) continue;
                this.soapBodyFirstChild = (SOAPElement)n;
                this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
                this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
                return this.soapBodyFirstChild;
            }
            if (foundElement) {
                Iterator i = this.sm.getSOAPBody().getChildElements();
                while (i.hasNext()) {
                    Object o = i.next();
                    if (!(o instanceof SOAPElement)) continue;
                    this.soapBodyFirstChild = (SOAPElement)o;
                    this.payloadLocalName = this.soapBodyFirstChild.getLocalName();
                    this.payloadNamespace = this.soapBodyFirstChild.getNamespaceURI();
                    return this.soapBodyFirstChild;
                }
            }
        }
        catch (SOAPException e) {
            throw new RuntimeException(e);
        }
        return this.soapBodyFirstChild;
    }

    protected static class SAAJAttachmentSet
    implements AttachmentSet {
        private Map<String, Attachment> attMap;
        private Iterator attIter;

        public SAAJAttachmentSet(SOAPMessage sm) {
            this.attIter = sm.getAttachments();
        }

        @Override
        public Attachment get(String contentId) {
            if (this.attMap == null) {
                if (!this.attIter.hasNext()) {
                    return null;
                }
                this.attMap = this.createAttachmentMap();
            }
            if (contentId.charAt(0) != '<') {
                return this.attMap.get('<' + contentId + '>');
            }
            return this.attMap.get(contentId);
        }

        @Override
        public boolean isEmpty() {
            if (this.attMap != null) {
                return this.attMap.isEmpty();
            }
            return !this.attIter.hasNext();
        }

        @Override
        public Iterator<Attachment> iterator() {
            if (this.attMap == null) {
                this.attMap = this.createAttachmentMap();
            }
            return this.attMap.values().iterator();
        }

        private Map<String, Attachment> createAttachmentMap() {
            HashMap<String, Attachment> map = new HashMap<String, Attachment>();
            while (this.attIter.hasNext()) {
                AttachmentPart ap = (AttachmentPart)this.attIter.next();
                map.put(ap.getContentId(), new SAAJAttachment(ap));
            }
            return map;
        }

        @Override
        public void add(Attachment att) {
            this.attMap.put('<' + att.getContentId() + '>', att);
        }
    }

    protected static class SAAJAttachment
    implements AttachmentEx {
        final AttachmentPart ap;
        String contentIdNoAngleBracket;

        public SAAJAttachment(AttachmentPart part) {
            this.ap = part;
        }

        @Override
        public String getContentId() {
            if (this.contentIdNoAngleBracket == null) {
                this.contentIdNoAngleBracket = this.ap.getContentId();
                if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<') {
                    this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1);
                }
            }
            return this.contentIdNoAngleBracket;
        }

        @Override
        public String getContentType() {
            return this.ap.getContentType();
        }

        @Override
        public byte[] asByteArray() {
            try {
                return this.ap.getRawContentBytes();
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public DataHandler asDataHandler() {
            try {
                return this.ap.getDataHandler();
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Source asSource() {
            try {
                return new StreamSource(this.ap.getRawContent());
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public InputStream asInputStream() {
            try {
                return this.ap.getRawContent();
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public void writeTo(OutputStream os) throws IOException {
            try {
                ASCIIUtility.copyStream(this.ap.getRawContent(), os);
            }
            catch (SOAPException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public void writeTo(SOAPMessage saaj) {
            saaj.addAttachmentPart(this.ap);
        }

        AttachmentPart asAttachmentPart() {
            return this.ap;
        }

        @Override
        public Iterator<AttachmentEx.MimeHeader> getMimeHeaders() {
            final Iterator it = this.ap.getAllMimeHeaders();
            return new Iterator<AttachmentEx.MimeHeader>(){

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public AttachmentEx.MimeHeader next() {
                    final MimeHeader mh = (MimeHeader)it.next();
                    return new AttachmentEx.MimeHeader(){

                        @Override
                        public String getName() {
                            return mh.getName();
                        }

                        @Override
                        public String getValue() {
                            return mh.getValue();
                        }
                    };
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}

