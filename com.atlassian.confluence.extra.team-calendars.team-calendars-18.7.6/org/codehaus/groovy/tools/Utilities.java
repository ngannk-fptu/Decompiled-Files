/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class Utilities {
    private static final Set<String> INVALID_JAVA_IDENTIFIERS = new HashSet<String>(Arrays.asList("abstract assert boolean break byte case catch char class const continue default do double else enum extends final finally float for goto if implements import instanceof int interface long native new package private protected public short static strictfp super switch synchronized this throw throws transient try void volatile while true false null".split(" ")));
    private static String eol = System.getProperty("line.separator", "\n");

    public static String repeatString(String pattern, int repeats) {
        StringBuilder buffer = new StringBuilder(pattern.length() * repeats);
        for (int i = 0; i < repeats; ++i) {
            buffer.append(pattern);
        }
        return new String(buffer);
    }

    public static String eol() {
        return eol;
    }

    public static boolean isJavaIdentifier(String name) {
        if (name.length() == 0 || INVALID_JAVA_IDENTIFIERS.contains(name)) {
            return false;
        }
        char[] chars = name.toCharArray();
        if (!Character.isJavaIdentifierStart(chars[0])) {
            return false;
        }
        for (int i = 1; i < chars.length; ++i) {
            if (Character.isJavaIdentifierPart(chars[i])) continue;
            return false;
        }
        return true;
    }
}

