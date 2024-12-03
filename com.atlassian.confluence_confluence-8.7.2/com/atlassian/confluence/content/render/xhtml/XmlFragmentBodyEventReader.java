/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XmlFragmentEventReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class XmlFragmentBodyEventReader
extends XmlFragmentEventReader {
    XmlFragmentBodyEventReader(XMLEventReader delegate) throws XMLStreamException {
        super(delegate);
        if (super.hasNext()) {
            super.nextEvent();
        }
    }

    @Override
    public boolean hasNext() {
        try {
            if (this.count == 1 && this.delegate.hasNext() && this.delegate.peek().isEndElement()) {
                return false;
            }
            return super.hasNext();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        super.close();
        if (this.delegate.hasNext()) {
            this.delegate.nextEvent();
        }
    }
}

