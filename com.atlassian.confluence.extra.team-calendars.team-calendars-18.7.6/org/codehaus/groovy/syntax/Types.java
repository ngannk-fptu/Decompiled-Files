/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.syntax.CSTNode;

public class Types {
    public static final int EOF = -1;
    public static final int UNKNOWN = 0;
    public static final int NEWLINE = 5;
    public static final int LEFT_CURLY_BRACE = 10;
    public static final int RIGHT_CURLY_BRACE = 20;
    public static final int LEFT_SQUARE_BRACKET = 30;
    public static final int RIGHT_SQUARE_BRACKET = 40;
    public static final int LEFT_PARENTHESIS = 50;
    public static final int RIGHT_PARENTHESIS = 60;
    public static final int DOT = 70;
    public static final int DOT_DOT = 75;
    public static final int DOT_DOT_DOT = 77;
    public static final int NAVIGATE = 80;
    public static final int FIND_REGEX = 90;
    public static final int MATCH_REGEX = 94;
    public static final int REGEX_PATTERN = 97;
    public static final int EQUAL = 100;
    public static final int EQUALS = 100;
    public static final int ASSIGN = 100;
    public static final int COMPARE_NOT_EQUAL = 120;
    public static final int COMPARE_IDENTICAL = 121;
    public static final int COMPARE_NOT_IDENTICAL = 122;
    public static final int COMPARE_EQUAL = 123;
    public static final int COMPARE_LESS_THAN = 124;
    public static final int COMPARE_LESS_THAN_EQUAL = 125;
    public static final int COMPARE_GREATER_THAN = 126;
    public static final int COMPARE_GREATER_THAN_EQUAL = 127;
    public static final int COMPARE_TO = 128;
    public static final int NOT = 160;
    public static final int LOGICAL_OR = 162;
    public static final int LOGICAL_AND = 164;
    public static final int LOGICAL_OR_EQUAL = 166;
    public static final int LOGICAL_AND_EQUAL = 168;
    public static final int PLUS = 200;
    public static final int MINUS = 201;
    public static final int MULTIPLY = 202;
    public static final int DIVIDE = 203;
    public static final int INTDIV = 204;
    public static final int MOD = 205;
    public static final int STAR_STAR = 206;
    public static final int POWER = 206;
    public static final int PLUS_EQUAL = 210;
    public static final int MINUS_EQUAL = 211;
    public static final int MULTIPLY_EQUAL = 212;
    public static final int DIVIDE_EQUAL = 213;
    public static final int INTDIV_EQUAL = 214;
    public static final int MOD_EQUAL = 215;
    public static final int POWER_EQUAL = 216;
    public static final int PLUS_PLUS = 250;
    public static final int PREFIX_PLUS_PLUS = 251;
    public static final int POSTFIX_PLUS_PLUS = 252;
    public static final int PREFIX_PLUS = 253;
    public static final int MINUS_MINUS = 260;
    public static final int PREFIX_MINUS_MINUS = 261;
    public static final int POSTFIX_MINUS_MINUS = 262;
    public static final int PREFIX_MINUS = 263;
    public static final int LEFT_SHIFT = 280;
    public static final int RIGHT_SHIFT = 281;
    public static final int RIGHT_SHIFT_UNSIGNED = 282;
    public static final int LEFT_SHIFT_EQUAL = 285;
    public static final int RIGHT_SHIFT_EQUAL = 286;
    public static final int RIGHT_SHIFT_UNSIGNED_EQUAL = 287;
    public static final int STAR = 202;
    public static final int COMMA = 300;
    public static final int COLON = 310;
    public static final int SEMICOLON = 320;
    public static final int QUESTION = 330;
    public static final int PIPE = 340;
    public static final int DOUBLE_PIPE = 162;
    public static final int BITWISE_OR = 340;
    public static final int BITWISE_AND = 341;
    public static final int BITWISE_XOR = 342;
    public static final int BITWISE_OR_EQUAL = 350;
    public static final int BITWISE_AND_EQUAL = 351;
    public static final int BITWISE_XOR_EQUAL = 352;
    public static final int BITWISE_NEGATION = 97;
    public static final int STRING = 400;
    public static final int IDENTIFIER = 440;
    public static final int INTEGER_NUMBER = 450;
    public static final int DECIMAL_NUMBER = 451;
    public static final int KEYWORD_PRIVATE = 500;
    public static final int KEYWORD_PROTECTED = 501;
    public static final int KEYWORD_PUBLIC = 502;
    public static final int KEYWORD_ABSTRACT = 510;
    public static final int KEYWORD_FINAL = 511;
    public static final int KEYWORD_NATIVE = 512;
    public static final int KEYWORD_TRANSIENT = 513;
    public static final int KEYWORD_VOLATILE = 514;
    public static final int KEYWORD_SYNCHRONIZED = 520;
    public static final int KEYWORD_STATIC = 521;
    public static final int KEYWORD_DEF = 530;
    public static final int KEYWORD_DEFMACRO = 539;
    public static final int KEYWORD_CLASS = 531;
    public static final int KEYWORD_INTERFACE = 532;
    public static final int KEYWORD_MIXIN = 533;
    public static final int KEYWORD_IMPLEMENTS = 540;
    public static final int KEYWORD_EXTENDS = 541;
    public static final int KEYWORD_THIS = 542;
    public static final int KEYWORD_SUPER = 543;
    public static final int KEYWORD_INSTANCEOF = 544;
    public static final int KEYWORD_PROPERTY = 545;
    public static final int KEYWORD_NEW = 546;
    public static final int KEYWORD_PACKAGE = 550;
    public static final int KEYWORD_IMPORT = 551;
    public static final int KEYWORD_AS = 552;
    public static final int KEYWORD_RETURN = 560;
    public static final int KEYWORD_IF = 561;
    public static final int KEYWORD_ELSE = 562;
    public static final int KEYWORD_DO = 570;
    public static final int KEYWORD_WHILE = 571;
    public static final int KEYWORD_FOR = 572;
    public static final int KEYWORD_IN = 573;
    public static final int KEYWORD_BREAK = 574;
    public static final int KEYWORD_CONTINUE = 575;
    public static final int KEYWORD_SWITCH = 576;
    public static final int KEYWORD_CASE = 577;
    public static final int KEYWORD_DEFAULT = 578;
    public static final int KEYWORD_TRY = 580;
    public static final int KEYWORD_CATCH = 581;
    public static final int KEYWORD_FINALLY = 582;
    public static final int KEYWORD_THROW = 583;
    public static final int KEYWORD_THROWS = 584;
    public static final int KEYWORD_ASSERT = 585;
    public static final int KEYWORD_VOID = 600;
    public static final int KEYWORD_BOOLEAN = 601;
    public static final int KEYWORD_BYTE = 602;
    public static final int KEYWORD_SHORT = 603;
    public static final int KEYWORD_INT = 604;
    public static final int KEYWORD_LONG = 605;
    public static final int KEYWORD_FLOAT = 606;
    public static final int KEYWORD_DOUBLE = 607;
    public static final int KEYWORD_CHAR = 608;
    public static final int KEYWORD_TRUE = 610;
    public static final int KEYWORD_FALSE = 611;
    public static final int KEYWORD_NULL = 612;
    public static final int KEYWORD_CONST = 700;
    public static final int KEYWORD_GOTO = 701;
    public static final int SYNTH_COMPILATION_UNIT = 800;
    public static final int SYNTH_CLASS = 801;
    public static final int SYNTH_INTERFACE = 802;
    public static final int SYNTH_MIXIN = 803;
    public static final int SYNTH_METHOD = 804;
    public static final int SYNTH_PROPERTY = 805;
    public static final int SYNTH_PARAMETER_DECLARATION = 806;
    public static final int SYNTH_LIST = 810;
    public static final int SYNTH_MAP = 811;
    public static final int SYNTH_GSTRING = 812;
    public static final int SYNTH_METHOD_CALL = 814;
    public static final int SYNTH_CAST = 815;
    public static final int SYNTH_BLOCK = 816;
    public static final int SYNTH_CLOSURE = 817;
    public static final int SYNTH_LABEL = 818;
    public static final int SYNTH_TERNARY = 819;
    public static final int SYNTH_TUPLE = 820;
    public static final int SYNTH_VARIABLE_DECLARATION = 830;
    public static final int GSTRING_START = 901;
    public static final int GSTRING_END = 902;
    public static final int GSTRING_EXPRESSION_START = 903;
    public static final int GSTRING_EXPRESSION_END = 904;
    public static final int ANY = 1000;
    public static final int NOT_EOF = 1001;
    public static final int GENERAL_END_OF_STATEMENT = 1002;
    public static final int ANY_END_OF_STATEMENT = 1003;
    public static final int ASSIGNMENT_OPERATOR = 1100;
    public static final int COMPARISON_OPERATOR = 1101;
    public static final int MATH_OPERATOR = 1102;
    public static final int LOGICAL_OPERATOR = 1103;
    public static final int RANGE_OPERATOR = 1104;
    public static final int REGEX_COMPARISON_OPERATOR = 1105;
    public static final int DEREFERENCE_OPERATOR = 1106;
    public static final int BITWISE_OPERATOR = 1107;
    public static final int PREFIX_OPERATOR = 1200;
    public static final int POSTFIX_OPERATOR = 1210;
    public static final int INFIX_OPERATOR = 1220;
    public static final int PREFIX_OR_INFIX_OPERATOR = 1230;
    public static final int PURE_PREFIX_OPERATOR = 1235;
    public static final int KEYWORD = 1300;
    public static final int SYMBOL = 1301;
    public static final int LITERAL = 1310;
    public static final int NUMBER = 1320;
    public static final int SIGN = 1325;
    public static final int NAMED_VALUE = 1330;
    public static final int TRUTH_VALUE = 1331;
    public static final int PRIMITIVE_TYPE = 1340;
    public static final int CREATABLE_PRIMITIVE_TYPE = 1341;
    public static final int LOOP = 1350;
    public static final int RESERVED_KEYWORD = 1360;
    public static final int KEYWORD_IDENTIFIER = 1361;
    public static final int SYNTHETIC = 1370;
    public static final int TYPE_DECLARATION = 1400;
    public static final int DECLARATION_MODIFIER = 1410;
    public static final int TYPE_NAME = 1420;
    public static final int CREATABLE_TYPE_NAME = 1430;
    public static final int MATCHED_CONTAINER = 1500;
    public static final int LEFT_OF_MATCHED_CONTAINER = 1501;
    public static final int RIGHT_OF_MATCHED_CONTAINER = 1502;
    public static final int EXPRESSION = 1900;
    public static final int OPERATOR_EXPRESSION = 1901;
    public static final int SYNTH_EXPRESSION = 1902;
    public static final int KEYWORD_EXPRESSION = 1903;
    public static final int LITERAL_EXPRESSION = 1904;
    public static final int ARRAY_EXPRESSION = 1905;
    public static final int SIMPLE_EXPRESSION = 1910;
    public static final int COMPLEX_EXPRESSION = 1911;
    public static final int PARAMETER_TERMINATORS = 2000;
    public static final int ARRAY_ITEM_TERMINATORS = 2001;
    public static final int TYPE_LIST_TERMINATORS = 2002;
    public static final int OPTIONAL_DATATYPE_FOLLOWERS = 2003;
    public static final int SWITCH_BLOCK_TERMINATORS = 2004;
    public static final int SWITCH_ENTRIES = 2005;
    public static final int METHOD_CALL_STARTERS = 2006;
    public static final int UNSAFE_OVER_NEWLINES = 2007;
    public static final int PRECLUDES_CAST_OPERATOR = 2008;
    private static final Map<Integer, String> TEXTS = new HashMap<Integer, String>();
    private static final Map<String, Integer> LOOKUP = new HashMap<String, Integer>();
    private static final Set<String> KEYWORDS = new HashSet<String>();
    private static final Map<Integer, String> DESCRIPTIONS;

