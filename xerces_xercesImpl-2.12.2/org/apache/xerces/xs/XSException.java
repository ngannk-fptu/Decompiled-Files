/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

public class XSException
extends RuntimeException {
    static final long serialVersionUID = 3111893084677917742L;
    public short code;
    public static final short NOT_SUPPORTED_ERR = 1;
    public static final short INDEX_SIZE_ERR = 2;

    public XSException(short s, String string) {
        super(string);
        this.code = s;
    }
}

