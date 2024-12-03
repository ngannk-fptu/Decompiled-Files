/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.util;

public class Util {
    public static int countLines(String str) {
        int lines = 1;
        int pos = 0;
        while ((pos = str.indexOf(10, pos) + 1) != 0) {
            ++lines;
        }
        return lines;
    }

    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*\"?|<>]", "_");
    }
}

