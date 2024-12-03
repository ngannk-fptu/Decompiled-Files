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

    public Stax2FilteredStreamReader(XMLStreamReader xMLStreamReader, StreamFilter streamFilter) {
        super(Stax2ReaderAdapter.wrapIfNecessary(xMLStreamReader));
        this.mFilter = streamFilter;
    }

    public int next() throws XMLStreamException {
        int n;
        do {
            n = this.mDelegate2.next();
        } while (!this.mFilter.accept(this) && n != 8);
        return n;
    }

    public int nextTag() throws XMLStreamException {
        int n;
        do {
            n = this.mDelegate2.nextTag();
        } while (!this.mFilter.accept(this));
        return n;
    }
}

