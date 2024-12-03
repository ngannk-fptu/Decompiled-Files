/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.alphabet;

public final class BuiltInRestrictedAlphabets {
    public static final char[][] table = new char[2][];

    static {
        BuiltInRestrictedAlphabets.table[0] = "0123456789-+.E ".toCharArray();
        BuiltInRestrictedAlphabets.table[1] = "0123456789-:TZ ".toCharArray();
    }
}

