/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util.xml;

import com.sun.xml.ws.util.xml.ContentHandlerToXMLStreamWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

public class StAXResult
extends SAXResult {
    public StAXResult(XMLStreamWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        super.setHandler(new ContentHandlerToXMLStreamWriter(writer));
    }
}

