/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 */
package com.sun.xml.ws.encoding.fastinfoset;

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec;
import com.sun.xml.ws.message.stream.StreamHeader;
import com.sun.xml.ws.message.stream.StreamHeader11;
import javax.xml.stream.XMLStreamReader;

final class FastInfosetStreamSOAP11Codec
extends FastInfosetStreamSOAPCodec {
    FastInfosetStreamSOAP11Codec(StreamSOAPCodec soapCodec, boolean retainState) {
        super(soapCodec, SOAPVersion.SOAP_11, retainState, retainState ? "application/vnd.sun.stateful.fastinfoset" : "application/fastinfoset");
    }

    private FastInfosetStreamSOAP11Codec(FastInfosetStreamSOAP11Codec that) {
        super(that);
    }

    @Override
    public Codec copy() {
        return new FastInfosetStreamSOAP11Codec(this);
    }

    @Override
    protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        return new StreamHeader11(reader, mark);
    }

    @Override
    protected ContentType getContentType(String soapAction) {
        if (soapAction == null || soapAction.length() == 0) {
            return this._defaultContentType;
        }
        return new ContentTypeImpl(this._defaultContentType.getContentType(), soapAction);
    }
}

