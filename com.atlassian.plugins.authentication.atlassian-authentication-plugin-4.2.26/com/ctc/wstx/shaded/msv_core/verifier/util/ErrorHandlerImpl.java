/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class ErrorHandlerImpl
implements ErrorHandler {
    public static final ErrorHandler theInstance = new ErrorHandlerImpl();

    public void fatalError(SAXParseException error) throws SAXParseException {
        throw error;
    }

    public void error(SAXParseException error) throws SAXParseException {
        throw error;
    }

    public void warning(SAXParseException warning) {
    }
}

