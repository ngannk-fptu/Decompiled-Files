/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j;

public class NewLineSupport {
    public static String NL;

    public static StringBuffer join(StringBuffer buffer, String[] lines) {
        for (String line : lines) {
            buffer.append(line).append(NL);
        }
        return buffer;
    }

    public static StringBuilder join(StringBuilder buffer, String[] lines) {
        for (String line : lines) {
            buffer.append(line).append(NL);
        }
        return buffer;
    }

    public static String join(String[] lines) {
        return NewLineSupport.join(new StringBuilder(), lines).toString();
    }

    static {
        try {
            NL = System.getProperty("line.separator");
        }
        catch (SecurityException ignore) {
            NL = "\n";
        }
    }
}

