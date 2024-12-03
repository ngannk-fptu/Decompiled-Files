/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xpath.regex;

public class ParseException
extends RuntimeException {
    static final long serialVersionUID = -7012400318097691370L;
    final int location;

    public ParseException(String string, int n) {
        super(string);
        this.location = n;
    }

    public int getLocation() {
        return this.location;
    }
}

