/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype;

public class DatatypeException
extends Exception {
    private final int index;
    public static final int UNKNOWN = -1;

    public DatatypeException(int n, String string) {
        super(string);
        this.index = n;
    }

    public DatatypeException(String string) {
        this(-1, string);
    }

    public DatatypeException() {
        this(-1, null);
    }

    public int getIndex() {
        return this.index;
    }
}

