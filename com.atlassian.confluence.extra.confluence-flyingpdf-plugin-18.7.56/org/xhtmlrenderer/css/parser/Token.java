/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

public class Token {
    public static final int S = 1;
    public static final int CDO = 2;
    public static final int CDC = 3;
    public static final int INCLUDES = 4;
    public static final int DASHMATCH = 5;
    public static final int PREFIXMATCH = 6;
    public static final int SUFFIXMATCH = 7;
    public static final int SUBSTRINGMATCH = 8;
    public static final int LBRACE = 9;
    public static final int PLUS = 10;
    public static final int GREATER = 11;
    public static final int COMMA = 12;
    public static final int STRING = 13;
    public static final int INVALID = 14;
    public static final int IDENT = 15;
    public static final int HASH = 16;
    public static final int IMPORT_SYM = 17;
    public static final int PAGE_SYM = 18;
    public static final int MEDIA_SYM = 19;
    public static final int CHARSET_SYM = 20;
    public static final int NAMESPACE_SYM = 21;
    public static final int FONT_FACE_SYM = 22;
    public static final int AT_RULE = 23;
    public static final int IMPORTANT_SYM = 24;
    public static final int EMS = 25;
    public static final int EXS = 26;
    public static final int PX = 27;
    public static final int CM = 28;
    public static final int MM = 29;
    public static final int IN = 30;
    public static final int PT = 31;
    public static final int PC = 32;
    public static final int ANGLE = 33;
    public static final int TIME = 34;
    public static final int FREQ = 35;
    public static final int DIMENSION = 36;
    public static final int PERCENTAGE = 37;
    public static final int NUMBER = 38;
    public static final int URI = 39;
    public static final int FUNCTION = 40;
    public static final int OTHER = 41;
    public static final int RBRACE = 42;
    public static final int SEMICOLON = 43;
    public static final int VIRGULE = 44;
    public static final int COLON = 45;
    public static final int MINUS = 46;
    public static final int RPAREN = 47;
    public static final int LBRACKET = 48;
    public static final int RBRACKET = 49;
    public static final int PERIOD = 50;
    public static final int EQUALS = 51;
    public static final int ASTERISK = 52;
    public static final int VERTICAL_BAR = 53;
    public static final int EOF = 54;
    public static final Token TK_S = new Token(1, "S", "whitespace");
    public static final Token TK_CDO = new Token(2, "CDO", "<!--");
    public static final Token TK_CDC = new Token(3, "CDC", "-->");
    public static final Token TK_INCLUDES = new Token(4, "INCLUDES", "an attribute word match");
    public static final Token TK_DASHMATCH = new Token(5, "DASHMATCH", "an attribute hyphen match");
    public static final Token TK_PREFIXMATCH = new Token(6, "PREFIXMATCH", "an attribute prefix match");
    public static final Token TK_SUFFIXMATCH = new Token(7, "SUFFIXMATCH", "an attribute suffix match");
    public static final Token TK_SUBSTRINGMATCH = new Token(8, "SUBSTRINGMATCH", "an attribute substring match");
    public static final Token TK_LBRACE = new Token(9, "LBRACE", "a {");
    public static final Token TK_PLUS = new Token(10, "PLUS", "a +");
    public static final Token TK_GREATER = new Token(11, "GREATER", "a >");
    public static final Token TK_COMMA = new Token(12, "COMMA", "a comma");
    public static final Token TK_STRING = new Token(13, "STRING", "a string");
    public static final Token TK_INVALID = new Token(14, "INVALID", "an unclosed string");
    public static final Token TK_IDENT = new Token(15, "IDENT", "an identifier");
    public static final Token TK_HASH = new Token(16, "HASH", "a hex color");
    public static final Token TK_IMPORT_SYM = new Token(17, "IMPORT_SYM", "@import");
    public static final Token TK_PAGE_SYM = new Token(18, "PAGE_SYM", "@page");
    public static final Token TK_MEDIA_SYM = new Token(19, "MEDIA_SYM", "@media");
    public static final Token TK_CHARSET_SYM = new Token(20, "CHARSET_SYM", "@charset");
    public static final Token TK_NAMESPACE_SYM = new Token(21, "NAMESPACE_SYM", "@namespace,");
    public static final Token TK_FONT_FACE_SYM = new Token(22, "FONT_FACE_SYM", "@font-face");
    public static final Token TK_AT_RULE = new Token(23, "AT_RULE", "at rule");
    public static final Token TK_IMPORTANT_SYM = new Token(24, "IMPORTANT_SYM", "!important");
    public static final Token TK_EMS = new Token(25, "EMS", "an em value");
    public static final Token TK_EXS = new Token(26, "EXS", "an ex value");
    public static final Token TK_PX = new Token(27, "PX", "a pixel value");
    public static final Token TK_CM = new Token(28, "CM", "a centimeter value");
    public static final Token TK_MM = new Token(29, "MM", "a millimeter value");
    public static final Token TK_IN = new Token(30, "IN", "an inch value");
    public static final Token TK_PT = new Token(31, "PT", "a point value");
    public static final Token TK_PC = new Token(32, "PC", "a pica value");
    public static final Token TK_ANGLE = new Token(33, "ANGLE", "an angle value");
    public static final Token TK_TIME = new Token(34, "TIME", "a time value");
    public static final Token TK_FREQ = new Token(35, "FREQ", "a freq value");
    public static final Token TK_DIMENSION = new Token(36, "DIMENSION", "a dimension");
    public static final Token TK_PERCENTAGE = new Token(37, "PERCENTAGE", "a percentage");
    public static final Token TK_NUMBER = new Token(38, "NUMBER", "a number");
    public static final Token TK_URI = new Token(39, "URI", "a URI");
    public static final Token TK_FUNCTION = new Token(40, "FUNCTION", "function");
    public static final Token TK_OTHER = new Token(41, "OTHER", "other");
    public static final Token TK_RBRACE = new Token(42, "RBRACE", "}");
    public static final Token TK_SEMICOLON = new Token(43, "SEMICOLON", ";");
    public static final Token TK_VIRGULE = new Token(44, "VIRGULE", "/");
    public static final Token TK_COLON = new Token(45, "COLON", ":");
    public static final Token TK_MINUS = new Token(46, "MINUS", "-");
    public static final Token TK_RPAREN = new Token(47, "RPAREN", ")");
    public static final Token TK_LBRACKET = new Token(48, "LBRACKET", "[");
    public static final Token TK_RBRACKET = new Token(49, "RBRACKET", "]");
    public static final Token TK_PERIOD = new Token(50, "PERIOD", ".");
    public static final Token TK_EQUALS = new Token(51, "EQUALS", "=");
    public static final Token TK_ASTERISK = new Token(52, "ASTERISK", "*");
    public static final Token TK_VERTICAL_BAR = new Token(53, "VERTICAL_BAR", "|");
    public static final Token TK_EOF = new Token(54, "EOF", "end of file");
    private final int _type;
    private final String _name;
    private final String _externalName;

    private Token(int type, String name, String externalName) {
        this._type = type;
        this._name = name;
        this._externalName = externalName;
    }

    public int getType() {
        return this._type;
    }

    public String getName() {
        return this._name;
    }

    public String getExternalName() {
        return this._externalName;
    }

    public String toString() {
        return this._name;
    }

    public static Token createOtherToken(String value) {
        return new Token(41, "OTHER", value + " (other)");
    }
}

