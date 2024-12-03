/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.html.encode;

class Util {
    Util() {
    }

    public static boolean isPrintableAscii(char c) {
        return ' ' <= c && c <= '~';
    }
}

