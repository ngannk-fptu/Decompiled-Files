/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

public interface ConfluenceDocumentConstants {
    public static final int EOF = 0;
    public static final int BULLETS = 1;
    public static final int NUMBERS = 2;
    public static final int EOL = 3;
    public static final int WHITESPACE = 4;
    public static final int HEADING = 5;
    public static final int IMAGE = 6;
    public static final int HYPERLINK = 7;
    public static final int FORMATMACRO = 8;
    public static final int MACRO = 9;
    public static final int ESCAPEDCHAR = 10;
    public static final int LINEESCAPE = 11;
    public static final int MONOL = 12;
    public static final int BAR = 13;
    public static final int BLOCKQUOTE = 14;
    public static final int HR = 15;
    public static final int TEXT = 16;
    public static final int MACROEND = 17;
    public static final int MACROBODY = 18;
    public static final int DEFAULT = 0;
    public static final int INSIDEMACRO = 1;
    public static final String[] tokenImage = new String[]{"<EOF>", "<BULLETS>", "<NUMBERS>", "<EOL>", "<WHITESPACE>", "<HEADING>", "<IMAGE>", "<HYPERLINK>", "<FORMATMACRO>", "<MACRO>", "<ESCAPEDCHAR>", "<LINEESCAPE>", "\"{{\"", "\"|\"", "\"bq.\"", "<HR>", "<TEXT>", "<MACROEND>", "<MACROBODY>"};
}

