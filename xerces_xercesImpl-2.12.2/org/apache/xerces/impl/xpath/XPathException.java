/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath;

public class XPathException
extends Exception {
    static final long serialVersionUID = -948482312169512085L;
    private final String fKey;

    public XPathException() {
        this.fKey = "c-general-xpath";
    }

    public XPathException(String string) {
        this.fKey = string;
    }

    public String getKey() {
        return this.fKey;
    }
}

