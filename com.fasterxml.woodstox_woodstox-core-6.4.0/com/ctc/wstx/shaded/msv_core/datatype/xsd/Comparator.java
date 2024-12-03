/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

public interface Comparator {
    public static final int LESS = -1;
    public static final int EQUAL = 0;
    public static final int GREATER = 1;
    public static final int UNDECIDABLE = 999;

    public int compare(Object var1, Object var2);
}

