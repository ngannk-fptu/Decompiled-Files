/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.queryParser;

public interface QueryParserConstants {
    public static final int EOF = 0;
    public static final int _NUM_CHAR = 1;
    public static final int _ESCAPED_CHAR = 2;
    public static final int _TERM_START_CHAR = 3;
    public static final int _TERM_CHAR = 4;
    public static final int _WHITESPACE = 5;
    public static final int _QUOTED_CHAR = 6;
    public static final int AND = 8;
    public static final int OR = 9;
    public static final int NOT = 10;
    public static final int PLUS = 11;
    public static final int MINUS = 12;
    public static final int BAREOPER = 13;
    public static final int LPAREN = 14;
    public static final int RPAREN = 15;
    public static final int COLON = 16;
    public static final int STAR = 17;
    public static final int CARAT = 18;
    public static final int QUOTED = 19;
    public static final int TERM = 20;
    public static final int FUZZY_SLOP = 21;
    public static final int PREFIXTERM = 22;
    public static final int WILDTERM = 23;
    public static final int RANGEIN_START = 24;
    public static final int RANGEEX_START = 25;
    public static final int NUMBER = 26;
    public static final int RANGEIN_TO = 27;
    public static final int RANGEIN_END = 28;
    public static final int RANGEIN_QUOTED = 29;
    public static final int RANGEIN_GOOP = 30;
    public static final int RANGEEX_TO = 31;
    public static final int RANGEEX_END = 32;
    public static final int RANGEEX_QUOTED = 33;
    public static final int RANGEEX_GOOP = 34;
    public static final int Boost = 0;
    public static final int RangeEx = 1;
    public static final int RangeIn = 2;
    public static final int DEFAULT = 3;
    public static final String[] tokenImage = new String[]{"<EOF>", "<_NUM_CHAR>", "<_ESCAPED_CHAR>", "<_TERM_START_CHAR>", "<_TERM_CHAR>", "<_WHITESPACE>", "<_QUOTED_CHAR>", "<token of kind 7>", "<AND>", "<OR>", "<NOT>", "\"+\"", "\"-\"", "<BAREOPER>", "\"(\"", "\")\"", "\":\"", "\"*\"", "\"^\"", "<QUOTED>", "<TERM>", "<FUZZY_SLOP>", "<PREFIXTERM>", "<WILDTERM>", "\"[\"", "\"{\"", "<NUMBER>", "\"TO\"", "\"]\"", "<RANGEIN_QUOTED>", "<RANGEIN_GOOP>", "\"TO\"", "\"}\"", "<RANGEEX_QUOTED>", "<RANGEEX_GOOP>"};
}