    public static boolean ofType(int specific, int general) {
        if (general == specific) {
            return true;
        }
        switch (general) {
            case 1000: {
                return true;
            }
            case 1001: {
                return specific >= 0 && specific <= 830;
            }
            case 1002: {
                switch (specific) {
                    case -1: 
                    case 5: 
                    case 320: {
                        return true;
                    }
                }
                break;
            }
            case 1003: {
                switch (specific) {
                    case -1: 
                    case 5: 
                    case 20: 
                    case 320: {
                        return true;
                    }
                }
                break;
            }
            case 1100: {
                return specific == 100 || specific >= 210 && specific <= 216 || specific >= 166 && specific <= 168 || specific >= 285 && specific <= 287 || specific >= 350 && specific <= 352;
            }
            case 1101: {
                return specific >= 120 && specific <= 128;
            }
            case 1102: {
                return specific >= 200 && specific <= 282 || specific >= 160 && specific <= 164 || specific >= 340 && specific <= 342;
            }
            case 1103: {
                return specific >= 160 && specific <= 164;
            }
            case 1107: {
                return specific >= 340 && specific <= 342 || specific == 97;
            }
            case 1104: {
                return specific == 75 || specific == 77;
            }
            case 1105: {
                return specific == 90 || specific == 94;
            }
            case 1106: {
                return specific == 70 || specific == 80;
            }
            case 1200: {
                switch (specific) {
                    case 201: 
                    case 250: 
                    case 260: {
                        return true;
                    }
                }
            }
            case 1235: {
                switch (specific) {
                    case 97: 
                    case 160: 
                    case 251: 
                    case 253: 
                    case 261: 
                    case 263: 
                    case 815: {
                        return true;
                    }
                }
                break;
            }
            case 1210: {
                switch (specific) {
                    case 250: 
                    case 252: 
                    case 260: 
                    case 262: {
                        return true;
                    }
                }
                break;
            }
            case 1220: {
                switch (specific) {
                    case 70: 
                    case 75: 
                    case 77: 
                    case 80: 
                    case 90: 
                    case 94: 
                    case 162: 
                    case 164: 
                    case 280: 
                    case 281: 
                    case 282: 
                    case 340: 
                    case 341: 
                    case 342: 
                    case 544: {
                        return true;
                    }
                }
                return specific >= 120 && specific <= 128 || specific >= 200 && specific <= 215 || specific == 100 || specific >= 210 && specific <= 216 || specific >= 166 && specific <= 168 || specific >= 285 && specific <= 287 || specific >= 350 && specific <= 352;
            }
            case 1230: {
                switch (specific) {
                    case 200: 
                    case 201: 
                    case 206: 
                    case 253: 
                    case 263: {
                        return true;
                    }
                }
                break;
            }
            case 1300: {
                return specific >= 500 && specific <= 701;
            }
            case 1301: {
                return specific >= 5 && specific <= 340;
            }
            case 1310: {
                return specific >= 400 && specific <= 451;
            }
            case 1320: {
                return specific == 450 || specific == 451;
            }
            case 1325: {
                switch (specific) {
                    case 200: 
                    case 201: {
                        return true;
                    }
                }
                break;
            }
            case 1330: {
                return specific >= 610 && specific <= 612;
            }
            case 1331: {
                return specific == 610 || specific == 611;
            }
            case 1420: {
                if (specific == 440) {
                    return true;
                }
            }
            case 1340: {
                return specific >= 600 && specific <= 608;
            }
            case 1430: {
                if (specific == 440) {
                    return true;
                }
            }
            case 1341: {
                return specific >= 601 && specific <= 608;
            }
            case 1350: {
                switch (specific) {
                    case 570: 
                    case 571: 
                    case 572: {
                        return true;
                    }
                }
                break;
            }
            case 1360: {
                return specific >= 700 && specific <= 701;
            }
            case 1361: {
                switch (specific) {
                    case 530: 
                    case 531: 
                    case 532: 
                    case 533: 
                    case 539: 
                    case 545: 
                    case 573: {
                        return true;
                    }
                }
                break;
            }
            case 1370: {
                return specific >= 800 && specific <= 830;
            }
            case 1400: {
                return specific >= 531 && specific <= 533;
            }
            case 1410: {
                return specific >= 500 && specific <= 521;
            }
            case 1500: {
                switch (specific) {
                    case 10: 
                    case 20: 
                    case 30: 
                    case 40: 
                    case 50: 
                    case 60: {
                        return true;
                    }
                }
                break;
            }
            case 1501: {
                switch (specific) {
                    case 10: 
                    case 30: 
                    case 50: {
                        return true;
                    }
                }
                break;
            }
            case 1502: {
                switch (specific) {
                    case 20: 
                    case 40: 
                    case 60: {
                        return true;
                    }
                }
                break;
            }
            case 2000: {
                return specific == 60 || specific == 300;
            }
            case 2001: {
                return specific == 40 || specific == 300;
            }
            case 2002: {
                switch (specific) {
                    case 10: 
                    case 300: 
                    case 540: 
                    case 584: {
                        return true;
                    }
                }
                break;
            }
            case 2003: {
                switch (specific) {
                    case 30: 
                    case 70: 
                    case 440: {
                        return true;
                    }
                }
                break;
            }
            case 2004: {
                if (specific == 20) {
                    return true;
                }
            }
            case 2005: {
                return specific == 577 || specific == 578;
            }
            case 2006: {
                if (specific >= 400 && specific <= 451) {
                    return true;
                }
                switch (specific) {
                    case 50: 
                    case 546: 
                    case 812: 
                    case 901: {
                        return true;
                    }
                }
                break;
            }
            case 2007: {
                if (Types.ofType(specific, 1301)) {
                    switch (specific) {
                        case 10: 
                        case 30: 
                        case 50: 
                        case 97: 
                        case 160: 
                        case 200: 
                        case 201: 
                        case 250: 
                        case 260: {
                            return true;
                        }
                    }
                    return false;
                }
                switch (specific) {
                    case 544: 
                    case 902: 
                    case 903: 
                    case 904: {
                        return false;
                    }
                }
                return true;
            }
            case 2008: {
                switch (specific) {
                    case 50: 
                    case 200: 
                    case 201: 
                    case 251: 
                    case 253: 
                    case 261: 
                    case 263: {
                        return false;
                    }
                }
                return !Types.ofType(specific, 1911);
            }
            case 1901: {
                return specific >= 70 && specific <= 282;
            }
            case 1902: {
                switch (specific) {
                    case 815: 
                    case 817: 
                    case 819: {
                        return true;
                    }
                }
                break;
            }
            case 1903: {
                switch (specific) {
                    case 542: 
                    case 543: 
                    case 544: 
                    case 546: 
                    case 610: 
                    case 611: 
                    case 612: {
                        return true;
                    }
                }
                break;
            }
            case 1904: {
                return specific >= 400 && specific <= 451;
            }
            case 1905: {
                return specific == 30;
            }
            case 1900: {
                if (specific >= 70 && specific <= 282) {
                    return true;
                }
                if (specific >= 400 && specific <= 451) {
                    return true;
                }
                switch (specific) {
                    case 30: 
                    case 542: 
                    case 543: 
                    case 544: 
                    case 546: 
                    case 610: 
                    case 611: 
                    case 612: 
                    case 812: 
                    case 815: 
                    case 817: 
                    case 819: {
                        return true;
                    }
                }
                break;
            }
            case 1911: {
                switch (specific) {
                    case 546: 
                    case 810: 
                    case 811: 
                    case 812: 
                    case 814: 
                    case 817: 
                    case 819: 
                    case 830: {
                        return true;
                    }
                }
            }
            case 1910: {
                if (specific >= 400 && specific <= 451) {
                    return true;
                }
                switch (specific) {
                    case 542: 
                    case 543: 
                    case 610: 
                    case 611: 
                    case 612: {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean canMean(int actual, int preferred) {
        if (actual == preferred) {
            return true;
        }
        switch (preferred) {
            case 440: 
            case 806: {
                switch (actual) {
                    case 440: 
                    case 530: 
                    case 531: 
                    case 532: 
                    case 533: 
                    case 539: {
                        return true;
                    }
                }
                break;
            }
            case 801: 
            case 802: 
            case 803: 
            case 804: 
            case 805: {
                return actual == 440;
            }
            case 810: 
            case 811: {
                return actual == 30;
            }
            case 815: {
                return actual == 50;
            }
            case 816: 
            case 817: {
                return actual == 10;
            }
            case 818: {
                return actual == 310;
            }
            case 830: {
                return actual == 440;
            }
        }
        return false;
    }

    public static void makePrefix(CSTNode node, boolean throwIfInvalid) {
        switch (node.getMeaning()) {
            case 200: {
                node.setMeaning(253);
                break;
            }
            case 201: {
                node.setMeaning(263);
                break;
            }
            case 250: {
                node.setMeaning(251);
                break;
            }
            case 260: {
                node.setMeaning(261);
                break;
            }
            default: {
                if (!throwIfInvalid) break;
                throw new GroovyBugError("cannot convert to prefix for type [" + node.getMeaning() + "]");
            }
        }
    }

    public static void makePostfix(CSTNode node, boolean throwIfInvalid) {
        switch (node.getMeaning()) {
            case 250: {
                node.setMeaning(252);
                break;
            }
            case 260: {
                node.setMeaning(262);
                break;
            }
            default: {
                if (!throwIfInvalid) break;
                throw new GroovyBugError("cannot convert to postfix for type [" + node.getMeaning() + "]");
            }
        }
    }

    public static int getPrecedence(int type, boolean throwIfInvalid) {
        switch (type) {
            case 50: {
                return 0;
            }
            case 100: 
            case 166: 
            case 168: 
            case 210: 
            case 211: 
            case 212: 
            case 213: 
            case 214: 
            case 215: 
            case 216: 
            case 285: 
            case 286: 
            case 287: 
            case 350: 
            case 351: 
            case 352: {
                return 5;
            }
            case 330: {
                return 10;
            }
            case 162: {
                return 15;
            }
            case 164: {
                return 20;
            }
            case 340: 
            case 341: 
            case 342: {
                return 22;
            }
            case 121: 
            case 122: {
                return 24;
            }
            case 90: 
            case 94: 
            case 120: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 127: 
            case 128: 
            case 544: {
                return 25;
            }
            case 75: 
            case 77: {
                return 30;
            }
            case 280: 
            case 281: 
            case 282: {
                return 35;
            }
            case 200: 
            case 201: {
                return 40;
            }
            case 202: 
            case 203: 
            case 204: 
            case 205: {
                return 45;
            }
            case 97: 
            case 160: {
                return 50;
            }
            case 815: {
                return 55;
            }
            case 250: 
            case 251: 
            case 252: 
            case 260: 
            case 261: 
            case 262: {
                return 65;
            }
            case 253: 
            case 263: {
                return 70;
            }
            case 206: {
                return 72;
            }
            case 30: 
            case 804: {
                return 75;
            }
            case 70: 
            case 80: {
                return 80;
            }
            case 546: {
                return 85;
            }
        }
        if (throwIfInvalid) {
            throw new GroovyBugError("precedence requested for non-operator");
        }
        return -1;
    }

    public static Collection<String> getKeywords() {
        return Collections.unmodifiableSet(KEYWORDS);
    }

    public static boolean isKeyword(String text) {
        return KEYWORDS.contains(text);
    }

    public static int lookup(String text, int filter) {
        int type = 0;
        if (LOOKUP.containsKey(text)) {
            type = LOOKUP.get(text);
            if (filter != 0 && !Types.ofType(type, filter)) {
                type = 0;
            }
        }
        return type;
    }

    public static int lookupKeyword(String text) {
        return Types.lookup(text, 1300);
    }

    public static int lookupSymbol(String text) {
        return Types.lookup(text, 1301);
    }

    public static String getText(int type) {
        String text = "";
        if (TEXTS.containsKey(type)) {
            text = TEXTS.get(type);
        }
        return text;
    }

    private static void addTranslation(String text, int type) {
        TEXTS.put(type, text);
        LOOKUP.put(text, type);
    }

    private static void addKeyword(String text, int type) {
        KEYWORDS.add(text);
        Types.addTranslation(text, type);
    }

    public static String getDescription(int type) {
        if (DESCRIPTIONS.containsKey(type)) {
            return DESCRIPTIONS.get(type);
        }
        return "<>";
    }

    private static void addDescription(int type, String description) {
        if (description.startsWith("<") && description.endsWith(">")) {
            DESCRIPTIONS.put(type, description);
        } else {
            DESCRIPTIONS.put(type, '\"' + description + '\"');
        }
    }

    static {
        Types.addTranslation("\n", 5);
        Types.addTranslation("{", 10);
        Types.addTranslation("}", 20);
        Types.addTranslation("[", 30);
        Types.addTranslation("]", 40);
        Types.addTranslation("(", 50);
        Types.addTranslation(")", 60);
        Types.addTranslation(".", 70);
        Types.addTranslation("..", 75);
        Types.addTranslation("...", 77);
        Types.addTranslation("->", 80);
        Types.addTranslation("=~", 90);
        Types.addTranslation("==~", 94);
        Types.addTranslation("~", 97);
        Types.addTranslation("=", 100);
        Types.addTranslation("!=", 120);
        Types.addTranslation("===", 121);
        Types.addTranslation("!==", 122);
        Types.addTranslation("==", 123);
        Types.addTranslation("<", 124);
        Types.addTranslation("<=", 125);
        Types.addTranslation(">", 126);
        Types.addTranslation(">=", 127);
        Types.addTranslation("<=>", 128);
        Types.addTranslation("!", 160);
        Types.addTranslation("||", 162);
        Types.addTranslation("&&", 164);
        Types.addTranslation("||=", 166);
        Types.addTranslation("&&=", 168);
        Types.addTranslation("+", 200);
        Types.addTranslation("-", 201);
        Types.addTranslation("*", 202);
        Types.addTranslation("/", 203);
        Types.addTranslation("\\", 204);
        Types.addTranslation("%", 205);
        Types.addTranslation("**", 206);
        Types.addTranslation("+=", 210);
        Types.addTranslation("-=", 211);
        Types.addTranslation("*=", 212);
        Types.addTranslation("/=", 213);
        Types.addTranslation("\\=", 214);
        Types.addTranslation("%=", 215);
        Types.addTranslation("**=", 216);
        Types.addTranslation("++", 250);
        Types.addTranslation("--", 260);
        Types.addTranslation("<<", 280);
        Types.addTranslation(">>", 281);
        Types.addTranslation(">>>", 282);
        Types.addTranslation("<<=", 285);
        Types.addTranslation(">>=", 286);
        Types.addTranslation(">>>=", 287);
        Types.addTranslation("&", 341);
        Types.addTranslation("^", 342);
        Types.addTranslation("|=", 350);
        Types.addTranslation("&=", 351);
        Types.addTranslation("^=", 352);
        Types.addTranslation(",", 300);
        Types.addTranslation(":", 310);
        Types.addTranslation(";", 320);
        Types.addTranslation("?", 330);
        Types.addTranslation("|", 340);
        Types.addTranslation("${}", 903);
        Types.addKeyword("abstract", 510);
        Types.addKeyword("as", 552);
        Types.addKeyword("assert", 585);
        Types.addKeyword("break", 574);
        Types.addKeyword("case", 577);
        Types.addKeyword("catch", 581);
        Types.addKeyword("class", 531);
        Types.addKeyword("const", 700);
        Types.addKeyword("continue", 575);
        Types.addKeyword("def", 530);
        Types.addKeyword("defmacro", 530);
        Types.addKeyword("default", 578);
        Types.addKeyword("do", 570);
        Types.addKeyword("else", 562);
        Types.addKeyword("extends", 541);
        Types.addKeyword("final", 511);
        Types.addKeyword("finally", 582);
        Types.addKeyword("for", 572);
        Types.addKeyword("goto", 701);
        Types.addKeyword("if", 561);
        Types.addKeyword("in", 573);
        Types.addKeyword("implements", 540);
        Types.addKeyword("import", 551);
        Types.addKeyword("instanceof", 544);
        Types.addKeyword("interface", 532);
        Types.addKeyword("mixin", 533);
        Types.addKeyword("native", 512);
        Types.addKeyword("new", 546);
        Types.addKeyword("package", 550);
        Types.addKeyword("private", 500);
        Types.addKeyword("property", 545);
        Types.addKeyword("protected", 501);
        Types.addKeyword("public", 502);
        Types.addKeyword("return", 560);
        Types.addKeyword("static", 521);
        Types.addKeyword("super", 543);
        Types.addKeyword("switch", 576);
        Types.addKeyword("synchronized", 520);
        Types.addKeyword("this", 542);
        Types.addKeyword("throw", 583);
        Types.addKeyword("throws", 584);
        Types.addKeyword("transient", 513);
        Types.addKeyword("try", 580);
        Types.addKeyword("volatile", 514);
        Types.addKeyword("while", 571);
        Types.addKeyword("true", 610);
        Types.addKeyword("false", 611);
        Types.addKeyword("null", 612);
        Types.addKeyword("void", 600);
        Types.addKeyword("boolean", 601);
        Types.addKeyword("byte", 602);
        Types.addKeyword("int", 604);
        Types.addKeyword("short", 603);
        Types.addKeyword("long", 605);
        Types.addKeyword("float", 606);
        Types.addKeyword("double", 607);
        Types.addKeyword("char", 608);
        DESCRIPTIONS = new HashMap<Integer, String>();
        for (Map.Entry<String, Integer> entry : LOOKUP.entrySet()) {
            Types.addDescription(entry.getValue(), entry.getKey());
        }
        Types.addDescription(5, "<newline>");
        Types.addDescription(251, "<prefix ++>");
        Types.addDescription(252, "<postfix ++>");
        Types.addDescription(261, "<prefix -->");
        Types.addDescription(262, "<postfix -->");
        Types.addDescription(253, "<positive>");
        Types.addDescription(263, "<negative>");
        Types.addDescription(400, "<string literal>");
        Types.addDescription(440, "<identifier>");
        Types.addDescription(450, "<integer>");
        Types.addDescription(451, "<decimal>");
        Types.addDescription(800, "<compilation unit>");
        Types.addDescription(801, "<class>");
        Types.addDescription(802, "<interface>");
        Types.addDescription(803, "<mixin>");
        Types.addDescription(804, "<method>");
        Types.addDescription(814, "<method call>");
        Types.addDescription(805, "<property>");
        Types.addDescription(806, "<parameter>");
        Types.addDescription(810, "<list>");
        Types.addDescription(811, "<map>");
        Types.addDescription(820, "<tuple>");
        Types.addDescription(812, "<gstring>");
        Types.addDescription(815, "<cast>");
        Types.addDescription(816, "<block>");
        Types.addDescription(817, "<closure>");
        Types.addDescription(819, "<ternary>");
        Types.addDescription(818, "<label>");
        Types.addDescription(830, "<variable declaration>");
        Types.addDescription(901, "<start of gstring tokens>");
        Types.addDescription(902, "<end of gstring tokens>");
        Types.addDescription(903, "<start of gstring expression>");
        Types.addDescription(904, "<end of gstring expression>");
        Types.addDescription(1100, "<assignment operator>");
        Types.addDescription(1101, "<comparison operator>");
        Types.addDescription(1102, "<math operator>");
        Types.addDescription(1103, "<logical operator>");
        Types.addDescription(1107, "<bitwise operator>");
        Types.addDescription(1104, "<range operator>");
        Types.addDescription(1105, "<regex comparison operator>");
        Types.addDescription(1106, "<dereference operator>");
        Types.addDescription(1200, "<prefix operator>");
        Types.addDescription(1210, "<postfix operator>");
        Types.addDescription(1220, "<infix operator>");
        Types.addDescription(1300, "<keyword>");
        Types.addDescription(1310, "<literal>");
        Types.addDescription(1320, "<number>");
        Types.addDescription(1330, "<named value>");
        Types.addDescription(1331, "<truth value>");
        Types.addDescription(1340, "<primitive type>");
        Types.addDescription(1341, "<creatable primitive type>");
        Types.addDescription(1350, "<loop>");
        Types.addDescription(1360, "<reserved keyword>");
        Types.addDescription(1370, "<synthetic>");
        Types.addDescription(1400, "<type declaration>");
        Types.addDescription(1410, "<declaration modifier>");
        Types.addDescription(1420, "<type name>");
        Types.addDescription(1430, "<creatable type name>");
        Types.addDescription(1500, "<matched container>");
        Types.addDescription(1501, "<left of matched container>");
        Types.addDescription(1502, "<right of matched container>");
        Types.addDescription(2005, "<valid in a switch body>");
    }
}

