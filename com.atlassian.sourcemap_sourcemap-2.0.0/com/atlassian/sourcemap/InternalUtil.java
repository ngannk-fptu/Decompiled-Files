/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import java.io.InputStream;
import java.io.Reader;
import java.util.Scanner;

class InternalUtil {
    InternalUtil() {
    }

    static String join(Iterable<String> list, String delimiter) {
        StringBuilder buff = new StringBuilder();
        boolean isFirst = true;
        for (String s : list) {
            if (isFirst) {
                isFirst = false;
            } else {
                buff.append(delimiter);
            }
            buff.append(s);
        }
        return buff.toString();
    }

    public static String toString(InputStream stream) {
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String toString(Reader reader) {
        Scanner s = new Scanner(reader).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

