/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.ldap.util;

public class XmlValidator {
    public static boolean isSafe(String s) {
        int c;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i += Character.charCount(c)) {
            c = Character.codePointAt(chars, i);
            if (XmlValidator.isXmlCharacter(c)) continue;
            return false;
        }
        return true;
    }

    public static boolean isXmlCharacter(int c) {
        return c == 9 || c == 10 || c == 13 || c >= 32 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF;
    }
}

