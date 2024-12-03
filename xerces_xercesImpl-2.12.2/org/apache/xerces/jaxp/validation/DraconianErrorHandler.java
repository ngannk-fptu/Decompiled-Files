/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class DraconianErrorHandler
implements ErrorHandler {
    private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();

    private DraconianErrorHandler() {
    }

    public static DraconianErrorHandler getInstance() {
        return ERROR_HANDLER_INSTANCE;
    }

    @Override
    public void warning(SAXParseException sAXParseException) throws SAXException {
    }

    @Override
    public void error(SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }

    @Override
    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }
}

