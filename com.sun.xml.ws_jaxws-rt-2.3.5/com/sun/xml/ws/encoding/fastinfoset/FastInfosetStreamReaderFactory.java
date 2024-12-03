/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.fastinfoset.stax.StAXDocumentParser
 */
package com.sun.xml.ws.encoding.fastinfoset;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetCodec;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLStreamReader;

public final class FastInfosetStreamReaderFactory
extends XMLStreamReaderFactory {
    private static final FastInfosetStreamReaderFactory factory = new FastInfosetStreamReaderFactory();
    private ThreadLocal<StAXDocumentParser> pool = new ThreadLocal();

    public static FastInfosetStreamReaderFactory getInstance() {
        return factory;
    }

    @Override
    public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
        StAXDocumentParser parser = this.fetch();
        if (parser == null) {
            return FastInfosetCodec.createNewStreamReaderRecyclable(in, false);
        }
        parser.setInputStream(in);
        return parser;
    }

    @Override
    public XMLStreamReader doCreate(String systemId, Reader reader, boolean rejectDTDs) {
        throw new UnsupportedOperationException();
    }

    private StAXDocumentParser fetch() {
        StAXDocumentParser parser = this.pool.get();
        this.pool.set(null);
        return parser;
    }

    @Override
    public void doRecycle(XMLStreamReader r) {
        if (r instanceof StAXDocumentParser) {
            this.pool.set((StAXDocumentParser)r);
        }
    }
}

