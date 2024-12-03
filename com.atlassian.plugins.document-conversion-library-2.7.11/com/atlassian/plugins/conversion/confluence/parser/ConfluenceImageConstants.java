/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

public interface ConfluenceImageConstants {
    public static final int EOF = 0;
    public static final int EXCLAMATION = 1;
    public static final int BAR = 2;
    public static final int COMMA = 3;
    public static final int WHITESPACE = 4;
    public static final int EQUALS = 5;
    public static final int COLON = 6;
    public static final int UP = 7;
    public static final int LETTER = 8;
    public static final int ANY = 9;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"!\"", "\"|\"", "\",\"", "<WHITESPACE>", "\"=\"", "\":\"", "\"^\"", "<LETTER>", "<ANY>"};
}

