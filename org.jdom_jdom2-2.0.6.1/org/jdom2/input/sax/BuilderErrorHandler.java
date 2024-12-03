/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class BuilderErrorHandler
implements ErrorHandler {
    public void warning(SAXParseException exception) throws SAXException {
    }

    public void error(SAXParseException exception) throws SAXException {
        throw exception;
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }
}

