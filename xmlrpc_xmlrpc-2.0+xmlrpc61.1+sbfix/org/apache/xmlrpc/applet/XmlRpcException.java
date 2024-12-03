/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.applet;

public class XmlRpcException
extends Exception {
    public final int code;

    public XmlRpcException(int code, String message) {
        super(message);
        this.code = code;
    }
}

