/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.ext.stax;

import javax.xml.stream.XMLStreamReader;

public interface DelegatingXMLStreamReader
extends XMLStreamReader {
    public XMLStreamReader getParent();
}

