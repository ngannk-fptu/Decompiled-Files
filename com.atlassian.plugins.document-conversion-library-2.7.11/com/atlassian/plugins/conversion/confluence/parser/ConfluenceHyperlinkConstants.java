/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

public interface ConfluenceHyperlinkConstants {
    public static final int EOF = 0;
    public static final int LEFT_BRACKET = 1;
    public static final int RIGHT_BRACKET = 2;
    public static final int BAR = 3;
    public static final int EQUALS = 4;
    public static final int PLUS = 5;
    public static final int AMP = 6;
    public static final int COLON = 7;
    public static final int DOLLAR = 8;
    public static final int UP = 9;
    public static final int DOTDOT = 10;
    public static final int AT = 11;
    public static final int QUESTION = 12;
    public static final int POUND = 13;
    public static final int IMAGE = 14;
    public static final int NOT_IN_TITLE = 15;
    public static final int FRONTSLASH = 16;
    public static final int SQUIGGLE = 17;
    public static final int WHITESPACE = 18;
    public static final int PROTOCOL = 19;
    public static final int MAILTO = 20;
    public static final int LETTER = 21;
    public static final int ANY = 22;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"[\"", "\"]\"", "\"|\"", "\"=\"", "\"+\"", "\"&\"", "\":\"", "\"$\"", "\"^\"", "\"..\"", "\"@\"", "\"?\"", "\"#\"", "<IMAGE>", "<NOT_IN_TITLE>", "\"/\"", "\"~\"", "<WHITESPACE>", "<PROTOCOL>", "<MAILTO>", "<LETTER>", "<ANY>"};
}

