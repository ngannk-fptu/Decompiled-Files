/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

public class Token {
    public static final boolean printTrees = false;
    static final boolean printICode = false;
    static final boolean printNames = false;
    public static final int ERROR = -1;
    public static final int EOF = 0;
    public static final int EOL = 1;
    public static final int FIRST_BYTECODE_TOKEN = 2;
    public static final int ENTERWITH = 2;
    public static final int LEAVEWITH = 3;
    public static final int RETURN = 4;
    public static final int GOTO = 5;
    public static final int IFEQ = 6;
    public static final int IFNE = 7;
    public static final int SETNAME = 8;
    public static final int BITOR = 9;
    public static final int BITXOR = 10;
    public static final int BITAND = 11;
    public static final int EQ = 12;
    public static final int NE = 13;
    public static final int LT = 14;
    public static final int LE = 15;
    public static final int GT = 16;
    public static final int GE = 17;
    public static final int LSH = 18;
    public static final int RSH = 19;
    public static final int URSH = 20;
    public static final int ADD = 21;
    public static final int SUB = 22;
    public static final int MUL = 23;
    public static final int DIV = 24;
    public static final int MOD = 25;
    public static final int NOT = 26;
    public static final int BITNOT = 27;
    public static final int POS = 28;
    public static final int NEG = 29;
    public static final int NEW = 30;
    public static final int DELPROP = 31;
    public static final int TYPEOF = 32;
    public static final int GETPROP = 33;
    public static final int GETPROPNOWARN = 34;
    public static final int SETPROP = 35;
    public static final int GETELEM = 36;
    public static final int SETELEM = 37;
    public static final int CALL = 38;
    public static final int NAME = 39;
    public static final int NUMBER = 40;
    public static final int STRING = 41;
    public static final int NULL = 42;
    public static final int THIS = 43;
    public static final int FALSE = 44;
    public static final int TRUE = 45;
    public static final int SHEQ = 46;
    public static final int SHNE = 47;
    public static final int REGEXP = 48;
    public static final int BINDNAME = 49;
    public static final int THROW = 50;
    public static final int RETHROW = 51;
    public static final int IN = 52;
    public static final int INSTANCEOF = 53;
    public static final int LOCAL_LOAD = 54;
    public static final int GETVAR = 55;
    public static final int SETVAR = 56;
    public static final int CATCH_SCOPE = 57;
    public static final int ENUM_INIT_KEYS = 58;
    public static final int ENUM_INIT_VALUES = 59;
    public static final int ENUM_INIT_ARRAY = 60;
    public static final int ENUM_INIT_VALUES_IN_ORDER = 61;
    public static final int ENUM_NEXT = 62;
    public static final int ENUM_ID = 63;
    public static final int THISFN = 64;
    public static final int RETURN_RESULT = 65;
    public static final int ARRAYLIT = 66;
    public static final int OBJECTLIT = 67;
    public static final int GET_REF = 68;
    public static final int SET_REF = 69;
    public static final int DEL_REF = 70;
    public static final int REF_CALL = 71;
    public static final int REF_SPECIAL = 72;
    public static final int YIELD = 73;
    public static final int STRICT_SETNAME = 74;
    public static final int EXP = 75;
    public static final int DEFAULTNAMESPACE = 76;
    public static final int ESCXMLATTR = 77;
    public static final int ESCXMLTEXT = 78;
    public static final int REF_MEMBER = 79;
    public static final int REF_NS_MEMBER = 80;
    public static final int REF_NAME = 81;
    public static final int REF_NS_NAME = 82;
    public static final int BIGINT = 83;
    public static final int LAST_BYTECODE_TOKEN = 83;
    public static final int TRY = 84;
    public static final int SEMI = 85;
    public static final int LB = 86;
    public static final int RB = 87;
    public static final int LC = 88;
    public static final int RC = 89;
    public static final int LP = 90;
    public static final int RP = 91;
    public static final int COMMA = 92;
    public static final int ASSIGN = 93;
    public static final int ASSIGN_BITOR = 94;
    public static final int ASSIGN_BITXOR = 95;
    public static final int ASSIGN_BITAND = 96;
    public static final int ASSIGN_LSH = 97;
    public static final int ASSIGN_RSH = 98;
    public static final int ASSIGN_URSH = 99;
    public static final int ASSIGN_ADD = 100;
    public static final int ASSIGN_SUB = 101;
    public static final int ASSIGN_MUL = 102;
    public static final int ASSIGN_DIV = 103;
    public static final int ASSIGN_MOD = 104;
    public static final int ASSIGN_EXP = 105;
    public static final int FIRST_ASSIGN = 93;
    public static final int LAST_ASSIGN = 105;
    public static final int HOOK = 106;
    public static final int COLON = 107;
    public static final int OR = 108;
    public static final int AND = 109;
    public static final int INC = 110;
    public static final int DEC = 111;
    public static final int DOT = 112;
    public static final int FUNCTION = 113;
    public static final int EXPORT = 114;
    public static final int IMPORT = 115;
    public static final int IF = 116;
    public static final int ELSE = 117;
    public static final int SWITCH = 118;
    public static final int CASE = 119;
    public static final int DEFAULT = 120;
    public static final int WHILE = 121;
    public static final int DO = 122;
    public static final int FOR = 123;
    public static final int BREAK = 124;
    public static final int CONTINUE = 125;
    public static final int VAR = 126;
    public static final int WITH = 127;
    public static final int CATCH = 128;
    public static final int FINALLY = 129;
    public static final int VOID = 130;
    public static final int RESERVED = 131;
    public static final int EMPTY = 132;
    public static final int BLOCK = 133;
    public static final int LABEL = 134;
    public static final int TARGET = 135;
    public static final int LOOP = 136;
    public static final int EXPR_VOID = 137;
    public static final int EXPR_RESULT = 138;
    public static final int JSR = 139;
    public static final int SCRIPT = 140;
    public static final int TYPEOFNAME = 141;
    public static final int USE_STACK = 142;
    public static final int SETPROP_OP = 143;
    public static final int SETELEM_OP = 144;
    public static final int LOCAL_BLOCK = 145;
    public static final int SET_REF_OP = 146;
    public static final int DOTDOT = 147;
    public static final int COLONCOLON = 148;
    public static final int XML = 149;
    public static final int DOTQUERY = 150;
    public static final int XMLATTR = 151;
    public static final int XMLEND = 152;
    public static final int TO_OBJECT = 153;
    public static final int TO_DOUBLE = 154;
    public static final int GET = 155;
    public static final int SET = 156;
    public static final int LET = 157;
    public static final int CONST = 158;
    public static final int SETCONST = 159;
    public static final int SETCONSTVAR = 160;
    public static final int ARRAYCOMP = 161;
    public static final int LETEXPR = 162;
    public static final int WITHEXPR = 163;
    public static final int DEBUGGER = 164;
    public static final int COMMENT = 165;
    public static final int GENEXPR = 166;
    public static final int METHOD = 167;
    public static final int ARROW = 168;
    public static final int YIELD_STAR = 169;
    public static final int TEMPLATE_LITERAL = 170;
    public static final int TEMPLATE_CHARS = 171;
    public static final int TEMPLATE_LITERAL_SUBST = 172;
    public static final int TAGGED_TEMPLATE_LITERAL = 173;
    public static final int LAST_TOKEN = 173;

