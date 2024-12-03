/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.fastinfoset.stax.StAXDocumentParser
 *  com.sun.xml.fastinfoset.stax.StAXDocumentSerializer
 *  com.sun.xml.fastinfoset.vocab.ParserVocabulary
 *  com.sun.xml.fastinfoset.vocab.SerializerVocabulary
 *  javax.xml.ws.WebServiceException
 *  org.jvnet.fastinfoset.FastInfosetSource
 */
package com.sun.xml.ws.encoding.fastinfoset;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.ContentType;
import com.sun.xml.ws.encoding.ContentTypeImpl;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamReaderRecyclable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import org.jvnet.fastinfoset.FastInfosetSource;

public class FastInfosetCodec
implements Codec {
    private static final int DEFAULT_INDEXED_STRING_SIZE_LIMIT = 32;
    private static final int DEFAULT_INDEXED_STRING_MEMORY_LIMIT = 0x400000;
    private StAXDocumentParser _parser;
    private StAXDocumentSerializer _serializer;
    private final boolean _retainState;
    private final ContentType _contentType;

    FastInfosetCodec(boolean retainState) {
        this._retainState = retainState;
        this._contentType = retainState ? new ContentTypeImpl("application/vnd.sun.stateful.fastinfoset") : new ContentTypeImpl("application/fastinfoset");
    }

    @Override
    public String getMimeType() {
        return this._contentType.getContentType();
    }

    @Override
    public Codec copy() {
        return new FastInfosetCodec(this._retainState);
    }

    @Override
    public ContentType getStaticContentType(Packet packet) {
        return this._contentType;
    }

    @Override
    public ContentType encode(Packet packet, OutputStream out) {
        Message message = packet.getMessage();
        if (message != null && message.hasPayload()) {
            XMLStreamWriter writer = this.getXMLStreamWriter(out);
            try {
                writer.writeStartDocument();
                packet.getMessage().writePayloadTo(writer);
                writer.writeEndDocument();
                writer.flush();
            }
            catch (XMLStreamException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
        return this._contentType;
    }

    @Override
    public ContentType encode(Packet packet, WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(InputStream in, String contentType, Packet packet) throws IOException {
        Message message = (in = FastInfosetCodec.hasSomeData(in)) != null ? Messages.createUsingPayload((Source)new FastInfosetSource(in), SOAPVersion.SOAP_11) : Messages.createEmpty(SOAPVersion.SOAP_11);
        packet.setMessage(message);
    }

    @Override
    public void decode(ReadableByteChannel in, String contentType, Packet response) {
        throw new UnsupportedOperationException();
    }

    private XMLStreamWriter getXMLStreamWriter(OutputStream out) {
        if (this._serializer != null) {
            this._serializer.setOutputStream(out);
            return this._serializer;
        }
        this._serializer = FastInfosetCodec.createNewStreamWriter(out, this._retainState);
        return this._serializer;
    }

    public static FastInfosetCodec create() {
        return FastInfosetCodec.create(false);
    }

    public static FastInfosetCodec create(boolean retainState) {
        return new FastInfosetCodec(retainState);
    }

    static StAXDocumentSerializer createNewStreamWriter(OutputStream out, boolean retainState) {
        return FastInfosetCodec.createNewStreamWriter(out, retainState, 32, 0x400000);
    }

    static StAXDocumentSerializer createNewStreamWriter(OutputStream out, boolean retainState, int indexedStringSizeLimit, int stringsMemoryLimit) {
        StAXDocumentSerializer serializer = new StAXDocumentSerializer(out);
        if (retainState) {
            SerializerVocabulary vocabulary = new SerializerVocabulary();
            serializer.setVocabulary(vocabulary);
            serializer.setMinAttributeValueSize(0);
            serializer.setMaxAttributeValueSize(indexedStringSizeLimit);
            serializer.setMinCharacterContentChunkSize(0);
            serializer.setMaxCharacterContentChunkSize(indexedStringSizeLimit);
            serializer.setAttributeValueMapMemoryLimit(stringsMemoryLimit);
            serializer.setCharacterContentChunkMapMemoryLimit(stringsMemoryLimit);
        }
        return serializer;
    }

    static StAXDocumentParser createNewStreamReader(InputStream in, boolean retainState) {
        StAXDocumentParser parser = new StAXDocumentParser(in);
        parser.setStringInterning(true);
        if (retainState) {
            ParserVocabulary vocabulary = new ParserVocabulary();
            parser.setVocabulary(vocabulary);
        }
        return parser;
    }

    static StAXDocumentParser createNewStreamReaderRecyclable(InputStream in, boolean retainState) {
        FastInfosetStreamReaderRecyclable parser = new FastInfosetStreamReaderRecyclable(in);
        parser.setStringInterning(true);
        parser.setForceStreamClose(true);
        if (retainState) {
            ParserVocabulary vocabulary = new ParserVocabulary();
            parser.setVocabulary(vocabulary);
        }
        return parser;
    }

    private static InputStream hasSomeData(InputStream in) throws IOException {
        if (in != null && in.available() < 1) {
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            in.mark(1);
            if (in.read() != -1) {
                in.reset();
            } else {
                in = null;
            }
        }
        return in;
    }
}

