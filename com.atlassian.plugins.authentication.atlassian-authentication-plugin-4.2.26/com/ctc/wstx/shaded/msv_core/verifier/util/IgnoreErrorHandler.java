/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class IgnoreErrorHandler
implements ErrorHandler {
    public void fatalError(SAXParseException e) throws SAXParseException {
        throw e;
    }

    public void error(SAXParseException error) {
    }

    public void warning(SAXParseException warning) {
    }
}

