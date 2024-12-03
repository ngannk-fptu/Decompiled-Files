/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.client.SelectOptimalEncodingFeature;
import com.sun.xml.ws.api.fastinfoset.FastInfosetFeature;
import com.sun.xml.ws.api.message.ExceptionHasMessage;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.Codecs;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.client.ContentNegotiation;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.MimeCodec;
import com.sun.xml.ws.encoding.MimeMultipartParser;
import com.sun.xml.ws.encoding.MtomCodec;
import com.sun.xml.ws.encoding.SwACodec;
import com.sun.xml.ws.protocol.soap.MessageCreationException;
import com.sun.xml.ws.resources.StreamingMessages;
import com.sun.xml.ws.server.UnsupportedMediaException;
import com.sun.xml.ws.util.FastInfosetUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;

public class SOAPBindingCodec
extends MimeCodec
implements com.sun.xml.ws.api.pipe.SOAPBindingCodec {
    public static final String UTF8_ENCODING = "utf-8";
    public static final String DEFAULT_ENCODING = "utf-8";
    private boolean isFastInfosetDisabled;
    private boolean useFastInfosetForEncoding;
    private boolean ignoreContentNegotiationProperty;
    private final StreamSOAPCodec xmlSoapCodec;
    private final Codec fiSoapCodec;
    private final MimeCodec xmlMtomCodec;
    private final MimeCodec xmlSwaCodec;
    private final MimeCodec fiSwaCodec;
    private final String xmlMimeType;
    private final String fiMimeType;
    private final String xmlAccept;
    private final String connegXmlAccept;

    @Override
    public StreamSOAPCodec getXMLCodec() {
        return this.xmlSoapCodec;
    }

    private ContentTypeImpl setAcceptHeader(Packet p, ContentTypeImpl c) {
        String _accept = !this.ignoreContentNegotiationProperty && p.contentNegotiation != ContentNegotiation.none ? this.connegXmlAccept : this.xmlAccept;
        c.setAcceptHeader(_accept);
        return c;
    }

    public SOAPBindingCodec(WSFeatureList features) {
        this(features, Codecs.createSOAPEnvelopeXmlCodec(features));
    }

    public SOAPBindingCodec(WSFeatureList features, StreamSOAPCodec xmlSoapCodec) {
        super(WebServiceFeatureList.getSoapVersion(features), features);
        this.xmlSoapCodec = xmlSoapCodec;
        this.xmlMimeType = xmlSoapCodec.getMimeType();
        this.xmlMtomCodec = new MtomCodec(this.version, xmlSoapCodec, features);
        this.xmlSwaCodec = new SwACodec(this.version, features, xmlSoapCodec);
        String clientAcceptedContentTypes = xmlSoapCodec.getMimeType() + ", " + this.xmlMtomCodec.getMimeType();
        FastInfosetFeature fi = features.get(FastInfosetFeature.class);
        boolean bl = this.isFastInfosetDisabled = fi != null && !fi.isEnabled();
        if (!this.isFastInfosetDisabled) {
            this.fiSoapCodec = FastInfosetUtil.getFICodec(xmlSoapCodec, this.version);
            if (this.fiSoapCodec != null) {
                this.fiMimeType = this.fiSoapCodec.getMimeType();
                this.fiSwaCodec = new SwACodec(this.version, features, this.fiSoapCodec);
                this.connegXmlAccept = this.fiMimeType + ", " + clientAcceptedContentTypes;
                SelectOptimalEncodingFeature select = features.get(SelectOptimalEncodingFeature.class);
                if (select != null) {
                    this.ignoreContentNegotiationProperty = true;
                    if (select.isEnabled()) {
                        if (fi != null) {
                            this.useFastInfosetForEncoding = true;
                        }
                        clientAcceptedContentTypes = this.connegXmlAccept;
                    } else {
                        this.isFastInfosetDisabled = true;
                    }
                }
            } else {
                this.isFastInfosetDisabled = true;
                this.fiSwaCodec = null;
                this.fiMimeType = "";
                this.connegXmlAccept = clientAcceptedContentTypes;
                this.ignoreContentNegotiationProperty = true;
            }
        } else {
            this.fiSwaCodec = null;
            this.fiSoapCodec = null;
            this.fiMimeType = "";
            this.connegXmlAccept = clientAcceptedContentTypes;
            this.ignoreContentNegotiationProperty = true;
        }
        this.xmlAccept = clientAcceptedContentTypes;
        if (WebServiceFeatureList.getSoapVersion(features) == null) {
            throw new WebServiceException("Expecting a SOAP binding but found ");
        }
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        ContentType toAdapt = this.getEncoder(packet).getStaticContentType(packet);
        return this.setAcceptHeader(packet, (ContentTypeImpl)toAdapt);
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) throws IOException {
        this.preEncode(packet);
        ContentType ct = this.getEncoder(packet).encode(packet, out);
        ct = this.setAcceptHeader(packet, (ContentTypeImpl)ct);
        this.postEncode();
        return ct;
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        this.preEncode(packet);
        ContentType ct = this.getEncoder(packet).encode(packet, buffer);
        ct = this.setAcceptHeader(packet, (ContentTypeImpl)ct);
        this.postEncode();
        return ct;
    }

    private void preEncode(Packet p) {
    }

    private void postEncode() {
    }

    private void preDecode(Packet p) {
        if (p.contentNegotiation == null) {
            this.useFastInfosetForEncoding = false;
        }
    }

    private void postDecode(Packet p) {
        MTOMFeature mtomFeature;
        p.setFastInfosetDisabled(this.isFastInfosetDisabled);
        if (this.features.isEnabled(MTOMFeature.class)) {
            p.checkMtomAcceptable();
        }
        if ((mtomFeature = this.features.get(MTOMFeature.class)) != null) {
            p.setMtomFeature(mtomFeature);
        }
        if (!this.useFastInfosetForEncoding) {
            this.useFastInfosetForEncoding = p.getFastInfosetAcceptable(this.fiMimeType);
        }
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        if (contentType == null) {
            contentType = this.xmlMimeType;
        }
        packet.setContentType(new ContentTypeImpl(contentType));
        this.preDecode(packet);
        try {
            if (this.isMultipartRelated(contentType)) {
                super.decode(in, contentType, packet);
            } else if (this.isFastInfoset(contentType)) {
                if (!this.ignoreContentNegotiationProperty && packet.contentNegotiation == ContentNegotiation.none) {
                    throw this.noFastInfosetForDecoding();
                }
                this.useFastInfosetForEncoding = true;
                this.fiSoapCodec.decode(in, contentType, packet);
            } else {
                this.xmlSoapCodec.decode(in, contentType, packet);
            }
        }
        catch (RuntimeException we) {
            if (we instanceof ExceptionHasMessage || we instanceof UnsupportedMediaException) {
                throw we;
            }
            throw new MessageCreationException(this.version, we);
        }
        this.postDecode(packet);
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet packet) {
        if (contentType == null) {
            throw new UnsupportedMediaException();
        }
        this.preDecode(packet);
        try {
            if (this.isMultipartRelated(contentType)) {
                super.decode(in, contentType, packet);
            } else if (this.isFastInfoset(contentType)) {
                if (packet.contentNegotiation == ContentNegotiation.none) {
                    throw this.noFastInfosetForDecoding();
                }
                this.useFastInfosetForEncoding = true;
                this.fiSoapCodec.decode(in, contentType, packet);
            } else {
                this.xmlSoapCodec.decode(in, contentType, packet);
            }
        }
        catch (RuntimeException we) {
            if (we instanceof ExceptionHasMessage || we instanceof UnsupportedMediaException) {
                throw we;
            }
            throw new MessageCreationException(this.version, we);
        }
        this.postDecode(packet);
    }

    @Override
    public SOAPBindingCodec copy() {
        return new SOAPBindingCodec(this.features, (StreamSOAPCodec)this.xmlSoapCodec.copy());
    }

    @Override
    protected void decode(MimeMultipartParser mpp, Packet packet) throws IOException {
        String rootContentType = mpp.getRootPart().getContentType();
        boolean isMTOM = this.isApplicationXopXml(rootContentType);
        packet.setMtomRequest(isMTOM);
        if (isMTOM) {
            this.xmlMtomCodec.decode(mpp, packet);
        } else if (this.isFastInfoset(rootContentType)) {
            if (packet.contentNegotiation == ContentNegotiation.none) {
                throw this.noFastInfosetForDecoding();
            }
            this.useFastInfosetForEncoding = true;
            this.fiSwaCodec.decode(mpp, packet);
        } else if (this.isXml(rootContentType)) {
            this.xmlSwaCodec.decode(mpp, packet);
        } else {
            throw new IOException("");
        }
    }

    private boolean isMultipartRelated(String contentType) {
        return this.compareStrings(contentType, "multipart/related");
    }

    private boolean isApplicationXopXml(String contentType) {
        return this.compareStrings(contentType, "application/xop+xml");
    }

    private boolean isXml(String contentType) {
        return this.compareStrings(contentType, this.xmlMimeType);
    }

    private boolean isFastInfoset(String contentType) {
        if (this.isFastInfosetDisabled) {
            return false;
        }
        return this.compareStrings(contentType, this.fiMimeType);
    }

    private boolean compareStrings(String a, String b) {
        return a.length() >= b.length() && b.equalsIgnoreCase(a.substring(0, b.length()));
    }

    private Codec getEncoder(Packet p) {
        if (!this.ignoreContentNegotiationProperty) {
            if (p.contentNegotiation == ContentNegotiation.none) {
                this.useFastInfosetForEncoding = false;
            } else if (p.contentNegotiation == ContentNegotiation.optimistic) {
                this.useFastInfosetForEncoding = true;
            }
        }
        if (this.useFastInfosetForEncoding) {
            Message m = p.getMessage();
            if (m == null || m.getAttachments().isEmpty() || this.features.isEnabled(MTOMFeature.class)) {
                return this.fiSoapCodec;
            }
            return this.fiSwaCodec;
        }
        if (p.getBinding() == null && this.features != null) {
            p.setMtomFeature(this.features.get(MTOMFeature.class));
        }
        if (p.shouldUseMtom()) {
            return this.xmlMtomCodec;
        }
        Message m = p.getMessage();
        if (m == null || m.getAttachments().isEmpty()) {
            return this.xmlSoapCodec;
        }
        return this.xmlSwaCodec;
    }

    private RuntimeException noFastInfosetForDecoding() {
        return new RuntimeException(StreamingMessages.FASTINFOSET_DECODING_NOT_ACCEPTED());
    }
}

