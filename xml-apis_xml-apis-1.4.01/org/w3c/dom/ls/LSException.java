/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ls;

public class LSException
extends RuntimeException {
    public short code;
    public static final short PARSE_ERR = 81;
    public static final short SERIALIZE_ERR = 82;

    public LSException(short s, String string) {
        super(string);
        this.code = s;
    }
}

