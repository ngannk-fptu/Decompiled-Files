/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.istack.XMLStreamReaderToContentHandler
 *  com.sun.xml.bind.api.Bridge
 *  com.sun.xml.stream.buffer.AbstractCreatorProcessor
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferMark
 *  com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator
 *  com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 *  javax.xml.ws.WebServiceException
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter
 */
package com.sun.xml.ws.message.stream;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.istack.XMLStreamReaderToContentHandler;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.stream.buffer.AbstractCreatorProcessor;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.StreamingSOAP;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.encoding.TagInfoset;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.ws.message.stream.StreamHeader11;
import com.sun.xml.ws.message.stream.StreamHeader12;
import com.sun.xml.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.ws.spi.db.XMLBridge;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.xml.DummyLocation;
import com.sun.xml.ws.util.xml.StAXSource;
import com.sun.xml.ws.util.xml.XMLReaderComposite;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.NamespaceSupport;

public class StreamMessage
extends AbstractMessageImpl
implements StreamingSOAP {
    @NotNull
    private XMLStreamReader reader;
    @Nullable
    private MessageHeaders headers;
    private String bodyPrologue = null;
    private String bodyEpilogue = null;
    private String payloadLocalName;
    private String payloadNamespaceURI;
    private Throwable consumedAt;
    private XMLStreamReader envelopeReader;
    private static final String SOAP_ENVELOPE = "Envelope";
    private static final String SOAP_HEADER = "Header";
    private static final String SOAP_BODY = "Body";
    static final StreamHeaderDecoder SOAP12StreamHeaderDecoder = new StreamHeaderDecoder(){

        @Override
        public Header decodeHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
            return new StreamHeader12(reader, mark);
        }
    };
    static final StreamHeaderDecoder SOAP11StreamHeaderDecoder = new StreamHeaderDecoder(){

        @Override
        public Header decodeHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
            return new StreamHeader11(reader, mark);
        }
    };

    public StreamMessage(SOAPVersion v) {
        super(v);
        this.payloadLocalName = null;
        this.payloadNamespaceURI = null;
    }

    public StreamMessage(SOAPVersion v, @NotNull XMLStreamReader envelope, @NotNull AttachmentSet attachments) {
        super(v);
        this.envelopeReader = envelope;
        this.attachmentSet = attachments;
    }

    @Override
    public XMLStreamReader readEnvelope() {
        if (this.envelopeReader == null) {
            XMLStreamReader[] xMLStreamReaderArray;
            ArrayList<XMLStreamReader> hReaders = new ArrayList<XMLStreamReader>();
            XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
            XMLReaderComposite.ElemInfo hdrElem = this.headerTag != null ? new XMLReaderComposite.ElemInfo(this.headerTag, envElem) : null;
            XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
            for (Header h : this.getHeaders().asList()) {
                try {
                    hReaders.add(h.readHeader());
                }
                catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }
            XMLReaderComposite soapHeader = hdrElem != null ? new XMLReaderComposite(hdrElem, hReaders.toArray(new XMLStreamReader[hReaders.size()])) : null;
            XMLStreamReader[] payload = new XMLStreamReader[]{this.readPayload()};
            XMLReaderComposite soapBody = new XMLReaderComposite(bdyElem, payload);
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
        return this.envelopeReader;
    }

    public StreamMessage(@Nullable MessageHeaders headers, @NotNull AttachmentSet attachmentSet, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
        super(soapVersion);
        this.init(headers, attachmentSet, reader, soapVersion);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void init(@Nullable MessageHeaders headers, @NotNull AttachmentSet attachmentSet, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
        this.headers = headers;
        this.attachmentSet = attachmentSet;
        this.reader = reader;
        if (reader.getEventType() == 7) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        if (reader.getEventType() == 2) {
            String body = reader.getLocalName();
            String nsUri = reader.getNamespaceURI();
            assert (body != null);
            assert (nsUri != null);
            if (!body.equals(SOAP_BODY) || !nsUri.equals(soapVersion.nsUri)) throw new WebServiceException("Malformed stream: {" + nsUri + "}" + body);
            this.payloadLocalName = null;
            this.payloadNamespaceURI = null;
        } else {
            this.payloadLocalName = reader.getLocalName();
            this.payloadNamespaceURI = reader.getNamespaceURI();
        }
        int base = soapVersion.ordinal() * 3;
        this.envelopeTag = (TagInfoset)DEFAULT_TAGS.get(base);
        this.headerTag = (TagInfoset)DEFAULT_TAGS.get(base + 1);
        this.bodyTag = (TagInfoset)DEFAULT_TAGS.get(base + 2);
    }

    public StreamMessage(@NotNull TagInfoset envelopeTag, @Nullable TagInfoset headerTag, @NotNull AttachmentSet attachmentSet, @Nullable MessageHeaders headers, @NotNull TagInfoset bodyTag, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
        this(envelopeTag, headerTag, attachmentSet, headers, null, bodyTag, null, reader, soapVersion);
    }

    public StreamMessage(@NotNull TagInfoset envelopeTag, @Nullable TagInfoset headerTag, @NotNull AttachmentSet attachmentSet, @Nullable MessageHeaders headers, @Nullable String bodyPrologue, @NotNull TagInfoset bodyTag, @Nullable String bodyEpilogue, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
        super(soapVersion);
        this.init(envelopeTag, headerTag, attachmentSet, headers, bodyPrologue, bodyTag, bodyEpilogue, reader, soapVersion);
    }

    private void init(@NotNull TagInfoset envelopeTag, @Nullable TagInfoset headerTag, @NotNull AttachmentSet attachmentSet, @Nullable MessageHeaders headers, @Nullable String bodyPrologue, @NotNull TagInfoset bodyTag, @Nullable String bodyEpilogue, @NotNull XMLStreamReader reader, @NotNull SOAPVersion soapVersion) {
        this.init(headers, attachmentSet, reader, soapVersion);
        if (envelopeTag == null) {
            throw new IllegalArgumentException("EnvelopeTag TagInfoset cannot be null");
        }
        if (bodyTag == null) {
            throw new IllegalArgumentException("BodyTag TagInfoset cannot be null");
        }
        this.envelopeTag = envelopeTag;
        this.headerTag = headerTag;
        this.bodyTag = bodyTag;
        this.bodyPrologue = bodyPrologue;
        this.bodyEpilogue = bodyEpilogue;
    }

    @Override
    public boolean hasHeaders() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        return this.headers != null && this.headers.hasHeaders();
    }

    @Override
    public MessageHeaders getHeaders() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        if (this.headers == null) {
            this.headers = new HeaderList(this.getSOAPVersion());
        }
        return this.headers;
    }

    @Override
    public String getPayloadLocalPart() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        return this.payloadLocalName;
    }

    @Override
    public String getPayloadNamespaceURI() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        return this.payloadNamespaceURI;
    }

    @Override
    public boolean hasPayload() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        return this.payloadLocalName != null;
    }

    @Override
    public Source readPayloadAsSource() {
        if (this.hasPayload()) {
            assert (this.unconsumed());
            return new StAXSource(this.reader, true, this.getInscopeNamespaces());
        }
        return null;
    }

    private String[] getInscopeNamespaces() {
        int i;
        NamespaceSupport nss = new NamespaceSupport();
        nss.pushContext();
        for (i = 0; i < this.envelopeTag.ns.length; i += 2) {
            nss.declarePrefix(this.envelopeTag.ns[i], this.envelopeTag.ns[i + 1]);
        }
        nss.pushContext();
        for (i = 0; i < this.bodyTag.ns.length; i += 2) {
            nss.declarePrefix(this.bodyTag.ns[i], this.bodyTag.ns[i + 1]);
        }
        ArrayList<String> inscope = new ArrayList<String>();
        Enumeration<String> en = nss.getPrefixes();
        while (en.hasMoreElements()) {
            String prefix = en.nextElement();
            inscope.add(prefix);
            inscope.add(nss.getURI(prefix));
        }
        return inscope.toArray(new String[inscope.size()]);
    }

    public Object readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
        if (!this.hasPayload()) {
            return null;
        }
        assert (this.unconsumed());
        if (this.hasAttachments()) {
            unmarshaller.setAttachmentUnmarshaller((AttachmentUnmarshaller)new AttachmentUnmarshallerImpl(this.getAttachments()));
        }
        try {
            Object object = unmarshaller.unmarshal(this.reader);
            return object;
        }
        finally {
            unmarshaller.setAttachmentUnmarshaller(null);
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
        }
    }

    @Override
    public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
        if (!this.hasPayload()) {
            return null;
        }
        assert (this.unconsumed());
        Object r = bridge.unmarshal(this.reader, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
        return (T)r;
    }

    @Override
    public <T> T readPayloadAsJAXB(XMLBridge<T> bridge) throws JAXBException {
        if (!this.hasPayload()) {
            return null;
        }
        assert (this.unconsumed());
        T r = bridge.unmarshal(this.reader, (AttachmentUnmarshaller)(this.hasAttachments() ? new AttachmentUnmarshallerImpl(this.getAttachments()) : null));
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
        return r;
    }

    @Override
    public void consume() {
        assert (this.unconsumed());
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
    }

    @Override
    public XMLStreamReader readPayload() {
        if (!this.hasPayload()) {
            return null;
        }
        assert (this.unconsumed());
        return this.reader;
    }

    @Override
    public void writePayloadTo(XMLStreamWriter writer) throws XMLStreamException {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        assert (this.unconsumed());
        if (this.payloadLocalName == null) {
            return;
        }
        if (this.bodyPrologue != null) {
            writer.writeCharacters(this.bodyPrologue);
        }
        XMLStreamReaderToXMLStreamWriter conv = new XMLStreamReaderToXMLStreamWriter();
        while (this.reader.getEventType() != 8) {
            String name = this.reader.getLocalName();
            String nsUri = this.reader.getNamespaceURI();
            if (this.reader.getEventType() == 2) {
                if (this.isBodyElement(name, nsUri)) break;
                String whiteSpaces = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
                if (whiteSpaces == null) continue;
                this.bodyEpilogue = whiteSpaces;
                writer.writeCharacters(whiteSpaces);
                continue;
            }
            conv.bridge(this.reader, writer);
        }
        XMLStreamReaderUtil.readRest(this.reader);
        XMLStreamReaderUtil.close(this.reader);
        XMLStreamReaderFactory.recycle(this.reader);
    }

    private boolean isBodyElement(String name, String nsUri) {
        return name.equals(SOAP_BODY) && nsUri.equals(this.soapVersion.nsUri);
    }

    @Override
    public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        this.writeEnvelope(sw);
    }

    @Override
    public void writeToBodyStart(XMLStreamWriter writer) throws XMLStreamException {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        writer.writeStartDocument();
        this.envelopeTag.writeStart(writer);
        MessageHeaders hl = this.getHeaders();
        if (hl.hasHeaders() && this.headerTag == null) {
            this.headerTag = new TagInfoset(this.envelopeTag.nsUri, SOAP_HEADER, this.envelopeTag.prefix, EMPTY_ATTS, new String[0]);
        }
        if (this.headerTag != null) {
            this.headerTag.writeStart(writer);
            if (hl.hasHeaders()) {
                for (Header h : hl.asList()) {
                    h.writeTo(writer);
                }
            }
            writer.writeEndElement();
        }
        this.bodyTag.writeStart(writer);
    }

    private void writeEnvelope(XMLStreamWriter writer) throws XMLStreamException {
        this.writeToBodyStart(writer);
        if (this.hasPayload()) {
            this.writePayloadTo(writer);
        }
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    @Override
    public void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        assert (this.unconsumed());
        try {
            if (this.payloadLocalName == null) {
                return;
            }
            if (this.bodyPrologue != null) {
                char[] chars = this.bodyPrologue.toCharArray();
                contentHandler.characters(chars, 0, chars.length);
            }
            XMLStreamReaderToContentHandler conv = new XMLStreamReaderToContentHandler(this.reader, contentHandler, true, fragment, this.getInscopeNamespaces());
            while (this.reader.getEventType() != 8) {
                String name = this.reader.getLocalName();
                String nsUri = this.reader.getNamespaceURI();
                if (this.reader.getEventType() == 2) {
                    if (this.isBodyElement(name, nsUri)) break;
                    String whiteSpaces = XMLStreamReaderUtil.nextWhiteSpaceContent(this.reader);
                    if (whiteSpaces == null) continue;
                    this.bodyEpilogue = whiteSpaces;
                    char[] chars = whiteSpaces.toCharArray();
                    contentHandler.characters(chars, 0, chars.length);
                    continue;
                }
                conv.bridge();
            }
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
        }
        catch (XMLStreamException e) {
            Location loc = e.getLocation();
            if (loc == null) {
                loc = DummyLocation.INSTANCE;
            }
            SAXParseException x = new SAXParseException(e.getMessage(), loc.getPublicId(), loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber(), e);
            errorHandler.error(x);
        }
    }

    @Override
    public Message copy() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        try {
            assert (this.unconsumed());
            this.consumedAt = null;
            MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            StreamReaderBufferCreator c = new StreamReaderBufferCreator(xsb);
            c.storeElement(this.envelopeTag.nsUri, this.envelopeTag.localName, this.envelopeTag.prefix, this.envelopeTag.ns);
            c.storeElement(this.bodyTag.nsUri, this.bodyTag.localName, this.bodyTag.prefix, this.bodyTag.ns);
            if (this.hasPayload()) {
                String nsUri;
                String name;
                while (this.reader.getEventType() != 8 && !this.isBodyElement(name = this.reader.getLocalName(), nsUri = this.reader.getNamespaceURI()) && this.reader.getEventType() != 8) {
                    c.create(this.reader);
                    if (this.reader.isWhiteSpace()) {
                        this.bodyEpilogue = XMLStreamReaderUtil.currentWhiteSpaceContent(this.reader);
                        continue;
                    }
                    this.bodyEpilogue = null;
                }
            }
            c.storeEndElement();
            c.storeEndElement();
            c.storeEndElement();
            XMLStreamReaderUtil.readRest(this.reader);
            XMLStreamReaderUtil.close(this.reader);
            XMLStreamReaderFactory.recycle(this.reader);
            this.reader = xsb.readAsXMLStreamReader();
            StreamReaderBufferProcessor clone = xsb.readAsXMLStreamReader();
            this.proceedToRootElement(this.reader);
            this.proceedToRootElement((XMLStreamReader)clone);
            return new StreamMessage(this.envelopeTag, this.headerTag, this.attachmentSet, HeaderList.copy(this.headers), this.bodyPrologue, this.bodyTag, this.bodyEpilogue, (XMLStreamReader)clone, this.soapVersion).copyFrom(this);
        }
        catch (XMLStreamException e) {
            throw new WebServiceException("Failed to copy a message", (Throwable)e);
        }
    }

    private void proceedToRootElement(XMLStreamReader xsr) throws XMLStreamException {
        assert (xsr.getEventType() == 7);
        xsr.nextTag();
        xsr.nextTag();
        xsr.nextTag();
        assert (xsr.getEventType() == 1 || xsr.getEventType() == 2);
    }

    @Override
    public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        contentHandler.setDocumentLocator(NULL_LOCATOR);
        contentHandler.startDocument();
        this.envelopeTag.writeStart(contentHandler);
        if (this.hasHeaders() && this.headerTag == null) {
            this.headerTag = new TagInfoset(this.envelopeTag.nsUri, SOAP_HEADER, this.envelopeTag.prefix, EMPTY_ATTS, new String[0]);
        }
        if (this.headerTag != null) {
            this.headerTag.writeStart(contentHandler);
            if (this.hasHeaders()) {
                MessageHeaders headers = this.getHeaders();
                for (Header h : headers.asList()) {
                    h.writeTo(contentHandler, errorHandler);
                }
            }
            this.headerTag.writeEnd(contentHandler);
        }
        this.bodyTag.writeStart(contentHandler);
        this.writePayloadTo(contentHandler, errorHandler, true);
        this.bodyTag.writeEnd(contentHandler);
        this.envelopeTag.writeEnd(contentHandler);
        contentHandler.endDocument();
    }

    private boolean unconsumed() {
        if (this.payloadLocalName == null) {
            return true;
        }
        if (this.reader.getEventType() != 1) {
            AssertionError error = new AssertionError((Object)"StreamMessage has been already consumed. See the nested exception for where it's consumed");
            ((Throwable)((Object)error)).initCause(this.consumedAt);
            throw error;
        }
        this.consumedAt = new Exception().fillInStackTrace();
        return true;
    }

    public String getBodyPrologue() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        return this.bodyPrologue;
    }

    public String getBodyEpilogue() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        return this.bodyEpilogue;
    }

    public XMLStreamReader getReader() {
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        assert (this.unconsumed());
        return this.reader;
    }

    private static void readEnvelope(StreamMessage message) {
        if (message.envelopeReader == null) {
            return;
        }
        XMLStreamReader reader = message.envelopeReader;
        message.envelopeReader = null;
        SOAPVersion soapVersion = message.soapVersion;
        if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        XMLStreamReaderUtil.verifyReaderState(reader, 1);
        if (SOAP_ENVELOPE.equals(reader.getLocalName()) && !soapVersion.nsUri.equals(reader.getNamespaceURI())) {
            throw new VersionMismatchException(soapVersion, soapVersion.nsUri, reader.getNamespaceURI());
        }
        XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, SOAP_ENVELOPE);
        TagInfoset envelopeTag = new TagInfoset(reader);
        HashMap<String, String> namespaces = new HashMap<String, String>();
        for (int i = 0; i < reader.getNamespaceCount(); ++i) {
            namespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
        }
        XMLStreamReaderUtil.nextElementContent(reader);
        XMLStreamReaderUtil.verifyReaderState(reader, 1);
        HeaderList headers = null;
        TagInfoset headerTag = null;
        if (reader.getLocalName().equals(SOAP_HEADER) && reader.getNamespaceURI().equals(soapVersion.nsUri)) {
            headerTag = new TagInfoset(reader);
            for (int i = 0; i < reader.getNamespaceCount(); ++i) {
                namespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }
            XMLStreamReaderUtil.nextElementContent(reader);
            if (reader.getEventType() == 1) {
                headers = new HeaderList(soapVersion);
                try {
                    StreamHeaderDecoder headerDecoder = SOAPVersion.SOAP_11.equals((Object)soapVersion) ? SOAP11StreamHeaderDecoder : SOAP12StreamHeaderDecoder;
                    StreamMessage.cacheHeaders(reader, namespaces, headers, headerDecoder);
                }
                catch (XMLStreamException e) {
                    throw new WebServiceException((Throwable)e);
                }
            }
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, SOAP_BODY);
        TagInfoset bodyTag = new TagInfoset(reader);
        String bodyPrologue = XMLStreamReaderUtil.nextWhiteSpaceContent(reader);
        message.init(envelopeTag, headerTag, message.attachmentSet, headers, bodyPrologue, bodyTag, null, reader, soapVersion);
    }

    private static XMLStreamBuffer cacheHeaders(XMLStreamReader reader, Map<String, String> namespaces, HeaderList headers, StreamHeaderDecoder headerDecoder) throws XMLStreamException {
        MutableXMLStreamBuffer buffer = StreamMessage.createXMLStreamBuffer();
        StreamReaderBufferCreator creator = new StreamReaderBufferCreator();
        creator.setXMLStreamBuffer(buffer);
        while (reader.getEventType() == 1) {
            Map<String, String> headerBlockNamespaces = namespaces;
            if (reader.getNamespaceCount() > 0) {
                headerBlockNamespaces = new HashMap<String, String>(namespaces);
                for (int i = 0; i < reader.getNamespaceCount(); ++i) {
                    headerBlockNamespaces.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
                }
            }
            XMLStreamBufferMark mark = new XMLStreamBufferMark(headerBlockNamespaces, (AbstractCreatorProcessor)creator);
            headers.add(headerDecoder.decodeHeader(reader, (XMLStreamBuffer)mark));
            creator.createElementFragment(reader, false);
            if (reader.getEventType() == 1 || reader.getEventType() == 2) continue;
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        return buffer;
    }

    private static MutableXMLStreamBuffer createXMLStreamBuffer() {
        return new MutableXMLStreamBuffer();
    }

    @Override
    public boolean isPayloadStreamReader() {
        return true;
    }

    @Override
    public QName getPayloadQName() {
        return this.hasPayload() ? new QName(this.payloadNamespaceURI, this.payloadLocalName) : null;
    }

    @Override
    public XMLStreamReader readToBodyStarTag() {
        XMLStreamReader[] xMLStreamReaderArray;
        if (this.envelopeReader != null) {
            StreamMessage.readEnvelope(this);
        }
        ArrayList<XMLStreamReader> hReaders = new ArrayList<XMLStreamReader>();
        XMLReaderComposite.ElemInfo envElem = new XMLReaderComposite.ElemInfo(this.envelopeTag, null);
        XMLReaderComposite.ElemInfo hdrElem = this.headerTag != null ? new XMLReaderComposite.ElemInfo(this.headerTag, envElem) : null;
        XMLReaderComposite.ElemInfo bdyElem = new XMLReaderComposite.ElemInfo(this.bodyTag, envElem);
        for (Header h : this.getHeaders().asList()) {
            try {
                hReaders.add(h.readHeader());
            }
            catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }
        XMLReaderComposite soapHeader = hdrElem != null ? new XMLReaderComposite(hdrElem, hReaders.toArray(new XMLStreamReader[hReaders.size()])) : null;
        XMLStreamReader[] payload = new XMLStreamReader[]{};
        XMLReaderComposite soapBody = new XMLReaderComposite(bdyElem, payload);
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

    protected static interface StreamHeaderDecoder {
        public Header decodeHeader(XMLStreamReader var1, XMLStreamBuffer var2);
    }
}

