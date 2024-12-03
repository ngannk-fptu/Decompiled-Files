/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

public class XmlRpcException
extends Exception {
    public final int code;
    private Throwable cause;

    public XmlRpcException(int code, String message) {
        this(code, message, null);
    }

    public XmlRpcException(int code, String message, Throwable cause) {
        super(message);
        this.code = code;
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

