/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.client.ContentNegotiation;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.MimeCodec;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import com.sun.xml.ws.encoding.xml.XMLCodec;
import com.sun.xml.ws.encoding.xml.XMLMessage;
import com.sun.xml.ws.resources.StreamingMessages;
import com.sun.xml.ws.util.ByteArrayBuffer;
import com.sun.xml.ws.util.FastInfosetUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.StringTokenizer;
import javax.activation.DataSource;
import javax.xml.ws.WebServiceException;

public final class XMLHTTPBindingCodec
extends MimeCodec {
    private static final String BASE_ACCEPT_VALUE = "*";
    private static final String APPLICATION_FAST_INFOSET_MIME_TYPE = "application/fastinfoset";
    private boolean useFastInfosetForEncoding;
    private final Codec xmlCodec;
    private final Codec fiCodec;
    private static final String xmlAccept = null;
    private static final String fiXmlAccept = "application/fastinfoset, *";

    private ContentTypeImpl setAcceptHeader(Packet p, ContentType c) {
        ContentTypeImpl ctImpl = (ContentTypeImpl)c;
        if (p.contentNegotiation == ContentNegotiation.optimistic || p.contentNegotiation == ContentNegotiation.pessimistic) {
            ctImpl.setAcceptHeader(fiXmlAccept);
        } else {
            ctImpl.setAcceptHeader(xmlAccept);
        }
        p.setContentType(ctImpl);
        return ctImpl;
    }

    public XMLHTTPBindingCodec(WSFeatureList f) {
        super(SOAPVersion.SOAP_11, f);
        this.xmlCodec = new XMLCodec(f);
        this.fiCodec = FastInfosetUtil.getFICodec();
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        XMLMessage.MessageDataSource mds;
        if (packet.getInternalMessage() instanceof XMLMessage.MessageDataSource && (mds = (XMLMessage.MessageDataSource)((Object)packet.getInternalMessage())).hasUnconsumedDataSource()) {
            ContentType ct = this.getStaticContentType(mds);
            return ct != null ? this.setAcceptHeader(packet, ct) : null;
        }
        ContentType ct = super.getStaticContentType(packet);
        return ct != null ? this.setAcceptHeader(packet, ct) : null;
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) throws IOException {
        XMLMessage.MessageDataSource mds;
        if (packet.getInternalMessage() instanceof XMLMessage.MessageDataSource && (mds = (XMLMessage.MessageDataSource)((Object)packet.getInternalMessage())).hasUnconsumedDataSource()) {
            return this.setAcceptHeader(packet, this.encode(mds, out));
        }
        return this.setAcceptHeader(packet, super.encode(packet, out));
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        if (packet.contentNegotiation == null) {
            this.useFastInfosetForEncoding = false;
        }
        if (contentType == null) {
            this.xmlCodec.decode(in, contentType, packet);
        } else if (this.isMultipartRelated(contentType)) {
            packet.setMessage(new XMLMessage.XMLMultiPart(contentType, in, this.features));
        } else if (this.isFastInfoset(contentType)) {
            if (this.fiCodec == null) {
                throw new RuntimeException(StreamingMessages.FASTINFOSET_NO_IMPLEMENTATION());
            }
            this.useFastInfosetForEncoding = true;
            this.fiCodec.decode(in, contentType, packet);
        } else if (this.isXml(contentType)) {
            this.xmlCodec.decode(in, contentType, packet);
        } else {
            packet.setMessage(new XMLMessage.UnknownContent(contentType, in));
        }
        if (!this.useFastInfosetForEncoding) {
            this.useFastInfosetForEncoding = this.isFastInfosetAcceptable(packet.acceptableMimeTypes);
        }
    }

    @Override
    protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
    }

    @Override
    public MimeCodec copy() {
        return new XMLHTTPBindingCodec(this.features);
    }

    private boolean isMultipartRelated(String contentType) {
        return this.compareStrings(contentType, "multipart/related");
    }

    private boolean isXml(String contentType) {
        return this.compareStrings(contentType, "application/xml") || this.compareStrings(contentType, "text/xml") || this.compareStrings(contentType, "application/") && contentType.toLowerCase().contains("+xml");
    }

    private boolean isFastInfoset(String contentType) {
        return this.compareStrings(contentType, APPLICATION_FAST_INFOSET_MIME_TYPE);
    }

    private boolean compareStrings(String a, String b) {
        return a.length() >= b.length() && b.equalsIgnoreCase(a.substring(0, b.length()));
    }

    private boolean isFastInfosetAcceptable(String accept) {
        if (accept == null) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(accept, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (!token.equalsIgnoreCase(APPLICATION_FAST_INFOSET_MIME_TYPE)) continue;
            return true;
        }
        return false;
    }

    private ContentType getStaticContentType(XMLMessage.MessageDataSource mds) {
        String contentType = mds.getDataSource().getContentType();
        boolean isFastInfoset = XMLMessage.isFastInfoset(contentType);
        if (!XMLHTTPBindingCodec.requiresTransformationOfDataSource(isFastInfoset, this.useFastInfosetForEncoding)) {
            return new ContentTypeImpl(contentType);
        }
        return null;
    }

    private ContentType encode(XMLMessage.MessageDataSource mds, OutputStream out) {
        try {
            int count;
            boolean isFastInfoset = XMLMessage.isFastInfoset(mds.getDataSource().getContentType());
            DataSource ds = XMLHTTPBindingCodec.transformDataSource(mds.getDataSource(), isFastInfoset, this.useFastInfosetForEncoding, this.features);
            InputStream is = ds.getInputStream();
            byte[] buf = new byte[1024];
            while ((count = is.read(buf)) != -1) {
                out.write(buf, 0, count);
            }
            return new ContentTypeImpl(ds.getContentType());
        }
        catch (IOException ioe) {
            throw new WebServiceException((Throwable)ioe);
        }
    }

    @Override
    protected Codec getMimeRootCodec(Packet p) {
        if (p.contentNegotiation == ContentNegotiation.none) {
            this.useFastInfosetForEncoding = false;
        } else if (p.contentNegotiation == ContentNegotiation.optimistic) {
            this.useFastInfosetForEncoding = true;
        }
        return this.useFastInfosetForEncoding && this.fiCodec != null ? this.fiCodec : this.xmlCodec;
    }

    public static boolean requiresTransformationOfDataSource(boolean isFastInfoset, boolean useFastInfoset) {
        return isFastInfoset && !useFastInfoset || !isFastInfoset && useFastInfoset;
    }

    public static DataSource transformDataSource(DataSource in, boolean isFastInfoset, boolean useFastInfoset, WSFeatureList f) {
        try {
            if (isFastInfoset && !useFastInfoset) {
                XMLHTTPBindingCodec codec = new XMLHTTPBindingCodec(f);
                Packet p = new Packet();
                codec.decode(in.getInputStream(), in.getContentType(), p);
                p.getMessage().getAttachments();
                codec.getStaticContentType(p);
                ByteArrayBuffer bos = new ByteArrayBuffer();
                ContentType ct = codec.encode(p, (OutputStream)bos);
                return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
            }
            if (!isFastInfoset && useFastInfoset) {
                XMLHTTPBindingCodec codec = new XMLHTTPBindingCodec(f);
                Packet p = new Packet();
                codec.decode(in.getInputStream(), in.getContentType(), p);
                p.contentNegotiation = ContentNegotiation.optimistic;
                p.getMessage().getAttachments();
                codec.getStaticContentType(p);
                ByteArrayBuffer bos = new ByteArrayBuffer();
                ContentType ct = codec.encode(p, (OutputStream)bos);
                return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
            }
        }
        catch (Exception ex) {
            throw new WebServiceException((Throwable)ex);
        }
        return in;
    }
}

