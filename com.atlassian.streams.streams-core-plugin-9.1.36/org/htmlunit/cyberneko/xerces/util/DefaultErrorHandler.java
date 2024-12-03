/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import java.io.PrintWriter;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLErrorHandler;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;

public class DefaultErrorHandler
implements XMLErrorHandler {
    private final PrintWriter fOut_;

    public DefaultErrorHandler() {
        this(new PrintWriter(System.err));
    }

    public DefaultErrorHandler(PrintWriter out) {
        this.fOut_ = out;
    }

    @Override
    public void warning(String domain, String key, XMLParseException ex) throws XNIException {
        this.printError("Warning", ex);
    }

    @Override
    public void error(String domain, String key, XMLParseException ex) throws XNIException {
        this.printError("Error", ex);
    }

    @Override
    public void fatalError(String domain, String key, XMLParseException ex) throws XNIException {
        this.printError("Fatal Error", ex);
        throw ex;
    }

    private void printError(String type, XMLParseException ex) {
        this.fOut_.print("[");
        this.fOut_.print(type);
        this.fOut_.print("] ");
        String systemId = ex.getExpandedSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf(47);
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
            this.fOut_.print(systemId);
        }
        this.fOut_.print(':');
        this.fOut_.print(ex.getLineNumber());
        this.fOut_.print(':');
        this.fOut_.print(ex.getColumnNumber());
        this.fOut_.print(": ");
        this.fOut_.print(ex.getMessage());
        this.fOut_.println();
        this.fOut_.flush();
    }
}

