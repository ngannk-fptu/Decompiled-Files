/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

public interface CssColorConstants {
    public static final int EOF = 0;
    public static final int POUND = 1;
    public static final int RGB = 2;
    public static final int COMMA = 3;
    public static final int DIGIT = 4;
    public static final int PERCENT = 5;
    public static final int DOT = 6;
    public static final int LETTER = 7;
    public static final int WHITESPACE = 8;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"#\"", "\"rgb\"", "\",\"", "<DIGIT>", "\"%\"", "\".\"", "<LETTER>", "<WHITESPACE>", "\"(\"", "\")\""};
}

