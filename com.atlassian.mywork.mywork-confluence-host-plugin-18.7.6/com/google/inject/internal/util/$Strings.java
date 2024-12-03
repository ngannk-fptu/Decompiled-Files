/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

public class $Strings {
    private $Strings() {
    }

    public static String capitalize(String s) {
        char capitalized;
        if (s.length() == 0) {
            return s;
        }
        char first = s.charAt(0);
        return first == (capitalized = Character.toUpperCase(first)) ? s : capitalized + s.substring(1);
    }
}

