/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.SOAPBindingCodec;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.encoding.XMLHTTPBindingCodec;

public abstract class Codecs {
    @NotNull
    public static SOAPBindingCodec createSOAPBindingCodec(WSFeatureList feature) {
        return new com.sun.xml.ws.encoding.SOAPBindingCodec(feature);
    }

    @NotNull
    public static Codec createXMLCodec(WSFeatureList feature) {
        return new XMLHTTPBindingCodec(feature);
    }

    @NotNull
    public static SOAPBindingCodec createSOAPBindingCodec(WSBinding binding, StreamSOAPCodec xmlEnvelopeCodec) {
        return new com.sun.xml.ws.encoding.SOAPBindingCodec(binding.getFeatures(), xmlEnvelopeCodec);
    }

    @NotNull
    public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull SOAPVersion version) {
        return com.sun.xml.ws.encoding.StreamSOAPCodec.create(version);
    }

    @NotNull
    public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSBinding binding) {
        return com.sun.xml.ws.encoding.StreamSOAPCodec.create(binding);
    }

    @NotNull
    public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull WSFeatureList features) {
        return com.sun.xml.ws.encoding.StreamSOAPCodec.create(features);
    }
}

