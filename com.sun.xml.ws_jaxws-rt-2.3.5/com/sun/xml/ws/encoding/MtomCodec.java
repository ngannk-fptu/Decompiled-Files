/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.DatatypeConverterImpl
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.MTOMFeature
 *  org.jvnet.staxex.Base64Data
 *  org.jvnet.staxex.NamespaceContextEx
 *  org.jvnet.staxex.NamespaceContextEx$Binding
 *  org.jvnet.staxex.XMLStreamReaderEx
 *  org.jvnet.staxex.XMLStreamWriterEx
 *  org.jvnet.staxex.util.MtomStreamWriter
 */
package com.sun.xml.ws.encoding;

import com.sun.istack.NotNull;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.developer.SerializationFeature;
import com.sun.xml.ws.developer.StreamingDataHandler;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.HasEncoding;
import com.sun.xml.ws.encoding.MimeCodec;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import com.sun.xml.ws.message.MimeAttachmentSet;
import com.sun.xml.ws.server.UnsupportedMediaException;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.ws.util.ByteArrayDataSource;
import com.sun.xml.ws.util.xml.NamespaceContextExAdaper;
import com.sun.xml.ws.util.xml.XMLStreamReaderFilter;
import com.sun.xml.ws.util.xml.XMLStreamWriterFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import org.jvnet.staxex.Base64Data;
import org.jvnet.staxex.NamespaceContextEx;
import org.jvnet.staxex.XMLStreamReaderEx;
import org.jvnet.staxex.XMLStreamWriterEx;
import org.jvnet.staxex.util.MtomStreamWriter;

