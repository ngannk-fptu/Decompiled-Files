/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding;

import com.oracle.webservices.impl.encoding.StreamDecoderImpl;
import com.oracle.webservices.impl.internalspi.encoding.StreamDecoder;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.developer.SerializationFeature;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.RootOnlyCodec;
import com.sun.xml.ws.encoding.StreamSOAP11Codec;
import com.sun.xml.ws.encoding.StreamSOAP12Codec;
import com.sun.xml.ws.message.AttachmentSetImpl;
import com.sun.xml.ws.message.stream.StreamMessage;
import com.sun.xml.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.ws.server.UnsupportedMediaException;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class StreamSOAPCodec
implements com.sun.xml.ws.api.pipe.StreamSOAPCodec,
RootOnlyCodec {
    private static final String SOAP_ENVELOPE = "Envelope";
    private static final String SOAP_HEADER = "Header";
    private static final String SOAP_BODY = "Body";
    private final SOAPVersion soapVersion;
    protected final SerializationFeature serializationFeature;
    private final StreamDecoder streamDecoder;
    private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";

    StreamSOAPCodec(SOAPVersion soapVersion) {
        this(soapVersion, null);
    }

    StreamSOAPCodec(WSBinding binding) {
        this(binding.getSOAPVersion(), binding.getFeature(SerializationFeature.class));
    }

    StreamSOAPCodec(WSFeatureList features) {
        this(WebServiceFeatureList.getSoapVersion(features), features.get(SerializationFeature.class));
    }

    private StreamSOAPCodec(SOAPVersion soapVersion, @Nullable SerializationFeature sf) {
        this.soapVersion = soapVersion;
        this.serializationFeature = sf;
        this.streamDecoder = this.selectStreamDecoder();
    }

    private StreamDecoder selectStreamDecoder() {
        Iterator<StreamDecoder> iterator = ServiceFinder.find(StreamDecoder.class).iterator();
        if (iterator.hasNext()) {
            StreamDecoder sd = iterator.next();
            return sd;
        }
        return new StreamDecoderImpl();
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        return this.getContentType(packet);
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) {
        if (packet.getMessage() != null) {
            String encoding = this.getPacketEncoding(packet);
            packet.invocationProperties.remove(DECODED_MESSAGE_CHARSET);
            XMLStreamWriter writer = XMLStreamWriterFactory.create(out, encoding);
            try {
                packet.getMessage().writeTo(writer);
                writer.flush();
            }
            catch (XMLStreamException e) {
                throw new WebServiceException((Throwable)e);
            }
            XMLStreamWriterFactory.recycle(writer);
        }
        return this.getContentType(packet);
    }

    protected abstract ContentType getContentType(Packet var1);

    protected abstract String getDefaultContentType();

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    protected abstract List<String> getExpectedContentTypes();

    @Override
    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        this.decode(in, contentType, packet, (AttachmentSet)new AttachmentSetImpl());
    }

    private static boolean isContentTypeSupported(String ct, List<String> expected) {
        for (String contentType : expected) {
            if (!ct.contains(contentType)) continue;
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public final Message decode(@NotNull XMLStreamReader reader) {
        return this.decode(reader, new AttachmentSetImpl());
    }

    @Override
    public final Message decode(XMLStreamReader reader, @NotNull AttachmentSet attachmentSet) {
        return StreamSOAPCodec.decode(this.soapVersion, reader, attachmentSet);
    }

    public static final Message decode(SOAPVersion soapVersion, XMLStreamReader reader, @NotNull AttachmentSet attachmentSet) {
        if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        XMLStreamReaderUtil.verifyReaderState(reader, 1);
        if (SOAP_ENVELOPE.equals(reader.getLocalName()) && !soapVersion.nsUri.equals(reader.getNamespaceURI())) {
            throw new VersionMismatchException(soapVersion, soapVersion.nsUri, reader.getNamespaceURI());
        }
        XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, SOAP_ENVELOPE);
        return new StreamMessage(soapVersion, reader, attachmentSet);
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet packet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final StreamSOAPCodec copy() {
        return this;
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet, AttachmentSet att) throws IOException {
        List<String> expectedContentTypes = this.getExpectedContentTypes();
        if (contentType != null && !StreamSOAPCodec.isContentTypeSupported(contentType, expectedContentTypes)) {
            throw new UnsupportedMediaException(contentType, expectedContentTypes);
        }
        com.oracle.webservices.api.message.ContentType pct = packet.getInternalContentType();
        ContentTypeImpl cti = pct != null && pct instanceof ContentTypeImpl ? (ContentTypeImpl)pct : new ContentTypeImpl(contentType);
        String charset = cti.getCharSet();
        if (charset != null && !Charset.isSupported(charset)) {
            throw new UnsupportedMediaException(charset);
        }
        if (charset != null) {
            packet.invocationProperties.put(DECODED_MESSAGE_CHARSET, charset);
        } else {
            packet.invocationProperties.remove(DECODED_MESSAGE_CHARSET);
        }
        packet.setMessage(this.streamDecoder.decode(in, charset, att, this.soapVersion));
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet response, AttachmentSet att) {
        throw new UnsupportedOperationException();
    }

    public static StreamSOAPCodec create(SOAPVersion version) {
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new StreamSOAP11Codec();
            }
            case SOAP_12: {
                return new StreamSOAP12Codec();
            }
        }
        throw new AssertionError();
    }

    public static StreamSOAPCodec create(WSFeatureList features) {
        SOAPVersion version = WebServiceFeatureList.getSoapVersion(features);
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new StreamSOAP11Codec(features);
            }
            case SOAP_12: {
                return new StreamSOAP12Codec(features);
            }
        }
        throw new AssertionError();
    }

    public static StreamSOAPCodec create(WSBinding binding) {
        SOAPVersion version = binding.getSOAPVersion();
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new StreamSOAP11Codec(binding);
            }
            case SOAP_12: {
                return new StreamSOAP12Codec(binding);
            }
        }
        throw new AssertionError();
    }

    private String getPacketEncoding(Packet packet) {
        if (this.serializationFeature != null && this.serializationFeature.getEncoding() != null) {
            return this.serializationFeature.getEncoding().equals("") ? "utf-8" : this.serializationFeature.getEncoding();
        }
        if (packet != null && packet.endpoint != null) {
            String charset = (String)packet.invocationProperties.get(DECODED_MESSAGE_CHARSET);
            return charset == null ? "utf-8" : charset;
        }
        return "utf-8";
    }

    protected ContentTypeImpl.Builder getContenTypeBuilder(Packet packet) {
        ContentTypeImpl.Builder b = new ContentTypeImpl.Builder();
        String encoding = this.getPacketEncoding(packet);
        if ("utf-8".equalsIgnoreCase(encoding)) {
            b.contentType = this.getDefaultContentType();
            b.charset = "utf-8";
            return b;
        }
        b.contentType = this.getMimeType() + " ;charset=" + encoding;
        b.charset = encoding;
        return b;
    }
}

