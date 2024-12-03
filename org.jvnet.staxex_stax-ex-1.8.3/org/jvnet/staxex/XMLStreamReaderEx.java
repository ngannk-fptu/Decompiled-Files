/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.staxex;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.NamespaceContextEx;

public interface XMLStreamReaderEx
extends XMLStreamReader {
    public CharSequence getPCDATA() throws XMLStreamException;

    @Override
    public NamespaceContextEx getNamespaceContext();

    public String getElementTextTrim() throws XMLStreamException;
}

