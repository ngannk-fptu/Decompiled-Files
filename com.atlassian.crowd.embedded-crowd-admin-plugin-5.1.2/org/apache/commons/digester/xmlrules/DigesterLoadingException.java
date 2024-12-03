/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.xmlrules;

public class DigesterLoadingException
extends Exception {
    private Throwable cause = null;

    public DigesterLoadingException(String msg) {
        super(msg);
    }

    public DigesterLoadingException(Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }

    public DigesterLoadingException(String msg, Throwable cause) {
        this(msg);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

