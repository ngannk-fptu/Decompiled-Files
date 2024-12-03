/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.util;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.util.StAXParserWrapper;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXFilteredParser
extends StAXParserWrapper {
    private StreamFilter _filter;

    public StAXFilteredParser() {
    }

    public StAXFilteredParser(XMLStreamReader reader, StreamFilter filter) {
        super(reader);
        this._filter = filter;
    }

    public void setFilter(StreamFilter filter) {
        this._filter = filter;
    }

    @Override
    public int next() throws XMLStreamException {
        if (this.hasNext()) {
            return super.next();
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.noMoreItems"));
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        while (super.hasNext()) {
            if (this._filter.accept(this.getReader())) {
                return true;
            }
            super.next();
        }
        return false;
    }
}

