/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.message;

public abstract class Util {
    public static boolean parseBool(String value) {
        if (value.length() == 0) {
            return false;
        }
        char ch = value.charAt(0);
        return ch == 't' || ch == '1';
    }
}

