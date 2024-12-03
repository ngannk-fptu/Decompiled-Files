/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler
extends DefaultHandler {
    private static int ERROR_COUNT_LIMIT = 10;
    private int errorCount = 0;

    DefaultValidationErrorHandler() {
    }

    @Override
    public void error(SAXParseException sAXParseException) throws SAXException {
        String string;
        if (this.errorCount >= ERROR_COUNT_LIMIT) {
            return;
        }
        if (this.errorCount == 0) {
            System.err.println("Warning: validation was turned on but an org.xml.sax.ErrorHandler was not");
            System.err.println("set, which is probably not what is desired.  Parser will use a default");
            System.err.println("ErrorHandler to print the first " + ERROR_COUNT_LIMIT + " errors.  Please call");
            System.err.println("the 'setErrorHandler' method to fix this.");
        }
        if ((string = sAXParseException.getSystemId()) == null) {
            string = "null";
        }
        String string2 = "Error: URI=" + string + " Line=" + sAXParseException.getLineNumber() + ": " + sAXParseException.getMessage();
        System.err.println(string2);
        ++this.errorCount;
    }
}