    public static String name(int token) {
        return String.valueOf(token);
    }

    public static String typeToName(int token) {
        switch (token) {
            case -1: {
                return "ERROR";
            }
            case 0: {
                return "EOF";
            }
            case 1: {
                return "EOL";
            }
            case 2: {
                return "ENTERWITH";
            }
            case 3: {
                return "LEAVEWITH";
            }
            case 4: {
                return "RETURN";
            }
            case 5: {
                return "GOTO";
            }
            case 6: {
                return "IFEQ";
            }
            case 7: {
                return "IFNE";
            }
            case 8: {
                return "SETNAME";
            }
            case 9: {
                return "BITOR";
            }
            case 10: {
                return "BITXOR";
            }
            case 11: {
                return "BITAND";
            }
            case 12: {
                return "EQ";
            }
            case 13: {
                return "NE";
            }
            case 14: {
                return "LT";
            }
            case 15: {
                return "LE";
            }
            case 16: {
                return "GT";
            }
            case 17: {
                return "GE";
            }
            case 18: {
                return "LSH";
            }
            case 19: {
                return "RSH";
            }
            case 20: {
                return "URSH";
            }
            case 21: {
                return "ADD";
            }
            case 22: {
                return "SUB";
            }
            case 23: {
                return "MUL";
            }
            case 24: {
                return "DIV";
            }
            case 25: {
                return "MOD";
            }
            case 26: {
                return "NOT";
            }
            case 27: {
                return "BITNOT";
            }
            case 28: {
                return "POS";
            }
            case 29: {
                return "NEG";
            }
            case 30: {
                return "NEW";
            }
            case 31: {
                return "DELPROP";
            }
            case 32: {
                return "TYPEOF";
            }
            case 33: {
                return "GETPROP";
            }
            case 34: {
                return "GETPROPNOWARN";
            }
            case 35: {
                return "SETPROP";
            }
            case 36: {
                return "GETELEM";
            }
            case 37: {
                return "SETELEM";
            }
            case 38: {
                return "CALL";
            }
            case 39: {
                return "NAME";
            }
            case 40: {
                return "NUMBER";
            }
            case 41: {
                return "STRING";
            }
            case 42: {
                return "NULL";
            }
            case 43: {
                return "THIS";
            }
            case 44: {
                return "FALSE";
            }
            case 45: {
                return "TRUE";
            }
            case 46: {
                return "SHEQ";
            }
            case 47: {
                return "SHNE";
            }
            case 48: {
                return "REGEXP";
            }
            case 49: {
                return "BINDNAME";
            }
            case 50: {
                return "THROW";
            }
            case 51: {
                return "RETHROW";
            }
            case 52: {
                return "IN";
            }
            case 53: {
                return "INSTANCEOF";
            }
            case 54: {
                return "LOCAL_LOAD";
            }
            case 55: {
                return "GETVAR";
            }
            case 56: {
                return "SETVAR";
            }
            case 57: {
                return "CATCH_SCOPE";
            }
            case 58: {
                return "ENUM_INIT_KEYS";
            }
            case 59: {
                return "ENUM_INIT_VALUES";
            }
            case 60: {
                return "ENUM_INIT_ARRAY";
            }
            case 61: {
                return "ENUM_INIT_VALUES_IN_ORDER";
            }
            case 62: {
                return "ENUM_NEXT";
            }
            case 63: {
                return "ENUM_ID";
            }
            case 64: {
                return "THISFN";
            }
            case 65: {
                return "RETURN_RESULT";
            }
            case 66: {
                return "ARRAYLIT";
            }
            case 67: {
                return "OBJECTLIT";
            }
            case 68: {
                return "GET_REF";
            }
            case 69: {
                return "SET_REF";
            }
            case 70: {
                return "DEL_REF";
            }
            case 71: {
                return "REF_CALL";
            }
            case 72: {
                return "REF_SPECIAL";
            }
            case 76: {
                return "DEFAULTNAMESPACE";
            }
            case 78: {
                return "ESCXMLTEXT";
            }
            case 77: {
                return "ESCXMLATTR";
            }
            case 79: {
                return "REF_MEMBER";
            }
            case 80: {
                return "REF_NS_MEMBER";
            }
            case 81: {
                return "REF_NAME";
            }
            case 82: {
                return "REF_NS_NAME";
            }
            case 84: {
                return "TRY";
            }
            case 85: {
                return "SEMI";
            }
            case 86: {
                return "LB";
            }
            case 87: {
                return "RB";
            }
            case 88: {
                return "LC";
            }
            case 89: {
                return "RC";
            }
            case 90: {
                return "LP";
            }
            case 91: {
                return "RP";
            }
            case 92: {
                return "COMMA";
            }
            case 93: {
                return "ASSIGN";
            }
            case 94: {
                return "ASSIGN_BITOR";
            }
            case 95: {
                return "ASSIGN_BITXOR";
            }
            case 96: {
                return "ASSIGN_BITAND";
            }
            case 97: {
                return "ASSIGN_LSH";
            }
            case 98: {
                return "ASSIGN_RSH";
            }
            case 99: {
                return "ASSIGN_URSH";
            }
            case 100: {
                return "ASSIGN_ADD";
            }
            case 101: {
                return "ASSIGN_SUB";
            }
            case 102: {
                return "ASSIGN_MUL";
            }
            case 103: {
                return "ASSIGN_DIV";
            }
            case 104: {
                return "ASSIGN_MOD";
            }
            case 105: {
                return "ASSIGN_EXP";
            }
            case 106: {
                return "HOOK";
            }
            case 107: {
                return "COLON";
            }
            case 108: {
                return "OR";
            }
            case 109: {
                return "AND";
            }
            case 110: {
                return "INC";
            }
            case 111: {
                return "DEC";
            }
            case 112: {
                return "DOT";
            }
            case 113: {
                return "FUNCTION";
            }
            case 114: {
                return "EXPORT";
            }
            case 115: {
                return "IMPORT";
            }
            case 116: {
                return "IF";
            }
            case 117: {
                return "ELSE";
            }
            case 118: {
                return "SWITCH";
            }
            case 119: {
                return "CASE";
            }
            case 120: {
                return "DEFAULT";
            }
            case 121: {
                return "WHILE";
            }
            case 122: {
                return "DO";
            }
            case 123: {
                return "FOR";
            }
            case 124: {
                return "BREAK";
            }
            case 125: {
                return "CONTINUE";
            }
            case 126: {
                return "VAR";
            }
            case 127: {
                return "WITH";
            }
            case 128: {
                return "CATCH";
            }
            case 129: {
                return "FINALLY";
            }
            case 130: {
                return "VOID";
            }
            case 131: {
                return "RESERVED";
            }
            case 132: {
                return "EMPTY";
            }
            case 133: {
                return "BLOCK";
            }
            case 134: {
                return "LABEL";
            }
            case 135: {
                return "TARGET";
            }
            case 136: {
                return "LOOP";
            }
            case 137: {
                return "EXPR_VOID";
            }
            case 138: {
                return "EXPR_RESULT";
            }
            case 139: {
                return "JSR";
            }
            case 140: {
                return "SCRIPT";
            }
            case 141: {
                return "TYPEOFNAME";
            }
            case 142: {
                return "USE_STACK";
            }
            case 143: {
                return "SETPROP_OP";
            }
            case 144: {
                return "SETELEM_OP";
            }
            case 145: {
                return "LOCAL_BLOCK";
            }
            case 146: {
                return "SET_REF_OP";
            }
            case 147: {
                return "DOTDOT";
            }
            case 148: {
                return "COLONCOLON";
            }
            case 149: {
                return "XML";
            }
            case 150: {
                return "DOTQUERY";
            }
            case 151: {
                return "XMLATTR";
            }
            case 152: {
                return "XMLEND";
            }
            case 153: {
                return "TO_OBJECT";
            }
            case 154: {
                return "TO_DOUBLE";
            }
            case 155: {
                return "GET";
            }
            case 156: {
                return "SET";
            }
            case 157: {
                return "LET";
            }
            case 73: {
                return "YIELD";
            }
            case 75: {
                return "EXP";
            }
            case 158: {
                return "CONST";
            }
            case 159: {
                return "SETCONST";
            }
            case 161: {
                return "ARRAYCOMP";
            }
            case 163: {
                return "WITHEXPR";
            }
            case 162: {
                return "LETEXPR";
            }
            case 164: {
                return "DEBUGGER";
            }
            case 165: {
                return "COMMENT";
            }
            case 166: {
                return "GENEXPR";
            }
            case 167: {
                return "METHOD";
            }
            case 168: {
                return "ARROW";
            }
            case 169: {
                return "YIELD_STAR";
            }
            case 83: {
                return "BIGINT";
            }
            case 170: {
                return "TEMPLATE_LITERAL";
            }
            case 171: {
                return "TEMPLATE_CHARS";
            }
            case 172: {
                return "TEMPLATE_LITERAL_SUBST";
            }
            case 173: {
                return "TAGGED_TEMPLATE_LITERAL";
            }
        }
        throw new IllegalStateException(String.valueOf(token));
    }

