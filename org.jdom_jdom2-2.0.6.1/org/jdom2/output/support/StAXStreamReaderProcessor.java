/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import javax.xml.stream.XMLStreamReader;
import org.jdom2.Document;
import org.jdom2.output.Format;

public interface StAXStreamReaderProcessor {
    public XMLStreamReader buildReader(Document var1, Format var2);
}

