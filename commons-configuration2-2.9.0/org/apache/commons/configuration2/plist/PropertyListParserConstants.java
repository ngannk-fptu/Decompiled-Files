/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.plist;

public interface PropertyListParserConstants {
    public static final int EOF = 0;
    public static final int SINGLE_LINE_COMMENT = 9;
    public static final int ARRAY_BEGIN = 11;
    public static final int ARRAY_END = 12;
    public static final int ARRAY_SEPARATOR = 13;
    public static final int DICT_BEGIN = 14;
    public static final int DICT_END = 15;
    public static final int DICT_SEPARATOR = 16;
    public static final int EQUAL = 17;
    public static final int DATA_START = 18;
    public static final int DATA_END = 19;
    public static final int DATE_START = 20;
    public static final int QUOTE = 21;
    public static final int LETTER = 22;
    public static final int WHITE = 23;
    public static final int HEXA = 24;
    public static final int DATA = 25;
    public static final int DATE = 26;
    public static final int STRING = 27;
    public static final int QUOTED_STRING = 28;
    public static final int ESCAPED_QUOTE = 29;
    public static final int DEFAULT = 0;
    public static final int IN_COMMENT = 1;
    public static final int IN_SINGLE_LINE_COMMENT = 2;
    public static final String[] tokenImage = new String[]{"<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"/*\"", "<token of kind 6>", "\"*/\"", "\"//\"", "<SINGLE_LINE_COMMENT>", "<token of kind 10>", "\"(\"", "\")\"", "\",\"", "\"{\"", "\"}\"", "\";\"", "\"=\"", "\"<\"", "\">\"", "\"<*D\"", "\"\\\"\"", "<LETTER>", "<WHITE>", "<HEXA>", "<DATA>", "<DATE>", "<STRING>", "<QUOTED_STRING>", "\"\\\\\\\"\""};
}

