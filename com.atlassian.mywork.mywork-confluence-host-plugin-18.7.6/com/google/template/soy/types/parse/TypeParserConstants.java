/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.parse;

public interface TypeParserConstants {
    public static final int EOF = 0;
    public static final int LANGLE = 1;
    public static final int RANGLE = 2;
    public static final int LBRACKET = 3;
    public static final int RBRACKET = 4;
    public static final int COMMA = 5;
    public static final int VBAR = 6;
    public static final int COLON = 7;
    public static final int DOT = 8;
    public static final int QMARK = 9;
    public static final int GENERIC_LIST = 10;
    public static final int GENERIC_MAP = 11;
    public static final int IDENT = 12;
    public static final int WS = 13;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"<\"", "\">\"", "\"[\"", "\"]\"", "\",\"", "\"|\"", "\":\"", "\".\"", "\"?\"", "\"list\"", "\"map\"", "<IDENT>", "<WS>"};
}

