/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import org.jdom2.internal.SystemProperty;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum LineSeparator {
    CRNL("\r\n"),
    NL("\n"),
    CR("\r"),
    DOS("\r\n"),
    UNIX("\n"),
    SYSTEM(SystemProperty.get("line.separator", "\r\n")),
    NONE(null),
    DEFAULT(LineSeparator.getDefaultLineSeparator());

    private final String value;

    private static String getDefaultLineSeparator() {
        String prop = SystemProperty.get("org.jdom2.output.LineSeparator", "DEFAULT");
        if ("DEFAULT".equals(prop)) {
            return "\r\n";
        }
        if ("SYSTEM".equals(prop)) {
            return System.getProperty("line.separator");
        }
        if ("CRNL".equals(prop)) {
            return "\r\n";
        }
        if ("NL".equals(prop)) {
            return "\n";
        }
        if ("CR".equals(prop)) {
            return "\r";
        }
        if ("DOS".equals(prop)) {
            return "\r\n";
        }
        if ("UNIX".equals(prop)) {
            return "\n";
        }
        if ("NONE".equals(prop)) {
            return null;
        }
        return prop;
    }

    private LineSeparator(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

