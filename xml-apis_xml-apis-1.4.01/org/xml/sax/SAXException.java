/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax;

public class SAXException
extends Exception {
    private Exception exception;
    static final long serialVersionUID = 583241635256073760L;

    public SAXException() {
        this.exception = null;
    }

    public SAXException(String string) {
        super(string);
        this.exception = null;
    }

    public SAXException(Exception exception) {
        this.exception = exception;
    }

    public SAXException(String string, Exception exception) {
        super(string);
        this.exception = exception;
    }

    public String getMessage() {
        String string = super.getMessage();
        if (string == null && this.exception != null) {
            return this.exception.getMessage();
        }
        return string;
    }

    public Exception getException() {
        return this.exception;
    }

    public String toString() {
        if (this.exception != null) {
            return this.exception.toString();
        }
        return super.toString();
    }
}

