/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprparse;

public interface ExpressionParserConstants {
    public static final int EOF = 0;
    public static final int NULL = 1;
    public static final int BOOLEAN = 2;
    public static final int INTEGER = 3;
    public static final int FLOAT = 4;
    public static final int STRING = 5;
    public static final int DEC_DIGITS = 6;
    public static final int HEX_DIGIT = 7;
    public static final int UNARY_OR_BINARY_OP = 8;
    public static final int ONLY_UNARY_OP = 9;
    public static final int ONLY_BINARY_OP = 10;
    public static final int IDENT = 11;
    public static final int DOLLAR_IDENT = 12;
    public static final int DOT_IDENT = 13;
    public static final int QUESTION_DOT_IDENT = 14;
    public static final int DOT_INDEX = 15;
    public static final int QUESTION_DOT_INDEX = 16;
    public static final int WS = 17;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"null\"", "<BOOLEAN>", "<INTEGER>", "<FLOAT>", "<STRING>", "<DEC_DIGITS>", "<HEX_DIGIT>", "\"-\"", "\"not\"", "<ONLY_BINARY_OP>", "<IDENT>", "<DOLLAR_IDENT>", "<DOT_IDENT>", "<QUESTION_DOT_IDENT>", "<DOT_INDEX>", "<QUESTION_DOT_INDEX>", "<WS>", "\",\"", "\"?:\"", "\"?\"", "\":\"", "\"(\"", "\")\"", "\"[\"", "\"]\"", "\"$ij.\"", "\"$ij?.\"", "\"?[\""};
}

