/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.regexp;

class ParseException
extends RuntimeException {
    int location;

    public ParseException(String mes, int location) {
        super(mes);
        this.location = location;
    }

    public int getLocation() {
        return this.location;
    }
}

