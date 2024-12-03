/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public interface ErrorHandler {
    public void warning(SAXParseException var1) throws SAXException;

    public void error(SAXParseException var1) throws SAXException;

    public void fatalError(SAXParseException var1) throws SAXException;
}