    public static String keywordToName(int token) {
        switch (token) {
            case 124: {
                return "break";
            }
            case 119: {
                return "case";
            }
            case 125: {
                return "continue";
            }
            case 120: {
                return "default";
            }
            case 31: {
                return "delete";
            }
            case 122: {
                return "do";
            }
            case 117: {
                return "else";
            }
            case 44: {
                return "false";
            }
            case 123: {
                return "for";
            }
            case 113: {
                return "function";
            }
            case 116: {
                return "if";
            }
            case 52: {
                return "in";
            }
            case 157: {
                return "let";
            }
            case 30: {
                return "new";
            }
            case 42: {
                return "null";
            }
            case 4: {
                return "return";
            }
            case 118: {
                return "switch";
            }
            case 43: {
                return "this";
            }
            case 45: {
                return "true";
            }
            case 32: {
                return "typeof";
            }
            case 126: {
                return "var";
            }
            case 130: {
                return "void";
            }
            case 121: {
                return "while";
            }
            case 127: {
                return "with";
            }
            case 73: {
                return "yield";
            }
            case 128: {
                return "catch";
            }
            case 158: {
                return "const";
            }
            case 164: {
                return "debugger";
            }
            case 129: {
                return "finally";
            }
            case 53: {
                return "instanceof";
            }
            case 50: {
                return "throw";
            }
            case 84: {
                return "try";
            }
        }
        return null;
    }

    public static boolean isValidToken(int code) {
        return code >= -1 && code <= 173;
    }

    public static enum CommentType {
        LINE,
        BLOCK_COMMENT,
        JSDOC,
        HTML;

    }
}

