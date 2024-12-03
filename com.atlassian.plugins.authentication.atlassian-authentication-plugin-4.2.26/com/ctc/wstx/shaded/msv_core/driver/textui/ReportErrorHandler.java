/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.shaded.msv_core.driver.textui.Driver
 */
package com.ctc.wstx.shaded.msv_core.driver.textui;

import com.ctc.wstx.shaded.msv_core.driver.textui.Driver;
import com.ctc.wstx.shaded.msv_core.verifier.ValidationUnrecoverableException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReportErrorHandler
implements ErrorHandler {
    private int counter = 0;
    public boolean hadError = false;
    public static final String MSG_TOO_MANY_ERRORS = "ReportErrorHandler.TooManyErrors";
    public static final String MSG_ERROR = "ReportErrorHandler.Error";
    public static final String MSG_WARNING = "ReportErrorHandler.Warning";
    public static final String MSG_FATAL = "ReportErrorHandler.Fatal";

    public void error(SAXParseException e) throws SAXException {
        this.hadError = true;
        this.countCheck(e);
        ReportErrorHandler.printSAXParseException(e, MSG_ERROR);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        this.hadError = true;
        ReportErrorHandler.printSAXParseException(e, MSG_FATAL);
        throw new ValidationUnrecoverableException(e);
    }

    public void warning(SAXParseException e) {
        ReportErrorHandler.printSAXParseException(e, MSG_WARNING);
    }

    protected static void printSAXParseException(SAXParseException spe, String prop) {
        System.out.println(Driver.localize((String)prop, (Object[])new Object[]{new Integer(spe.getLineNumber()), new Integer(spe.getColumnNumber()), spe.getSystemId(), spe.getLocalizedMessage()}));
    }

    private void countCheck(SAXParseException e) throws ValidationUnrecoverableException {
        if (this.counter++ < 20) {
            return;
        }
        System.out.println(Driver.localize((String)MSG_TOO_MANY_ERRORS));
        throw new ValidationUnrecoverableException(e);
    }
}

