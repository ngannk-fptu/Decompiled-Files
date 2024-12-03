/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.stax;

import javax.xml.stream.XMLStreamException;

public interface FastInfosetStreamReader {
    public int peekNext() throws XMLStreamException;

    public int accessNamespaceCount();

    public String accessLocalName();

    public String accessNamespaceURI();

    public String accessPrefix();

    public char[] accessTextCharacters();

    public int accessTextStart();

    public int accessTextLength();
}

