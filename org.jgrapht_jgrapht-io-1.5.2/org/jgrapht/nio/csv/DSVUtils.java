/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio.csv;

class DSVUtils {
    private static final char DSV_QUOTE = '\"';
    private static final char DSV_LF = '\n';
    private static final char DSV_CR = '\r';
    private static final String DSV_QUOTE_AS_STRING = String.valueOf('\"');

    DSVUtils() {
    }

    public static boolean isValidDelimiter(char delimiter) {
        return delimiter != '\n' && delimiter != '\r' && delimiter != '\"';
    }

    public static String escapeDSV(String input, char delimiter) {
        char[] specialChars = new char[]{delimiter, '\"', '\n', '\r'};
        boolean containsSpecial = false;
        for (int i = 0; i < specialChars.length; ++i) {
            if (!input.contains(String.valueOf(specialChars[i]))) continue;
            containsSpecial = true;
            break;
        }
        if (containsSpecial) {
            return DSV_QUOTE_AS_STRING + input.replaceAll(DSV_QUOTE_AS_STRING, DSV_QUOTE_AS_STRING + DSV_QUOTE_AS_STRING) + DSV_QUOTE_AS_STRING;
        }
        return input;
    }

    public static String unescapeDSV(String input, char delimiter) {
        char[] specialChars = new char[]{delimiter, '\"', '\n', '\r'};
        if (input.charAt(0) != '\"' || input.charAt(input.length() - 1) != '\"') {
            return input;
        }
        String noQuotes = input.subSequence(1, input.length() - 1).toString();
        boolean containsSpecial = false;
        for (int i = 0; i < specialChars.length; ++i) {
            if (!noQuotes.contains(String.valueOf(specialChars[i]))) continue;
            containsSpecial = true;
            break;
        }
        if (containsSpecial) {
            return noQuotes.replaceAll(DSV_QUOTE_AS_STRING + DSV_QUOTE_AS_STRING, DSV_QUOTE_AS_STRING);
        }
        return input;
    }
}

