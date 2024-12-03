/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.server;

import com.sun.xml.ws.api.server.SDDocument;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface SDDocumentFilter {
    public XMLStreamWriter filter(SDDocument var1, XMLStreamWriter var2) throws XMLStreamException, IOException;
}

