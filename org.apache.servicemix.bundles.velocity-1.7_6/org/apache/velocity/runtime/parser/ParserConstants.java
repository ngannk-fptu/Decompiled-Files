/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser;

public interface ParserConstants {
    public static final int EOF = 0;
    public static final int INDEX_LBRACKET = 1;
    public static final int INDEX_RBRACKET = 2;
    public static final int LBRACKET = 3;
    public static final int RBRACKET = 4;
    public static final int COMMA = 5;
    public static final int DOUBLEDOT = 6;
    public static final int COLON = 7;
    public static final int LEFT_CURLEY = 8;
    public static final int RIGHT_CURLEY = 9;
    public static final int LPAREN = 10;
    public static final int RPAREN = 11;
    public static final int REFMOD2_RPAREN = 12;
    public static final int ESCAPE_DIRECTIVE = 13;
    public static final int SET_DIRECTIVE = 14;
    public static final int DOLLAR = 15;
    public static final int DOLLARBANG = 16;
    public static final int HASH = 20;
    public static final int SINGLE_LINE_COMMENT_START = 21;
    public static final int DOUBLE_ESCAPE = 22;
    public static final int ESCAPE = 23;
    public static final int TEXT = 24;
    public static final int SINGLE_LINE_COMMENT = 25;
    public static final int FORMAL_COMMENT = 26;
    public static final int MULTI_LINE_COMMENT = 27;
    public static final int TEXTBLOCK = 28;
    public static final int WHITESPACE = 31;
    public static final int STRING_LITERAL = 32;
    public static final int TRUE = 33;
    public static final int FALSE = 34;
    public static final int NEWLINE = 35;
    public static final int MINUS = 36;
    public static final int PLUS = 37;
    public static final int MULTIPLY = 38;
    public static final int DIVIDE = 39;
    public static final int MODULUS = 40;
    public static final int LOGICAL_AND = 41;
    public static final int LOGICAL_OR = 42;
    public static final int LOGICAL_LT = 43;
    public static final int LOGICAL_LE = 44;
    public static final int LOGICAL_GT = 45;
    public static final int LOGICAL_GE = 46;
    public static final int LOGICAL_EQUALS = 47;
    public static final int LOGICAL_NOT_EQUALS = 48;
    public static final int LOGICAL_NOT = 49;
    public static final int EQUALS = 50;
    public static final int END = 51;
    public static final int IF_DIRECTIVE = 52;
    public static final int ELSEIF_DIRECTIVE = 53;
    public static final int ELSE_DIRECTIVE = 54;
    public static final int DIGIT = 55;
    public static final int INTEGER_LITERAL = 56;
    public static final int FLOATING_POINT_LITERAL = 57;
    public static final int EXPONENT = 58;
    public static final int LETTER = 59;
    public static final int DIRECTIVE_CHAR = 60;
    public static final int WORD = 61;
    public static final int BRACKETED_WORD = 62;
    public static final int ALPHA_CHAR = 63;
    public static final int ALPHANUM_CHAR = 64;
    public static final int IDENTIFIER_CHAR = 65;
    public static final int IDENTIFIER = 66;
    public static final int DOT = 67;
    public static final int LCURLY = 68;
    public static final int RCURLY = 69;
    public static final int REFERENCE_TERMINATOR = 70;
    public static final int DIRECTIVE_TERMINATOR = 71;
    public static final int EMPTY_INDEX = 72;
    public static final int REFERENCE = 0;
    public static final int REFMODIFIER = 1;
    public static final int REFINDEX = 2;
    public static final int DIRECTIVE = 3;
    public static final int REFMOD2 = 4;
    public static final int DEFAULT = 5;
    public static final int REFMOD = 6;
    public static final int IN_TEXTBLOCK = 7;
    public static final int IN_MULTI_LINE_COMMENT = 8;
    public static final int IN_FORMAL_COMMENT = 9;
    public static final int IN_SINGLE_LINE_COMMENT = 10;
    public static final int PRE_DIRECTIVE = 11;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"[\"", "\"]\"", "\"[\"", "\"]\"", "\",\"", "\"..\"", "\":\"", "\"{\"", "\"}\"", "\"(\"", "<RPAREN>", "\")\"", "<ESCAPE_DIRECTIVE>", "<SET_DIRECTIVE>", "<DOLLAR>", "<DOLLARBANG>", "\"#[[\"", "<token of kind 18>", "\"#*\"", "\"#\"", "\"##\"", "\"\\\\\\\\\"", "\"\\\\\"", "<TEXT>", "<SINGLE_LINE_COMMENT>", "\"*#\"", "\"*#\"", "\"]]#\"", "<token of kind 29>", "<token of kind 30>", "<WHITESPACE>", "<STRING_LITERAL>", "\"true\"", "\"false\"", "<NEWLINE>", "\"-\"", "\"+\"", "\"*\"", "\"/\"", "\"%\"", "<LOGICAL_AND>", "<LOGICAL_OR>", "<LOGICAL_LT>", "<LOGICAL_LE>", "<LOGICAL_GT>", "<LOGICAL_GE>", "<LOGICAL_EQUALS>", "<LOGICAL_NOT_EQUALS>", "<LOGICAL_NOT>", "\"=\"", "<END>", "<IF_DIRECTIVE>", "<ELSEIF_DIRECTIVE>", "<ELSE_DIRECTIVE>", "<DIGIT>", "<INTEGER_LITERAL>", "<FLOATING_POINT_LITERAL>", "<EXPONENT>", "<LETTER>", "<DIRECTIVE_CHAR>", "<WORD>", "<BRACKETED_WORD>", "<ALPHA_CHAR>", "<ALPHANUM_CHAR>", "<IDENTIFIER_CHAR>", "<IDENTIFIER>", "<DOT>", "\"{\"", "\"}\"", "<REFERENCE_TERMINATOR>", "<DIRECTIVE_TERMINATOR>", "<EMPTY_INDEX>"};
}

