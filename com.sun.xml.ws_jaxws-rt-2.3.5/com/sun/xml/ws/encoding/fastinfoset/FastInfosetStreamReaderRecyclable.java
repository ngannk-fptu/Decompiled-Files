/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.fastinfoset.stax.StAXDocumentParser
 */
package com.sun.xml.ws.encoding.fastinfoset;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.encoding.fastinfoset.FastInfosetStreamReaderFactory;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public final class FastInfosetStreamReaderRecyclable
extends StAXDocumentParser
implements XMLStreamReaderFactory.RecycleAware {
    private static final FastInfosetStreamReaderFactory READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();

    public FastInfosetStreamReaderRecyclable() {
    }

    public FastInfosetStreamReaderRecyclable(InputStream in) {
        super(in);
    }

    @Override
    public void onRecycled() {
        READER_FACTORY.doRecycle((XMLStreamReader)((Object)this));
    }
}

