/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import javax.xml.stream.XMLStreamReader;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractStAXStreamReader;
import org.jdom2.output.support.StAXStreamReaderProcessor;

public class AbstractStAXStreamReaderProcessor
implements StAXStreamReaderProcessor {
    public XMLStreamReader buildReader(Document doc, Format format) {
        return new DefaultXMLStreamReader(doc, format);
    }

    private static final class DefaultXMLStreamReader
    extends AbstractStAXStreamReader {
        public DefaultXMLStreamReader(Document doc, Format format) {
            super(doc, format);
        }
    }
}

