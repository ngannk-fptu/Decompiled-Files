/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.xmlrules;

public class XmlLoadException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Throwable cause = null;

    public XmlLoadException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    public XmlLoadException(String msg) {
        super(msg);
    }

    public XmlLoadException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

