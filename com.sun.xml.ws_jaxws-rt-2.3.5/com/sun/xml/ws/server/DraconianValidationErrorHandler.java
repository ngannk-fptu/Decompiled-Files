/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.server;

import com.sun.xml.ws.developer.ValidationErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DraconianValidationErrorHandler
extends ValidationErrorHandler {
    @Override
    public void warning(SAXParseException e) throws SAXException {
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }
}

