/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class SOAPIOException
extends IOException {
    SOAPExceptionImpl soapException;

    public SOAPIOException() {
        this.soapException = new SOAPExceptionImpl();
        this.soapException.fillInStackTrace();
    }

    public SOAPIOException(String s) {
        this.soapException = new SOAPExceptionImpl(s);
        this.soapException.fillInStackTrace();
    }

    public SOAPIOException(String reason, Throwable cause) {
        this.soapException = new SOAPExceptionImpl(reason, cause);
        this.soapException.fillInStackTrace();
    }

    public SOAPIOException(Throwable cause) {
        super(cause.toString());
        this.soapException = new SOAPExceptionImpl(cause);
        this.soapException.fillInStackTrace();
    }

    @Override
    public Throwable fillInStackTrace() {
        if (this.soapException != null) {
            this.soapException.fillInStackTrace();
        }
        return this;
    }

    @Override
    public String getLocalizedMessage() {
        return this.soapException.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return this.soapException.getMessage();
    }

    @Override
    public void printStackTrace() {
        this.soapException.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        this.soapException.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        this.soapException.printStackTrace(s);
    }

    @Override
    public String toString() {
        return this.soapException.toString();
    }
}

