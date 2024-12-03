/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.regexp;

public class RegexpUtil {
    public static boolean hasFlag(int options, int flag) {
        return (options & flag) > 0;
    }

    public static int removeFlag(int options, int flag) {
        return options & -1 - flag;
    }

    public static int asOptions(String flags) {
        int options = 0;
        if (flags != null) {
            options = RegexpUtil.asOptions(!flags.contains("i"), flags.contains("m"), flags.contains("s"));
            if (flags.contains("g")) {
                options |= 0x10;
            }
        }
        return options;
    }

    public static int asOptions(boolean caseSensitive) {
        return RegexpUtil.asOptions(caseSensitive, false, false);
    }

    public static int asOptions(boolean caseSensitive, boolean multiLine, boolean singleLine) {
        int options = 0;
        if (!caseSensitive) {
            options |= 0x100;
        }
        if (multiLine) {
            options |= 0x1000;
        }
        if (singleLine) {
            options |= 0x10000;
        }
        return options;
    }
}

