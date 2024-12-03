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
import com.sun.xml.ws.message.stream.StreamHeader12;
import javax.xml.stream.XMLStreamReader;

final class FastInfosetStreamSOAP12Codec
extends FastInfosetStreamSOAPCodec {
    FastInfosetStreamSOAP12Codec(StreamSOAPCodec soapCodec, boolean retainState) {
        super(soapCodec, SOAPVersion.SOAP_12, retainState, retainState ? "application/vnd.sun.stateful.soap+fastinfoset" : "application/soap+fastinfoset");
    }

    private FastInfosetStreamSOAP12Codec(FastInfosetStreamSOAPCodec that) {
        super(that);
    }

    @Override
    public Codec copy() {
        return new FastInfosetStreamSOAP12Codec(this);
    }

    @Override
    protected final StreamHeader createHeader(XMLStreamReader reader, XMLStreamBuffer mark) {
        return new StreamHeader12(reader, mark);
    }

    @Override
    protected ContentType getContentType(String soapAction) {
        if (soapAction == null) {
            return this._defaultContentType;
        }
        return new ContentTypeImpl(this._defaultContentType.getContentType() + ";action=\"" + soapAction + "\"");
    }
}

