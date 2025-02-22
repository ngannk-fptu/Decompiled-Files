/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.parser;

public interface ELParserConstants {
    public static final int EOF = 0;
    public static final int LITERAL_EXPRESSION = 1;
    public static final int START_DYNAMIC_EXPRESSION = 2;
    public static final int START_DEFERRED_EXPRESSION = 3;
    public static final int START_SET_OR_MAP = 8;
    public static final int RBRACE = 9;
    public static final int INTEGER_LITERAL = 10;
    public static final int FLOATING_POINT_LITERAL = 11;
    public static final int EXPONENT = 12;
    public static final int STRING_LITERAL = 13;
    public static final int TRUE = 14;
    public static final int FALSE = 15;
    public static final int NULL = 16;
    public static final int DOT = 17;
    public static final int LPAREN = 18;
    public static final int RPAREN = 19;
    public static final int LBRACK = 20;
    public static final int RBRACK = 21;
    public static final int COLON = 22;
    public static final int SEMICOLON = 23;
    public static final int COMMA = 24;
    public static final int GT0 = 25;
    public static final int GT1 = 26;
    public static final int LT0 = 27;
    public static final int LT1 = 28;
    public static final int GE0 = 29;
    public static final int GE1 = 30;
    public static final int LE0 = 31;
    public static final int LE1 = 32;
    public static final int EQ0 = 33;
    public static final int EQ1 = 34;
    public static final int NE0 = 35;
    public static final int NE1 = 36;
    public static final int NOT0 = 37;
    public static final int NOT1 = 38;
    public static final int AND0 = 39;
    public static final int AND1 = 40;
    public static final int OR0 = 41;
    public static final int OR1 = 42;
    public static final int EMPTY = 43;
    public static final int INSTANCEOF = 44;
    public static final int MULT = 45;
    public static final int PLUS = 46;
    public static final int MINUS = 47;
    public static final int QUESTIONMARK = 48;
    public static final int DIV0 = 49;
    public static final int DIV1 = 50;
    public static final int MOD0 = 51;
    public static final int MOD1 = 52;
    public static final int CONCAT = 53;
    public static final int ASSIGN = 54;
    public static final int ARROW = 55;
    public static final int IDENTIFIER = 56;
    public static final int FUNCTIONSUFFIX = 57;
    public static final int IMPL_OBJ_START = 58;
    public static final int LETTER = 59;
    public static final int DIGIT = 60;
    public static final int ILLEGAL_CHARACTER = 61;
    public static final int DEFAULT = 0;
    public static final int IN_EXPRESSION = 1;
    public static final int IN_SET_OR_MAP = 2;
    public static final String[] tokenImage = new String[]{"<EOF>", "<LITERAL_EXPRESSION>", "\"${\"", "\"#{\"", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"{\"", "\"}\"", "<INTEGER_LITERAL>", "<FLOATING_POINT_LITERAL>", "<EXPONENT>", "<STRING_LITERAL>", "\"true\"", "\"false\"", "\"null\"", "\".\"", "\"(\"", "\")\"", "\"[\"", "\"]\"", "\":\"", "\";\"", "\",\"", "\">\"", "\"gt\"", "\"<\"", "\"lt\"", "\">=\"", "\"ge\"", "\"<=\"", "\"le\"", "\"==\"", "\"eq\"", "\"!=\"", "\"ne\"", "\"!\"", "\"not\"", "\"&&\"", "\"and\"", "\"||\"", "\"or\"", "\"empty\"", "\"instanceof\"", "\"*\"", "\"+\"", "\"-\"", "\"?\"", "\"/\"", "\"div\"", "\"%\"", "\"mod\"", "\"+=\"", "\"=\"", "\"->\"", "<IDENTIFIER>", "<FUNCTIONSUFFIX>", "\"#\"", "<LETTER>", "<DIGIT>", "<ILLEGAL_CHARACTER>"};
}

