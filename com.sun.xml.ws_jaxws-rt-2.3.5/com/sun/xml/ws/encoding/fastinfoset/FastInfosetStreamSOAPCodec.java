/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.fastinfoset.stax.StAXDocumentParser
 *  com.sun.xml.fastinfoset.stax.StAXDocumentSerializer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.encoding.fastinfoset;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetCodec;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamReaderFactory;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamSOAP11Codec;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamSOAP12Codec;
import com.sun.xml.ws.message.stream.StreamHeader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class FastInfosetStreamSOAPCodec
implements Codec {
    private static final FastInfosetStreamReaderFactory READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();
    private StAXDocumentParser _statefulParser;
    private StAXDocumentSerializer _serializer;
    private final StreamSOAPCodec _soapCodec;
    private final boolean _retainState;
    protected final ContentType _defaultContentType;

    FastInfosetStreamSOAPCodec(StreamSOAPCodec soapCodec, SOAPVersion soapVersion, boolean retainState, String mimeType) {
        this._soapCodec = soapCodec;
        this._retainState = retainState;
        this._defaultContentType = new ContentTypeImpl(mimeType);
    }

    FastInfosetStreamSOAPCodec(FastInfosetStreamSOAPCodec that) {
        this._soapCodec = (StreamSOAPCodec)that._soapCodec.copy();
        this._retainState = that._retainState;
        this._defaultContentType = that._defaultContentType;
    }

    @Override
    public String getMimeType() {
        return this._defaultContentType.getContentType();
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        return this.getContentType(packet.soapAction);
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) {
        if (packet.getMessage() != null) {
            XMLStreamWriter writer = this.getXMLStreamWriter(out);
            try {
                packet.getMessage().writeTo(writer);
                writer.flush();
            }
            catch (XMLStreamException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
        return this.getContentType(packet.soapAction);
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(InputStream in, String contentType, Packet response) throws IOException {
        response.setMessage(this._soapCodec.decode(this.getXMLStreamReader(in)));
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet response) {
        throw new UnsupportedOperationException();
    }

    protected abstract StreamHeader createHeader(XMLStreamReader var1, XMLStreamBuffer var2);

    protected abstract ContentType getContentType(String var1);

    private XMLStreamWriter getXMLStreamWriter(OutputStream out) {
        if (this._serializer != null) {
            this._serializer.setOutputStream(out);
            return this._serializer;
        }
        this._serializer = FastInfosetCodec.createNewStreamWriter(out, this._retainState);
        return this._serializer;
    }

    private XMLStreamReader getXMLStreamReader(InputStream in) {
        if (this._retainState) {
            if (this._statefulParser != null) {
                this._statefulParser.setInputStream(in);
                return this._statefulParser;
            }
            this._statefulParser = FastInfosetCodec.createNewStreamReader(in, this._retainState);
            return this._statefulParser;
        }
        return READER_FACTORY.doCreate(null, in, false);
    }

    public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec soapCodec, SOAPVersion version) {
        return FastInfosetStreamSOAPCodec.create(soapCodec, version, false);
    }

    public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec soapCodec, SOAPVersion version, boolean retainState) {
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new FastInfosetStreamSOAP11Codec(soapCodec, retainState);
            }
            case SOAP_12: {
                return new FastInfosetStreamSOAP12Codec(soapCodec, retainState);
            }
        }
        throw new AssertionError();
    }
}

