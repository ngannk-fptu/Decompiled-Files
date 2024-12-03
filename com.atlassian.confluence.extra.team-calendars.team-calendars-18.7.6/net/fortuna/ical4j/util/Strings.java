/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.util.regex.Pattern;

public final class Strings {
    public static final Pattern PARAM_QUOTE_PATTERN = Pattern.compile("[:;,]|[^\\p{ASCII}]");
    private static final Pattern ESCAPE_PUNCTUATION_PATTERN = Pattern.compile("([,;])");
    private static final Pattern UNESCAPE_PUNCTUATION_PATTERN = Pattern.compile("\\\\([,;\"])");
    private static final Pattern ESCAPE_NEWLINE_PATTERN = Pattern.compile("\r?\n");
    private static final Pattern UNESCAPE_NEWLINE_PATTERN = Pattern.compile("(?<!\\\\)\\\\n");
    private static final Pattern ESCAPE_BACKSLASH_PATTERN = Pattern.compile("\\\\");
    private static final Pattern UNESCAPE_BACKSLASH_PATTERN = Pattern.compile("\\\\\\\\");
    public static final String LINE_SEPARATOR = "\r\n";

    private Strings() {
    }

    public static String quote(Object aValue) {
        if (aValue != null) {
            return "\"" + aValue + "\"";
        }
        return "\"\"";
    }

    public static String unquote(String aValue) {
        if (aValue != null && aValue.startsWith("\"") && aValue.endsWith("\"")) {
            return aValue.substring(0, aValue.length() - 1).substring(1);
        }
        return aValue;
    }

    public static String escape(String aValue) {
        return Strings.escapePunctuation(Strings.escapeNewline(Strings.escapeBackslash(aValue)));
    }

    public static String unescape(String aValue) {
        return Strings.unescapeBackslash(Strings.unescapeNewline(Strings.unescapePunctuation(aValue)));
    }

    private static String escapePunctuation(String value) {
        if (value != null) {
            return ESCAPE_PUNCTUATION_PATTERN.matcher(value).replaceAll("\\\\$1");
        }
        return null;
    }

    private static String unescapePunctuation(String value) {
        if (value != null) {
            return UNESCAPE_PUNCTUATION_PATTERN.matcher(value).replaceAll("$1");
        }
        return null;
    }

    public static String escapeNewline(String value) {
        if (value != null) {
            return ESCAPE_NEWLINE_PATTERN.matcher(value).replaceAll("\\\\n");
        }
        return null;
    }

    private static String unescapeNewline(String value) {
        if (value != null) {
            return UNESCAPE_NEWLINE_PATTERN.matcher(value).replaceAll("\n");
        }
        return null;
    }

    private static String escapeBackslash(String value) {
        if (value != null) {
            return ESCAPE_BACKSLASH_PATTERN.matcher(value).replaceAll("\\\\\\\\");
        }
        return null;
    }

    private static String unescapeBackslash(String value) {
        if (value != null) {
            return UNESCAPE_BACKSLASH_PATTERN.matcher(value).replaceAll("\\\\");
        }
        return null;
    }

    public static String valueOf(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
}

