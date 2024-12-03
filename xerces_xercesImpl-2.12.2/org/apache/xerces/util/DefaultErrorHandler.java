/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.PrintWriter;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLParseException;

public class DefaultErrorHandler
implements XMLErrorHandler {
    protected PrintWriter fOut;

    public DefaultErrorHandler() {
        this(new PrintWriter(System.err));
    }

    public DefaultErrorHandler(PrintWriter printWriter) {
        this.fOut = printWriter;
    }

    @Override
    public void warning(String string, String string2, XMLParseException xMLParseException) throws XNIException {
        this.printError("Warning", xMLParseException);
    }

    @Override
    public void error(String string, String string2, XMLParseException xMLParseException) throws XNIException {
        this.printError("Error", xMLParseException);
    }

    @Override
    public void fatalError(String string, String string2, XMLParseException xMLParseException) throws XNIException {
        this.printError("Fatal Error", xMLParseException);
        throw xMLParseException;
    }

    private void printError(String string, XMLParseException xMLParseException) {
        this.fOut.print("[");
        this.fOut.print(string);
        this.fOut.print("] ");
        String string2 = xMLParseException.getExpandedSystemId();
        if (string2 != null) {
            int n = string2.lastIndexOf(47);
            if (n != -1) {
                string2 = string2.substring(n + 1);
            }
            this.fOut.print(string2);
        }
        this.fOut.print(':');
        this.fOut.print(xMLParseException.getLineNumber());
        this.fOut.print(':');
        this.fOut.print(xMLParseException.getColumnNumber());
        this.fOut.print(": ");
        this.fOut.print(xMLParseException.getMessage());
        this.fOut.println();
        this.fOut.flush();
    }
}

