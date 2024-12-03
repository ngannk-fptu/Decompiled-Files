/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

@Deprecated
public class Strings {
    @Deprecated
    public static String capitalize(String s) {
        char capitalized;
        if (s.length() == 0) {
            return s;
        }
        char first = s.charAt(0);
        return first == (capitalized = Character.toUpperCase(first)) ? s : capitalized + s.substring(1);
    }
}

