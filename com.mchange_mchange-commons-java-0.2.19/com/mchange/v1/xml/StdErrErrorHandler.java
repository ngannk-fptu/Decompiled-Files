/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class StdErrErrorHandler
implements ErrorHandler {
    @Override
    public void warning(SAXParseException sAXParseException) {
        System.err.println("[Warning]");
        this.showExceptionInformation(sAXParseException);
        sAXParseException.printStackTrace();
    }

    @Override
    public void error(SAXParseException sAXParseException) {
        System.err.println("[Error]");
        this.showExceptionInformation(sAXParseException);
        sAXParseException.printStackTrace();
    }

    @Override
    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        System.err.println("[Fatal Error]");
        this.showExceptionInformation(sAXParseException);
        sAXParseException.printStackTrace();
        throw sAXParseException;
    }

    private void showExceptionInformation(SAXParseException sAXParseException) {
        System.err.println("[\tLine Number: " + sAXParseException.getLineNumber() + ']');
        System.err.println("[\tColumn Number: " + sAXParseException.getColumnNumber() + ']');
        System.err.println("[\tPublic ID: " + sAXParseException.getPublicId() + ']');
        System.err.println("[\tSystem ID: " + sAXParseException.getSystemId() + ']');
    }
}

