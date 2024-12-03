/*
 * Decompiled with CFR 0.152.
 */
package uk.org.xml.sax;

import java.io.Writer;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;

public interface DocumentHandler
extends org.xml.sax.DocumentHandler {
    public Writer startDocument(Writer var1) throws SAXException;

    public Writer startElement(String var1, AttributeList var2, Writer var3) throws SAXException;
}

