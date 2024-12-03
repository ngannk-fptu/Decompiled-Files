/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.api.Bridge
 *  javax.activation.DataSource
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding.xml;

import com.sun.istack.NotNull;
import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.ws.encoding.ContentType;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import com.sun.xml.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.ws.message.AbstractMessageImpl;
import com.sun.xml.ws.message.EmptyMessageImpl;
import com.sun.xml.ws.message.MimeAttachmentSet;
import com.sun.xml.ws.message.source.PayloadSourceMessage;
import com.sun.xml.ws.util.ByteArrayBuffer;
import com.sun.xml.ws.util.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class XMLMessage {
    private static final int PLAIN_XML_FLAG = 1;
    private static final int MIME_MULTIPART_FLAG = 2;
    private static final int FI_ENCODED_FLAG = 16;

    public static Message create(String ct, InputStream in, WSFeatureList f) {
        AbstractMessageImpl data;
        try {
            ContentType contentType;
            int contentTypeId;
            in = StreamUtils.hasSomeData(in);
            if (in == null) {
                return Messages.createEmpty(SOAPVersion.SOAP_11);
            }
            data = ct != null ? (((contentTypeId = XMLMessage.identifyContentType(contentType = new ContentType(ct))) & 2) != 0 ? new XMLMultiPart(ct, in, f) : ((contentTypeId & 1) != 0 ? new XmlContent(ct, in, f) : new UnknownContent(ct, in))) : new UnknownContent("application/octet-stream", in);
        }
        catch (Exception ex) {
            throw new WebServiceException((Throwable)ex);
        }
        return data;
    }

    public static Message create(Source source) {
        return source == null ? Messages.createEmpty(SOAPVersion.SOAP_11) : Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
    }

    public static Message create(DataSource ds, WSFeatureList f) {
        try {
            return ds == null ? Messages.createEmpty(SOAPVersion.SOAP_11) : XMLMessage.create(ds.getContentType(), ds.getInputStream(), f);
        }
        catch (IOException ioe) {
            throw new WebServiceException((Throwable)ioe);
        }
    }

    public static Message create(Exception e) {
        return new FaultMessage(SOAPVersion.SOAP_11);
    }

    private static int getContentId(String ct) {
        try {
            ContentType contentType = new ContentType(ct);
            return XMLMessage.identifyContentType(contentType);
        }
        catch (Exception ex) {
            throw new WebServiceException((Throwable)ex);
        }
    }

    public static boolean isFastInfoset(String ct) {
        return (XMLMessage.getContentId(ct) & 0x10) != 0;
    }

    public static int identifyContentType(ContentType contentType) {
        String primary = contentType.getPrimaryType();
        String sub = contentType.getSubType();
        if (primary.equalsIgnoreCase("multipart") && sub.equalsIgnoreCase("related")) {
            String type = contentType.getParameter("type");
            if (type != null) {
                if (XMLMessage.isXMLType(type)) {
                    return 3;
                }
                if (XMLMessage.isFastInfosetType(type)) {
                    return 18;
                }
            }
            return 0;
        }
        if (XMLMessage.isXMLType(primary, sub)) {
            return 1;
        }
        if (XMLMessage.isFastInfosetType(primary, sub)) {
            return 16;
        }
        return 0;
    }

    protected static boolean isXMLType(@NotNull String primary, @NotNull String sub) {
        return primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml") || primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("xml") || primary.equalsIgnoreCase("application") && sub.toLowerCase().endsWith("+xml");
    }

    protected static boolean isXMLType(String type) {
        String lowerType = type.toLowerCase();
        return lowerType.startsWith("text/xml") || lowerType.startsWith("application/xml") || lowerType.startsWith("application/") && lowerType.indexOf("+xml") != -1;
    }

    protected static boolean isFastInfosetType(String primary, String sub) {
        return primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("fastinfoset");
    }

    protected static boolean isFastInfosetType(String type) {
        return type.toLowerCase().startsWith("application/fastinfoset");
    }

    public static DataSource getDataSource(Message msg, WSFeatureList f) {
        if (msg == null) {
            return null;
        }
        if (msg instanceof MessageDataSource) {
            return ((MessageDataSource)((Object)msg)).getDataSource();
        }
        AttachmentSet atts = msg.getAttachments();
        if (atts != null && !atts.isEmpty()) {
            ByteArrayBuffer bos = new ByteArrayBuffer();
            try {
                XMLHTTPBindingCodec codec = new XMLHTTPBindingCodec(f);
                Packet packet = new Packet(msg);
                com.sun.xml.ws.api.pipe.ContentType ct = codec.getStaticContentType(packet);
                codec.encode(packet, (OutputStream)bos);
                return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
            }
            catch (IOException ioe) {
                throw new WebServiceException((Throwable)ioe);
            }
        }
        ByteArrayBuffer bos = new ByteArrayBuffer();
        XMLStreamWriter writer = XMLStreamWriterFactory.create(bos);
        try {
            msg.writePayloadTo(writer);
            writer.flush();
        }
        catch (XMLStreamException e) {
            throw new WebServiceException((Throwable)e);
        }
        return XMLMessage.createDataSource("text/xml", bos.newInputStream());
    }

    public static DataSource createDataSource(String contentType, InputStream is) {
        return new XmlDataSource(contentType, is);
    }

    private static class XmlDataSource
    implements DataSource {
        private final String contentType;
        private final InputStream is;
        private boolean consumed;

        XmlDataSource(String contentType, InputStream is) {
            this.contentType = contentType;
            this.is = is;
        }

        public boolean consumed() {
            return this.consumed;
        }

        public InputStream getInputStream() {
            this.consumed = !this.consumed;
            return this.is;
        }

        public OutputStream getOutputStream() {
            return null;
        }

        public String getContentType() {
            return this.contentType;
        }

        public String getName() {
            return "";
        }
    }

    public static class UnknownContent
    extends AbstractMessageImpl
    implements MessageDataSource {
        private final DataSource ds;
        private final HeaderList headerList;

        public UnknownContent(String ct, InputStream in) {
            this(XMLMessage.createDataSource(ct, in));
        }

        public UnknownContent(DataSource ds) {
            super(SOAPVersion.SOAP_11);
            this.ds = ds;
            this.headerList = new HeaderList(SOAPVersion.SOAP_11);
        }

        private UnknownContent(UnknownContent that) {
            super(that.soapVersion);
            this.ds = that.ds;
            this.headerList = HeaderList.copy(that.headerList);
            this.copyFrom(that);
        }

        @Override
        public boolean hasUnconsumedDataSource() {
            return true;
        }

        @Override
        public DataSource getDataSource() {
            assert (this.ds != null);
            return this.ds;
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
        public boolean isFault() {
            return false;
        }

        @Override
        public MessageHeaders getHeaders() {
            return this.headerList;
        }

        @Override
        public String getPayloadLocalPart() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPayloadNamespaceURI() {
            throw new UnsupportedOperationException();
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
            throw new WebServiceException("There isn't XML payload. Shouldn't come here.");
        }

        @Override
        public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
        }

        @Override
        public Message copy() {
            return new UnknownContent(this).copyFrom(this);
        }
    }

    private static class FaultMessage
    extends EmptyMessageImpl {
        public FaultMessage(SOAPVersion version) {
            super(version);
        }

        @Override
        public boolean isFault() {
            return true;
        }
    }

    public static final class XMLMultiPart
    extends AbstractMessageImpl
    implements MessageDataSource {
        private final DataSource dataSource;
        private final StreamingAttachmentFeature feature;
        private Message delegate;
        private HeaderList headerList = new HeaderList(SOAPVersion.SOAP_11);
        private final WSFeatureList features;

        public XMLMultiPart(String contentType, InputStream is, WSFeatureList f) {
            super(SOAPVersion.SOAP_11);
            this.dataSource = XMLMessage.createDataSource(contentType, is);
            this.feature = f.get(StreamingAttachmentFeature.class);
            this.features = f;
        }

        private Message getMessage() {
            if (this.delegate == null) {
                MimeMultipartParser mpp;
                try {
                    mpp = new MimeMultipartParser(this.dataSource.getInputStream(), this.dataSource.getContentType(), this.feature);
                }
                catch (IOException ioe) {
                    throw new WebServiceException((Throwable)ioe);
                }
                InputStream in = mpp.getRootPart().asInputStream();
                assert (in != null);
                this.delegate = new PayloadSourceMessage((MessageHeaders)this.headerList, new StreamSource(in), (AttachmentSet)new MimeAttachmentSet(mpp), SOAPVersion.SOAP_11);
            }
            return this.delegate;
        }

        @Override
        public boolean hasUnconsumedDataSource() {
            return this.delegate == null;
        }

        @Override
        public DataSource getDataSource() {
            return this.hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(this.getMessage(), this.features);
        }

        @Override
        public boolean hasHeaders() {
            return false;
        }

        @Override
        @NotNull
        public MessageHeaders getHeaders() {
            return this.headerList;
        }

        @Override
        public String getPayloadLocalPart() {
            return this.getMessage().getPayloadLocalPart();
        }

        @Override
        public String getPayloadNamespaceURI() {
            return this.getMessage().getPayloadNamespaceURI();
        }

        @Override
        public boolean hasPayload() {
            return true;
        }

        @Override
        public boolean isFault() {
            return false;
        }

        @Override
        public Source readEnvelopeAsSource() {
            return this.getMessage().readEnvelopeAsSource();
        }

        @Override
        public Source readPayloadAsSource() {
            return this.getMessage().readPayloadAsSource();
        }

        @Override
        public SOAPMessage readAsSOAPMessage() throws SOAPException {
            return this.getMessage().readAsSOAPMessage();
        }

        @Override
        public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
            return this.getMessage().readAsSOAPMessage(packet, inbound);
        }

        @Override
        public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(unmarshaller);
        }

        @Override
        public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(bridge);
        }

        @Override
        public XMLStreamReader readPayload() throws XMLStreamException {
            return this.getMessage().readPayload();
        }

        @Override
        public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writePayloadTo(sw);
        }

        @Override
        public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writeTo(sw);
        }

        @Override
        public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
            this.getMessage().writeTo(contentHandler, errorHandler);
        }

        @Override
        public Message copy() {
            return this.getMessage().copy().copyFrom(this.getMessage());
        }

        @Override
        protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOneWay(@NotNull WSDLPort port) {
            return false;
        }

        @Override
        @NotNull
        public AttachmentSet getAttachments() {
            return this.getMessage().getAttachments();
        }
    }

    private static class XmlContent
    extends AbstractMessageImpl
    implements MessageDataSource {
        private final XmlDataSource dataSource;
        private boolean consumed;
        private Message delegate;
        private final HeaderList headerList;
        private WSFeatureList features;

        public XmlContent(String ct, InputStream in, WSFeatureList f) {
            super(SOAPVersion.SOAP_11);
            this.dataSource = new XmlDataSource(ct, in);
            this.headerList = new HeaderList(SOAPVersion.SOAP_11);
            this.features = f;
        }

        private Message getMessage() {
            if (this.delegate == null) {
                InputStream in = this.dataSource.getInputStream();
                assert (in != null);
                this.delegate = Messages.createUsingPayload(new StreamSource(in), SOAPVersion.SOAP_11);
                this.consumed = true;
            }
            return this.delegate;
        }

        @Override
        public boolean hasUnconsumedDataSource() {
            return !this.dataSource.consumed() && !this.consumed;
        }

        @Override
        public DataSource getDataSource() {
            return this.hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(this.getMessage(), this.features);
        }

        @Override
        public boolean hasHeaders() {
            return false;
        }

        @Override
        @NotNull
        public MessageHeaders getHeaders() {
            return this.headerList;
        }

        @Override
        public String getPayloadLocalPart() {
            return this.getMessage().getPayloadLocalPart();
        }

        @Override
        public String getPayloadNamespaceURI() {
            return this.getMessage().getPayloadNamespaceURI();
        }

        @Override
        public boolean hasPayload() {
            return true;
        }

        @Override
        public boolean isFault() {
            return false;
        }

        @Override
        public Source readEnvelopeAsSource() {
            return this.getMessage().readEnvelopeAsSource();
        }

        @Override
        public Source readPayloadAsSource() {
            return this.getMessage().readPayloadAsSource();
        }

        @Override
        public SOAPMessage readAsSOAPMessage() throws SOAPException {
            return this.getMessage().readAsSOAPMessage();
        }

        @Override
        public SOAPMessage readAsSOAPMessage(Packet packet, boolean inbound) throws SOAPException {
            return this.getMessage().readAsSOAPMessage(packet, inbound);
        }

        @Override
        public <T> T readPayloadAsJAXB(Unmarshaller unmarshaller) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(unmarshaller);
        }

        @Override
        public <T> T readPayloadAsJAXB(Bridge<T> bridge) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(bridge);
        }

        @Override
        public XMLStreamReader readPayload() throws XMLStreamException {
            return this.getMessage().readPayload();
        }

        @Override
        public void writePayloadTo(XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writePayloadTo(sw);
        }

        @Override
        public void writeTo(XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writeTo(sw);
        }

        @Override
        public void writeTo(ContentHandler contentHandler, ErrorHandler errorHandler) throws SAXException {
            this.getMessage().writeTo(contentHandler, errorHandler);
        }

        @Override
        public Message copy() {
            return this.getMessage().copy().copyFrom(this.getMessage());
        }

        @Override
        protected void writePayloadTo(ContentHandler contentHandler, ErrorHandler errorHandler, boolean fragment) throws SAXException {
            throw new UnsupportedOperationException();
        }
    }

    public static interface MessageDataSource {
        public boolean hasUnconsumedDataSource();

        public DataSource getDataSource();
    }
}

