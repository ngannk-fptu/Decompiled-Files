/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import org.codehaus.stax2.util.StreamReader2Delegate;

public class Stax2FilteredStreamReader
extends StreamReader2Delegate
implements XMLStreamConstants {
    final StreamFilter mFilter;

    public Stax2FilteredStreamReader(XMLStreamReader r, StreamFilter f) {
        super(Stax2ReaderAdapter.wrapIfNecessary(r));
        this.mFilter = f;
    }

    @Override
    public int next() throws XMLStreamException {
        int type;
        do {
            type = this._delegate2.next();
        } while (!this.mFilter.accept(this) && type != 8);
        return type;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        int type;
        do {
            type = this._delegate2.nextTag();
        } while (!this.mFilter.accept(this));
        return type;
    }
}

