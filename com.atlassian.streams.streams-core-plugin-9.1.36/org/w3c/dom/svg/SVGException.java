/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

public abstract class SVGException
extends RuntimeException {
    public short code;
    public static final short SVG_WRONG_TYPE_ERR = 0;
    public static final short SVG_INVALID_VALUE_ERR = 1;
    public static final short SVG_MATRIX_NOT_INVERTABLE = 2;

    public SVGException(short s, String string) {
        super(string);
        this.code = s;
    }
}