public class MtomCodec
extends MimeCodec {
    public static final String XOP_XML_MIME_TYPE = "application/xop+xml";
    public static final String XOP_LOCALNAME = "Include";
    public static final String XOP_NAMESPACEURI = "http://www.w3.org/2004/08/xop/include";
    private final StreamSOAPCodec codec;
    private final MTOMFeature mtomFeature;
    private final SerializationFeature sf;
    private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";

    MtomCodec(SOAPVersion version, StreamSOAPCodec codec, WSFeatureList features) {
        super(version, features);
        this.codec = codec;
        this.sf = features.get(SerializationFeature.class);
        MTOMFeature mtom = features.get(MTOMFeature.class);
        this.mtomFeature = mtom == null ? new MTOMFeature() : mtom;
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        return MtomCodec.getStaticContentTypeStatic(packet, this.version);
    }

    public static ContentType getStaticContentTypeStatic(Packet packet, SOAPVersion version) {
        ContentTypeImpl ct = (ContentTypeImpl)packet.getInternalContentType();
        if (ct != null && ct.getBoundary() != null && ct.getRootId() != null) {
            return ct;
        }
        String uuid = UUID.randomUUID().toString();
        String boundary = "uuid:" + uuid;
        String rootId = "<rootpart*" + uuid + "@example.jaxws.sun.com>";
        String soapActionParameter = SOAPVersion.SOAP_11.equals((Object)version) ? null : MtomCodec.createActionParameter(packet);
        String boundaryParameter = "boundary=\"" + boundary + "\"";
        String messageContentType = "multipart/related;start=\"" + rootId + "\";type=\"" + XOP_XML_MIME_TYPE + "\";" + boundaryParameter + ";start-info=\"" + version.contentType + (soapActionParameter == null ? "" : soapActionParameter) + "\"";
        ContentTypeImpl ctImpl = SOAPVersion.SOAP_11.equals((Object)version) ? new ContentTypeImpl(messageContentType, packet.soapAction == null ? "" : packet.soapAction, null) : new ContentTypeImpl(messageContentType, null, null);
        ctImpl.setBoundary(boundary);
        ctImpl.setRootId(rootId);
        packet.setContentType(ctImpl);
        return ctImpl;
    }

    private static String createActionParameter(Packet packet) {
        return packet.soapAction != null ? ";action=\\\"" + packet.soapAction + "\\\"" : "";
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) throws IOException {
        ContentTypeImpl ctImpl = (ContentTypeImpl)this.getStaticContentType(packet);
        String boundary = ctImpl.getBoundary();
        String rootId = ctImpl.getRootId();
        if (packet.getMessage() != null) {
            try {
                String encoding = this.getPacketEncoding(packet);
                packet.invocationProperties.remove(DECODED_MESSAGE_CHARSET);
                String actionParameter = MtomCodec.getActionParameter(packet, this.version);
                String soapXopContentType = MtomCodec.getSOAPXopContentType(encoding, this.version, actionParameter);
                MtomCodec.writeln("--" + boundary, out);
                MtomCodec.writeMimeHeaders(soapXopContentType, rootId, out);
                ArrayList<ByteArrayBuffer> mtomAttachments = new ArrayList<ByteArrayBuffer>();
                MtomStreamWriterImpl writer = new MtomStreamWriterImpl(XMLStreamWriterFactory.create(out, encoding), mtomAttachments, boundary, this.mtomFeature);
                packet.getMessage().writeTo(writer);
                XMLStreamWriterFactory.recycle(writer);
                MtomCodec.writeln(out);
                for (ByteArrayBuffer bos : mtomAttachments) {
                    bos.write(out);
                }
                this.writeNonMtomAttachments(packet.getMessage().getAttachments(), out, boundary);
                MtomCodec.writeAsAscii("--" + boundary, out);
                MtomCodec.writeAsAscii("--", out);
            }
            catch (XMLStreamException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
        return ctImpl;
    }

    public static String getSOAPXopContentType(String encoding, SOAPVersion version, String actionParameter) {
        return "application/xop+xml;charset=" + encoding + ";type=\"" + version.contentType + actionParameter + "\"";
    }

    public static String getActionParameter(Packet packet, SOAPVersion version) {
        return version == SOAPVersion.SOAP_11 ? "" : MtomCodec.createActionParameter(packet);
    }

    public static void writeMimeHeaders(String contentType, String contentId, OutputStream out) throws IOException {
        String cid = contentId;
        if (cid != null && cid.length() > 0 && cid.charAt(0) != '<') {
            cid = '<' + cid + '>';
        }
        MtomCodec.writeln("Content-Id: " + cid, out);
        MtomCodec.writeln("Content-Type: " + contentType, out);
        MtomCodec.writeln("Content-Transfer-Encoding: binary", out);
        MtomCodec.writeln(out);
    }

    private void writeNonMtomAttachments(AttachmentSet attachments, OutputStream out, String boundary) throws IOException {
        for (Attachment att : attachments) {
            StreamingDataHandler sdh;
            DataHandler dh = att.asDataHandler();
            if (dh instanceof StreamingDataHandler && (sdh = (StreamingDataHandler)dh).getHrefCid() != null) continue;
            MtomCodec.writeln("--" + boundary, out);
            MtomCodec.writeMimeHeaders(att.getContentType(), att.getContentId(), out);
            att.writeTo(out);
            MtomCodec.writeln(out);
        }
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MtomCodec copy() {
        return new MtomCodec(this.version, (StreamSOAPCodec)this.codec.copy(), this.features);
    }

    private static String encodeCid() {
        String cid = "example.jaxws.sun.com";
        String name = UUID.randomUUID() + "@";
        return name + cid;
    }

    @Override
    protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
        String charset = null;
        String ct = mpp.getRootPart().getContentType();
        if (ct != null) {
            charset = new ContentTypeImpl(ct).getCharSet();
        }
        if (charset != null && !Charset.isSupported(charset)) {
            throw new UnsupportedMediaException(charset);
        }
        if (charset != null) {
            packet.invocationProperties.put(DECODED_MESSAGE_CHARSET, charset);
        } else {
            packet.invocationProperties.remove(DECODED_MESSAGE_CHARSET);
        }
        MtomXMLStreamReaderEx mtomReader = new MtomXMLStreamReaderEx(mpp, XMLStreamReaderFactory.create(null, mpp.getRootPart().asInputStream(), charset, true));
        packet.setMessage(this.codec.decode(mtomReader, new MimeAttachmentSet(mpp)));
        packet.setMtomFeature(this.mtomFeature);
        packet.setContentType(mpp.getContentType());
    }

    private String getPacketEncoding(Packet packet) {
        if (this.sf != null && this.sf.getEncoding() != null) {
            return this.sf.getEncoding().equals("") ? "utf-8" : this.sf.getEncoding();
        }
        return MtomCodec.determinePacketEncoding(packet);
    }

    public static String determinePacketEncoding(Packet packet) {
        if (packet != null && packet.endpoint != null) {
            String charset = (String)packet.invocationProperties.get(DECODED_MESSAGE_CHARSET);
            return charset == null ? "utf-8" : charset;
        }
        return "utf-8";
    }

    public static class MtomXMLStreamReaderEx
    extends XMLStreamReaderFilter
    implements XMLStreamReaderEx {
        private final MimeMultipartParser mimeMP;
        private boolean xopReferencePresent = false;
        private Base64Data base64AttData;
        private char[] base64EncodedText;
        private String xopHref;

        public MtomXMLStreamReaderEx(MimeMultipartParser mimeMP, XMLStreamReader reader) {
            super(reader);
            this.mimeMP = mimeMP;
        }

        public CharSequence getPCDATA() throws XMLStreamException {
            if (this.xopReferencePresent) {
                return this.base64AttData;
            }
            return this.reader.getText();
        }

        public NamespaceContextEx getNamespaceContext() {
            return new NamespaceContextExAdaper(this.reader.getNamespaceContext());
        }

        public String getElementTextTrim() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getTextLength() {
            if (this.xopReferencePresent) {
                return this.base64AttData.length();
            }
            return this.reader.getTextLength();
        }

        @Override
        public int getTextStart() {
            if (this.xopReferencePresent) {
                return 0;
            }
            return this.reader.getTextStart();
        }

        @Override
        public int getEventType() {
            if (this.xopReferencePresent) {
                return 4;
            }
            return super.getEventType();
        }

        @Override
        public int next() throws XMLStreamException {
            int event = this.reader.next();
            if (event == 1 && this.reader.getLocalName().equals(MtomCodec.XOP_LOCALNAME) && this.reader.getNamespaceURI().equals(MtomCodec.XOP_NAMESPACEURI)) {
                String href = this.reader.getAttributeValue(null, "href");
                try {
                    this.xopHref = href;
                    Attachment att = this.getAttachment(href);
                    if (att != null) {
                        DataHandler dh = att.asDataHandler();
                        if (dh instanceof StreamingDataHandler) {
                            ((StreamingDataHandler)dh).setHrefCid(att.getContentId());
                        }
                        this.base64AttData = new Base64Data();
                        this.base64AttData.set(dh);
                    }
                    this.xopReferencePresent = true;
                }
                catch (IOException e) {
                    throw new WebServiceException((Throwable)e);
                }
                XMLStreamReaderUtil.nextElementContent(this.reader);
                return 4;
            }
            if (this.xopReferencePresent) {
                this.xopReferencePresent = false;
                this.base64EncodedText = null;
                this.xopHref = null;
            }
            return event;
        }

        private String decodeCid(String cid) {
            try {
                cid = URLDecoder.decode(cid, "utf-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
            return cid;
        }

        private Attachment getAttachment(String cid) throws IOException {
            if (cid.startsWith("cid:")) {
                cid = cid.substring(4, cid.length());
            }
            if (cid.indexOf(37) != -1) {
                cid = this.decodeCid(cid);
                return this.mimeMP.getAttachmentPart(cid);
            }
            return this.mimeMP.getAttachmentPart(cid);
        }

        @Override
        public char[] getTextCharacters() {
            if (this.xopReferencePresent) {
                char[] chars = new char[this.base64AttData.length()];
                this.base64AttData.writeTo(chars, 0);
                return chars;
            }
            return this.reader.getTextCharacters();
        }

        @Override
        public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
            if (this.xopReferencePresent) {
                if (target == null) {
                    throw new NullPointerException("target char array can't be null");
                }
                if (targetStart < 0 || length < 0 || sourceStart < 0 || targetStart >= target.length || targetStart + length > target.length) {
                    throw new IndexOutOfBoundsException();
                }
                int textLength = this.base64AttData.length();
                if (sourceStart > textLength) {
                    throw new IndexOutOfBoundsException();
                }
                if (this.base64EncodedText == null) {
                    this.base64EncodedText = new char[this.base64AttData.length()];
                    this.base64AttData.writeTo(this.base64EncodedText, 0);
                }
                int copiedLength = Math.min(textLength - sourceStart, length);
                System.arraycopy(this.base64EncodedText, sourceStart, target, targetStart, copiedLength);
                return copiedLength;
            }
            return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
        }

        @Override
        public String getText() {
            if (this.xopReferencePresent) {
                return this.base64AttData.toString();
            }
            return this.reader.getText();
        }

        protected boolean isXopReference() throws XMLStreamException {
            return this.xopReferencePresent;
        }

        protected String getXopHref() {
            return this.xopHref;
        }

        public MimeMultipartParser getMimeMultipartParser() {
            return this.mimeMP;
        }
    }

    public static class MtomStreamWriterImpl
    extends XMLStreamWriterFilter
    implements XMLStreamWriterEx,
    MtomStreamWriter,
    HasEncoding {
        private final List<ByteArrayBuffer> mtomAttachments;
        private final String boundary;
        private final MTOMFeature myMtomFeature;

        public MtomStreamWriterImpl(XMLStreamWriter w, List<ByteArrayBuffer> mtomAttachments, String b, MTOMFeature myMtomFeature) {
            super(w);
            this.mtomAttachments = mtomAttachments;
            this.boundary = b;
            this.myMtomFeature = myMtomFeature;
        }

        public void writeBinary(byte[] data, int start, int len, String contentType) throws XMLStreamException {
            if (this.myMtomFeature.getThreshold() > len) {
                this.writeCharacters(DatatypeConverterImpl._printBase64Binary((byte[])data, (int)start, (int)len));
                return;
            }
            ByteArrayBuffer bab = new ByteArrayBuffer(new DataHandler((DataSource)new ByteArrayDataSource(data, start, len, contentType)), this.boundary);
            this.writeBinary(bab);
        }

        public void writeBinary(DataHandler dataHandler) throws XMLStreamException {
            this.writeBinary(new ByteArrayBuffer(dataHandler, this.boundary));
        }

        public OutputStream writeBinary(String contentType) throws XMLStreamException {
            throw new UnsupportedOperationException();
        }

        public void writePCDATA(CharSequence data) throws XMLStreamException {
            if (data == null) {
                return;
            }
            if (data instanceof Base64Data) {
                Base64Data binaryData = (Base64Data)data;
                this.writeBinary(binaryData.getDataHandler());
                return;
            }
            this.writeCharacters(data.toString());
        }

        private void writeBinary(ByteArrayBuffer bab) {
            try {
                this.mtomAttachments.add(bab);
                String prefix = this.writer.getPrefix(MtomCodec.XOP_NAMESPACEURI);
                if (prefix == null || !prefix.equals("xop")) {
                    this.writer.setPrefix("xop", MtomCodec.XOP_NAMESPACEURI);
                    this.writer.writeNamespace("xop", MtomCodec.XOP_NAMESPACEURI);
                }
                this.writer.writeStartElement(MtomCodec.XOP_NAMESPACEURI, MtomCodec.XOP_LOCALNAME);
                this.writer.writeAttribute("href", "cid:" + bab.contentId);
                this.writer.writeEndElement();
                this.writer.flush();
            }
            catch (XMLStreamException e) {
                throw new WebServiceException((Throwable)e);
            }
        }

        @Override
        public Object getProperty(String name) throws IllegalArgumentException {
            Object obj;
            if (name.equals("sjsxp-outputstream") && this.writer instanceof Map && (obj = ((Map)((Object)this.writer)).get("sjsxp-outputstream")) != null) {
                return obj;
            }
            return super.getProperty(name);
        }

        public AttachmentMarshaller getAttachmentMarshaller() {
            return new AttachmentMarshaller(){

                public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName) {
                    ByteArrayBuffer bab = new ByteArrayBuffer(data, boundary);
                    mtomAttachments.add(bab);
                    return "cid:" + bab.contentId;
                }

                public String addMtomAttachment(byte[] data, int offset, int length, String mimeType, String elementNamespace, String elementLocalName) {
                    if (myMtomFeature.getThreshold() > length) {
                        return null;
                    }
                    ByteArrayBuffer bab = new ByteArrayBuffer(new DataHandler((DataSource)new ByteArrayDataSource(data, offset, length, mimeType)), boundary);
                    mtomAttachments.add(bab);
                    return "cid:" + bab.contentId;
                }

                public String addSwaRefAttachment(DataHandler data) {
                    ByteArrayBuffer bab = new ByteArrayBuffer(data, boundary);
                    mtomAttachments.add(bab);
                    return "cid:" + bab.contentId;
                }

                public boolean isXOPPackage() {
                    return true;
                }
            };
        }

        public List<ByteArrayBuffer> getMtomAttachments() {
            return this.mtomAttachments;
        }

        @Override
        public String getEncoding() {
            return XMLStreamWriterUtil.getEncoding(this.writer);
        }

        public NamespaceContextEx getNamespaceContext() {
            NamespaceContext nsContext = this.writer.getNamespaceContext();
            return new MtomNamespaceContextEx(nsContext);
        }

        private static class MtomNamespaceContextEx
        implements NamespaceContextEx {
            private final NamespaceContext nsContext;

            public MtomNamespaceContextEx(NamespaceContext nsContext) {
                this.nsContext = nsContext;
            }

            public Iterator<NamespaceContextEx.Binding> iterator() {
                throw new UnsupportedOperationException();
            }

            public String getNamespaceURI(String prefix) {
                return this.nsContext.getNamespaceURI(prefix);
            }

            public String getPrefix(String namespaceURI) {
                return this.nsContext.getPrefix(namespaceURI);
            }

            public Iterator getPrefixes(String namespaceURI) {
                return this.nsContext.getPrefixes(namespaceURI);
            }
        }
    }

    public static class ByteArrayBuffer {
        final String contentId;
        private final DataHandler dh;
        private final String boundary;

        ByteArrayBuffer(@NotNull DataHandler dh, String b) {
            StreamingDataHandler sdh;
            this.dh = dh;
            String cid = null;
            if (dh instanceof StreamingDataHandler && (sdh = (StreamingDataHandler)dh).getHrefCid() != null) {
                cid = sdh.getHrefCid();
            }
            this.contentId = cid != null ? cid : MtomCodec.encodeCid();
            this.boundary = b;
        }

        public void write(OutputStream os) throws IOException {
            MimeCodec.writeln("--" + this.boundary, os);
            MtomCodec.writeMimeHeaders(this.dh.getContentType(), this.contentId, os);
            this.dh.writeTo(os);
            MimeCodec.writeln(os);
        }
    }
}

