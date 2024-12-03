/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.StreamSOAPCodec;
import java.util.Collections;
import java.util.List;

final class StreamSOAP11Codec
extends StreamSOAPCodec {
    public static final String SOAP11_MIME_TYPE = "text/xml";
    public static final String DEFAULT_SOAP11_CONTENT_TYPE = "text/xml; charset=utf-8";
    private static final List<String> EXPECTED_CONTENT_TYPES = Collections.singletonList("text/xml");

    StreamSOAP11Codec() {
        super(SOAPVersion.SOAP_11);
    }

    StreamSOAP11Codec(WSBinding binding) {
        super(binding);
    }

    StreamSOAP11Codec(WSFeatureList features) {
        super(features);
    }

    @Override
    public String getMimeType() {
        return SOAP11_MIME_TYPE;
    }

    @Override
    protected ContentType getContentType(Packet packet) {
        ContentTypeImpl.Builder b = this.getContenTypeBuilder(packet);
        b.soapAction = packet.soapAction;
        return b.build();
    }

    @Override
    protected String getDefaultContentType() {
        return DEFAULT_SOAP11_CONTENT_TYPE;
    }

    @Override
    protected List<String> getExpectedContentTypes() {
        return EXPECTED_CONTENT_TYPES;
    }
}

