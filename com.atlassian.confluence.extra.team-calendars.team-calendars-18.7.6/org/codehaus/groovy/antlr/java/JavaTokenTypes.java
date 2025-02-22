/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

public interface JavaTokenTypes {
    public static final int EOF = 1;
    public static final int NULL_TREE_LOOKAHEAD = 3;
    public static final int BLOCK = 4;
    public static final int MODIFIERS = 5;
    public static final int OBJBLOCK = 6;
    public static final int SLIST = 7;
    public static final int METHOD_DEF = 8;
    public static final int VARIABLE_DEF = 9;
    public static final int INSTANCE_INIT = 10;
    public static final int STATIC_INIT = 11;
    public static final int TYPE = 12;
    public static final int CLASS_DEF = 13;
    public static final int INTERFACE_DEF = 14;
    public static final int PACKAGE_DEF = 15;
    public static final int ARRAY_DECLARATOR = 16;
    public static final int EXTENDS_CLAUSE = 17;
    public static final int IMPLEMENTS_CLAUSE = 18;
    public static final int PARAMETERS = 19;
    public static final int PARAMETER_DEF = 20;
    public static final int LABELED_STAT = 21;
    public static final int TYPECAST = 22;
    public static final int INDEX_OP = 23;
    public static final int POST_INC = 24;
    public static final int POST_DEC = 25;
    public static final int METHOD_CALL = 26;
    public static final int EXPR = 27;
    public static final int ARRAY_INIT = 28;
    public static final int IMPORT = 29;
    public static final int UNARY_MINUS = 30;
    public static final int UNARY_PLUS = 31;
    public static final int CASE_GROUP = 32;
    public static final int ELIST = 33;
    public static final int FOR_INIT = 34;
    public static final int FOR_CONDITION = 35;
    public static final int FOR_ITERATOR = 36;
    public static final int EMPTY_STAT = 37;
    public static final int FINAL = 38;
    public static final int ABSTRACT = 39;
    public static final int STRICTFP = 40;
    public static final int SUPER_CTOR_CALL = 41;
    public static final int CTOR_CALL = 42;
    public static final int VARIABLE_PARAMETER_DEF = 43;
    public static final int STATIC_IMPORT = 44;
    public static final int ENUM_DEF = 45;
    public static final int ENUM_CONSTANT_DEF = 46;
    public static final int FOR_EACH_CLAUSE = 47;
    public static final int ANNOTATION_DEF = 48;
    public static final int ANNOTATIONS = 49;
    public static final int ANNOTATION = 50;
    public static final int ANNOTATION_MEMBER_VALUE_PAIR = 51;
    public static final int ANNOTATION_FIELD_DEF = 52;
    public static final int ANNOTATION_ARRAY_INIT = 53;
    public static final int TYPE_ARGUMENTS = 54;
    public static final int TYPE_ARGUMENT = 55;
    public static final int TYPE_PARAMETERS = 56;
    public static final int TYPE_PARAMETER = 57;
    public static final int WILDCARD_TYPE = 58;
    public static final int TYPE_UPPER_BOUNDS = 59;
    public static final int TYPE_LOWER_BOUNDS = 60;
    public static final int LITERAL_package = 61;
    public static final int SEMI = 62;
    public static final int LITERAL_import = 63;
    public static final int LITERAL_static = 64;
    public static final int LBRACK = 65;
    public static final int RBRACK = 66;
    public static final int IDENT = 67;
    public static final int DOT = 68;
    public static final int QUESTION = 69;
    public static final int LITERAL_extends = 70;
    public static final int LITERAL_super = 71;
    public static final int LT = 72;
    public static final int GT = 73;
    public static final int COMMA = 74;
    public static final int SR = 75;
    public static final int BSR = 76;
    public static final int LITERAL_void = 77;
    public static final int LITERAL_boolean = 78;
    public static final int LITERAL_byte = 79;
    public static final int LITERAL_char = 80;
    public static final int LITERAL_short = 81;
    public static final int LITERAL_int = 82;
    public static final int LITERAL_float = 83;
    public static final int LITERAL_long = 84;
    public static final int LITERAL_double = 85;
    public static final int STAR = 86;
    public static final int LITERAL_private = 87;
    public static final int LITERAL_public = 88;
    public static final int LITERAL_protected = 89;
    public static final int LITERAL_transient = 90;
    public static final int LITERAL_native = 91;
    public static final int LITERAL_threadsafe = 92;
    public static final int LITERAL_synchronized = 93;
    public static final int LITERAL_volatile = 94;
    public static final int AT = 95;
    public static final int LPAREN = 96;
    public static final int RPAREN = 97;
    public static final int ASSIGN = 98;
    public static final int LCURLY = 99;
    public static final int RCURLY = 100;
    public static final int LITERAL_class = 101;
    public static final int LITERAL_interface = 102;
    public static final int LITERAL_enum = 103;
    public static final int BAND = 104;
    public static final int LITERAL_default = 105;
    public static final int LITERAL_implements = 106;
    public static final int LITERAL_this = 107;
    public static final int LITERAL_throws = 108;
    public static final int TRIPLE_DOT = 109;
    public static final int COLON = 110;
    public static final int LITERAL_if = 111;
    public static final int LITERAL_else = 112;
    public static final int LITERAL_while = 113;
    public static final int LITERAL_do = 114;
    public static final int LITERAL_break = 115;
    public static final int LITERAL_continue = 116;
    public static final int LITERAL_return = 117;
    public static final int LITERAL_switch = 118;
    public static final int LITERAL_throw = 119;
    public static final int LITERAL_assert = 120;
    public static final int LITERAL_for = 121;
    public static final int LITERAL_case = 122;
    public static final int LITERAL_try = 123;
    public static final int LITERAL_finally = 124;
    public static final int LITERAL_catch = 125;
    public static final int PLUS_ASSIGN = 126;
    public static final int MINUS_ASSIGN = 127;
    public static final int STAR_ASSIGN = 128;
    public static final int DIV_ASSIGN = 129;
    public static final int MOD_ASSIGN = 130;
    public static final int SR_ASSIGN = 131;
    public static final int BSR_ASSIGN = 132;
    public static final int SL_ASSIGN = 133;
    public static final int BAND_ASSIGN = 134;
    public static final int BXOR_ASSIGN = 135;
    public static final int BOR_ASSIGN = 136;
    public static final int LOR = 137;
    public static final int LAND = 138;
    public static final int BOR = 139;
    public static final int BXOR = 140;
    public static final int NOT_EQUAL = 141;
    public static final int EQUAL = 142;
    public static final int LE = 143;
    public static final int GE = 144;
    public static final int LITERAL_instanceof = 145;
    public static final int SL = 146;
    public static final int PLUS = 147;
    public static final int MINUS = 148;
    public static final int DIV = 149;
    public static final int MOD = 150;
    public static final int INC = 151;
    public static final int DEC = 152;
    public static final int BNOT = 153;
    public static final int LNOT = 154;
    public static final int LITERAL_true = 155;
    public static final int LITERAL_false = 156;
    public static final int LITERAL_null = 157;
    public static final int LITERAL_new = 158;
    public static final int NUM_INT = 159;
    public static final int CHAR_LITERAL = 160;
    public static final int STRING_LITERAL = 161;
    public static final int NUM_FLOAT = 162;
    public static final int NUM_LONG = 163;
    public static final int NUM_DOUBLE = 164;
    public static final int WS = 165;
    public static final int SL_COMMENT = 166;
    public static final int ML_COMMENT = 167;
    public static final int ESC = 168;
    public static final int HEX_DIGIT = 169;
    public static final int VOCAB = 170;
    public static final int EXPONENT = 171;
    public static final int FLOAT_SUFFIX = 172;
}

