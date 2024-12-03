/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

public interface ConfluenceMacroConstants {
    public static final int EOF = 0;
    public static final int LBRACKET = 1;
    public static final int RBRACKET = 2;
    public static final int ESCAPEDLBRACKET = 3;
    public static final int ESCAPEDRBRACKET = 4;
    public static final int COLON = 5;
    public static final int BAR = 6;
    public static final int SPACE = 7;
    public static final int EQUALS = 8;
    public static final int ANY = 9;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"{\"", "\"}\"", "\"\\\\{\"", "\"\\\\}\"", "\":\"", "\"|\"", "\" \"", "\"=\"", "<ANY>"};
}

