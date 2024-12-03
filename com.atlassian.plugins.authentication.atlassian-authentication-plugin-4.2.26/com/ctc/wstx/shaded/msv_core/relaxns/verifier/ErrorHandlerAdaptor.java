/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerAdaptor
implements ErrorHandler {
    private final Dispatcher core;

    public ErrorHandlerAdaptor(Dispatcher core) {
        this.core = core;
    }

    public void fatalError(SAXParseException error) throws SAXException {
        this.core.getErrorHandler().fatalError(error);
    }

    public void error(SAXParseException error) throws SAXException {
        this.core.getErrorHandler().error(error);
    }

    public void warning(SAXParseException error) throws SAXException {
        this.core.getErrorHandler().warning(error);
    }
}

