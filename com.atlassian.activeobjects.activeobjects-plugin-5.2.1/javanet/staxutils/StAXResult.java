/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.ContentHandlerToXMLEventWriter;
import javanet.staxutils.ContentHandlerToXMLStreamWriter;
import javax.xml.stream.XMLEventWriter;
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

    public StAXResult(XMLEventWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        super.setHandler(new ContentHandlerToXMLEventWriter(writer));
    }
}

