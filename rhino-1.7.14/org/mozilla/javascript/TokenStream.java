/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Token;

class TokenStream {
    private static final int EOF_CHAR = -1;
    private static final int REPORT_NUMBER_FORMAT_ERROR = -2;
    private static final char BYTE_ORDER_MARK = '\ufeff';
    private static final char NUMERIC_SEPARATOR = '_';
    private StringBuilder rawString = new StringBuilder();
    private boolean dirtyLine;
    String regExpFlags;
    private String string = "";
    private double number;
    private BigInteger bigInt;
    private boolean isBinary;
    private boolean isOldOctal;
    private boolean isOctal;
    private boolean isHex;
    private int quoteChar;
    private char[] stringBuffer = new char[128];
    private int stringBufferTop;
    private ObjToIntMap allStrings = new ObjToIntMap(50);
    private final int[] ungetBuffer = new int[3];
    private int ungetCursor;
    private boolean hitEOF = false;
    private int lineStart = 0;
    private int lineEndChar = -1;
    int lineno;
    private String sourceString;
    private Reader sourceReader;
    private char[] sourceBuffer;
    private int sourceEnd;
    int sourceCursor;
    int cursor;
    int tokenBeg;
    int tokenEnd;
    Token.CommentType commentType;
    private boolean xmlIsAttribute;
    private boolean xmlIsTagContent;
    private int xmlOpenTagsCount;
    private Parser parser;
    private String commentPrefix = "";
    private int commentCursor = -1;

    TokenStream(Parser parser, Reader sourceReader, String sourceString, int lineno) {
        this.parser = parser;
        this.lineno = lineno;
        if (sourceReader != null) {
            if (sourceString != null) {
                Kit.codeBug();
            }
            this.sourceReader = sourceReader;
            this.sourceBuffer = new char[512];
            this.sourceEnd = 0;
        } else {
            if (sourceString == null) {
                Kit.codeBug();
            }
            this.sourceString = sourceString;
            this.sourceEnd = sourceString.length();
        }
        this.cursor = 0;
        this.sourceCursor = 0;
    }

    String tokenToString(int token) {
        return "";
    }

    static boolean isKeyword(String s, int version, boolean isStrict) {
        return 0 != TokenStream.stringToKeyword(s, version, isStrict);
    }

    private static int stringToKeyword(String name, int version, boolean isStrict) {
        if (version < 200) {
            return TokenStream.stringToKeywordForJS(name);
        }
        return TokenStream.stringToKeywordForES(name, isStrict);
    }

    private static int stringToKeywordForJS(String name) {
        int id;
        String s;
        int Id_break = 124;
        int Id_case = 119;
        int Id_continue = 125;
        int Id_default = 120;
        int Id_delete = 31;
        int Id_do = 122;
        int Id_else = 117;
        int Id_export = 131;
        int Id_false = 44;
        int Id_for = 123;
        int Id_function = 113;
        int Id_if = 116;
        int Id_in = 52;
        int Id_let = 157;
        int Id_new = 30;
        int Id_null = 42;
        int Id_return = 4;
        int Id_switch = 118;
        int Id_this = 43;
        int Id_true = 45;
        int Id_typeof = 32;
        int Id_var = 126;
        int Id_void = 130;
        int Id_while = 121;
        int Id_with = 127;
        int Id_yield = 73;
        int Id_abstract = 131;
        int Id_boolean = 131;
        int Id_byte = 131;
        int Id_catch = 128;
        int Id_char = 131;
        int Id_class = 131;
        int Id_const = 158;
        int Id_debugger = 164;
        int Id_double = 131;
        int Id_enum = 131;
        int Id_extends = 131;
        int Id_final = 131;
        int Id_finally = 129;
        int Id_float = 131;
        int Id_goto = 131;
        int Id_implements = 131;
        int Id_import = 131;
        int Id_instanceof = 53;
        int Id_int = 131;
        int Id_interface = 131;
        int Id_long = 131;
        int Id_native = 131;
        int Id_package = 131;
        int Id_private = 131;
        int Id_protected = 131;
        int Id_public = 131;
        int Id_short = 131;
        int Id_static = 131;
        int Id_super = 131;
        int Id_synchronized = 131;
        int Id_throw = 50;
        int Id_throws = 131;
        int Id_transient = 131;
        int Id_try = 84;
        int Id_volatile = 131;
        switch (s = name) {
            case "break": {
                id = 124;
                break;
            }
            case "case": {
                id = 119;
                break;
            }
            case "continue": {
                id = 125;
                break;
            }
            case "default": {
                id = 120;
                break;
            }
            case "delete": {
                id = 31;
                break;
            }
            case "do": {
                id = 122;
                break;
            }
            case "else": {
                id = 117;
                break;
            }
            case "export": {
                id = 131;
                break;
            }
            case "false": {
                id = 44;
                break;
            }
            case "for": {
                id = 123;
                break;
            }
            case "function": {
                id = 113;
                break;
            }
            case "if": {
                id = 116;
                break;
            }
            case "in": {
                id = 52;
                break;
            }
            case "let": {
                id = 157;
                break;
            }
            case "new": {
                id = 30;
                break;
            }
            case "null": {
                id = 42;
                break;
            }
            case "return": {
                id = 4;
                break;
            }
            case "switch": {
                id = 118;
                break;
            }
            case "this": {
                id = 43;
                break;
            }
            case "true": {
                id = 45;
                break;
            }
            case "typeof": {
                id = 32;
                break;
            }
            case "var": {
                id = 126;
                break;
            }
            case "void": {
                id = 130;
                break;
            }
            case "while": {
                id = 121;
                break;
            }
            case "with": {
                id = 127;
                break;
            }
            case "yield": {
                id = 73;
                break;
            }
            case "abstract": {
                id = 131;
                break;
            }
            case "boolean": {
                id = 131;
                break;
            }
            case "byte": {
                id = 131;
                break;
            }
            case "catch": {
                id = 128;
                break;
            }
            case "char": {
                id = 131;
                break;
            }
            case "class": {
                id = 131;
                break;
            }
            case "const": {
                id = 158;
                break;
            }
            case "debugger": {
                id = 164;
                break;
            }
            case "double": {
                id = 131;
                break;
            }
            case "enum": {
                id = 131;
                break;
            }
            case "extends": {
                id = 131;
                break;
            }
            case "final": {
                id = 131;
                break;
            }
            case "finally": {
                id = 129;
                break;
            }
            case "float": {
                id = 131;
                break;
            }
            case "goto": {
                id = 131;
                break;
            }
            case "implements": {
                id = 131;
                break;
            }
            case "import": {
                id = 131;
                break;
            }
            case "instanceof": {
                id = 53;
                break;
            }
            case "int": {
                id = 131;
                break;
            }
            case "interface": {
                id = 131;
                break;
            }
            case "long": {
                id = 131;
                break;
            }
            case "native": {
                id = 131;
                break;
            }
            case "package": {
                id = 131;
                break;
            }
            case "private": {
                id = 131;
                break;
            }
            case "protected": {
                id = 131;
                break;
            }
            case "public": {
                id = 131;
                break;
            }
            case "short": {
                id = 131;
                break;
            }
            case "static": {
                id = 131;
                break;
            }
            case "super": {
                id = 131;
                break;
            }
            case "synchronized": {
                id = 131;
                break;
            }
            case "throw": {
                id = 50;
                break;
            }
            case "throws": {
                id = 131;
                break;
            }
            case "transient": {
                id = 131;
                break;
            }
            case "try": {
                id = 84;
                break;
            }
            case "volatile": {
                id = 131;
                break;
            }
            default: {
                id = 0;
            }
        }
        if (id == 0) {
            return 0;
        }
        return id & 0xFF;
    }

    private static int stringToKeywordForES(String name, boolean isStrict) {
        String s;
        int Id_break = 124;
        int Id_case = 119;
        int Id_catch = 128;
        int Id_class = 131;
        int Id_const = 158;
        int Id_continue = 125;
        int Id_debugger = 164;
        int Id_default = 120;
        int Id_delete = 31;
        int Id_do = 122;
        int Id_else = 117;
        int Id_export = 131;
        int Id_extends = 131;
        int Id_finally = 129;
        int Id_for = 123;
        int Id_function = 113;
        int Id_if = 116;
        int Id_import = 131;
        int Id_in = 52;
        int Id_instanceof = 53;
        int Id_new = 30;
        int Id_return = 4;
        int Id_super = 131;
        int Id_switch = 118;
        int Id_this = 43;
        int Id_throw = 50;
        int Id_try = 84;
        int Id_typeof = 32;
        int Id_var = 126;
        int Id_void = 130;
        int Id_while = 121;
        int Id_with = 127;
        int Id_yield = 73;
        int Id_await = 131;
        int Id_enum = 131;
        int Id_implements = 131;
        int Id_interface = 131;
        int Id_package = 131;
        int Id_private = 131;
        int Id_protected = 131;
        int Id_public = 131;
        int Id_false = 44;
        int Id_null = 42;
        int Id_true = 45;
        int Id_let = 157;
        int Id_static = 131;
        int id = 0;
        switch (s = name) {
            case "break": {
                id = 124;
                break;
            }
            case "case": {
                id = 119;
                break;
            }
            case "catch": {
                id = 128;
                break;
            }
            case "class": {
                id = 131;
                break;
            }
            case "const": {
                id = 158;
                break;
            }
            case "continue": {
                id = 125;
                break;
            }
            case "debugger": {
                id = 164;
                break;
            }
            case "default": {
                id = 120;
                break;
            }
            case "delete": {
                id = 31;
                break;
            }
            case "do": {
                id = 122;
                break;
            }
            case "else": {
                id = 117;
                break;
            }
            case "export": {
                id = 131;
                break;
            }
            case "extends": {
                id = 131;
                break;
            }
            case "finally": {
                id = 129;
                break;
            }
            case "for": {
                id = 123;
                break;
            }
            case "function": {
                id = 113;
                break;
            }
            case "if": {
                id = 116;
                break;
            }
            case "import": {
                id = 131;
                break;
            }
            case "in": {
                id = 52;
                break;
            }
            case "instanceof": {
                id = 53;
                break;
            }
            case "new": {
                id = 30;
                break;
            }
            case "return": {
                id = 4;
                break;
            }
            case "super": {
                id = 131;
                break;
            }
            case "switch": {
                id = 118;
                break;
            }
            case "this": {
                id = 43;
                break;
            }
            case "throw": {
                id = 50;
                break;
            }
            case "try": {
                id = 84;
                break;
            }
            case "typeof": {
                id = 32;
                break;
            }
            case "var": {
                id = 126;
                break;
            }
            case "void": {
                id = 130;
                break;
            }
            case "while": {
                id = 121;
                break;
            }
            case "with": {
                id = 127;
                break;
            }
            case "yield": {
                id = 73;
                break;
            }
            case "await": {
                id = 131;
                break;
            }
            case "enum": {
                id = 131;
                break;
            }
            case "implements": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            case "interface": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            case "package": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            case "private": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            case "protected": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            case "public": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            case "false": {
                id = 44;
                break;
            }
            case "null": {
                id = 42;
                break;
            }
            case "true": {
                id = 45;
                break;
            }
            case "let": {
                id = 157;
                break;
            }
            case "static": {
                if (!isStrict) break;
                id = 131;
                break;
            }
            default: {
                id = 0;
            }
        }
        if (id == 0) {
            return 0;
        }
        return id & 0xFF;
    }

    final String getSourceString() {
        return this.sourceString;
    }

    final int getLineno() {
        return this.lineno;
    }

    final String getString() {
        return this.string;
    }

    final char getQuoteChar() {
        return (char)this.quoteChar;
    }

    final double getNumber() {
        return this.number;
    }

    final BigInteger getBigInt() {
        return this.bigInt;
    }

    final boolean isNumericBinary() {
        return this.isBinary;
    }

    final boolean isNumericOldOctal() {
        return this.isOldOctal;
    }

    final boolean isNumericOctal() {
        return this.isOctal;
    }

    final boolean isNumericHex() {
        return this.isHex;
    }

    final boolean eof() {
        return this.hitEOF;
    }

    final int getToken() throws IOException {
        boolean identifierStart;
        int c;
        do {
            if ((c = this.getChar()) == -1) {
                this.tokenBeg = this.cursor - 1;
                this.tokenEnd = this.cursor;
                return 0;
            }
            if (c != 10) continue;
            this.dirtyLine = false;
            this.tokenBeg = this.cursor - 1;
            this.tokenEnd = this.cursor;
            return 1;
        } while (TokenStream.isJSSpace(c));
        if (c != 45) {
            this.dirtyLine = true;
        }
        this.tokenBeg = this.cursor - 1;
        this.tokenEnd = this.cursor;
        if (c == 64) {
            return 151;
        }
        boolean isUnicodeEscapeStart = false;
        if (c == 92) {
            c = this.getChar();
            if (c == 117) {
                identifierStart = true;
                isUnicodeEscapeStart = true;
                this.stringBufferTop = 0;
            } else {
                identifierStart = false;
                this.ungetChar(c);
                c = 92;
            }
        } else {
            identifierStart = Character.isJavaIdentifierStart((char)c);
            if (identifierStart) {
                this.stringBufferTop = 0;
                this.addToString(c);
            }
        }
        if (identifierStart) {
            boolean containsEscape = isUnicodeEscapeStart;
            while (true) {
                if (isUnicodeEscapeStart) {
                    int escapeVal = 0;
                    for (int i = 0; i != 4 && (escapeVal = Kit.xDigitToInt(c = this.getChar(), escapeVal)) >= 0; ++i) {
                    }
                    if (escapeVal < 0) {
                        this.parser.addError("msg.invalid.escape");
                        return -1;
                    }
                    this.addToString(escapeVal);
                    isUnicodeEscapeStart = false;
                    continue;
                }
                c = this.getChar();
                if (c == 92) {
                    c = this.getChar();
                    if (c == 117) {
                        isUnicodeEscapeStart = true;
                        containsEscape = true;
                        continue;
                    }
                    this.parser.addError("msg.illegal.character", c);
                    return -1;
                }
                if (c == -1 || c == 65279 || !Character.isJavaIdentifierPart((char)c)) break;
                this.addToString(c);
            }
            this.ungetChar(c);
            String str = this.getStringFromBuffer();
            if (!containsEscape) {
                int result = TokenStream.stringToKeyword(str, this.parser.compilerEnv.getLanguageVersion(), this.parser.inUseStrictDirective());
                if (result != 0) {
                    if ((result == 157 || result == 73) && this.parser.compilerEnv.getLanguageVersion() < 170) {
                        this.string = result == 157 ? "let" : "yield";
                        result = 39;
                    }
                    this.string = (String)this.allStrings.intern(str);
                    if (result != 131) {
                        return result;
                    }
                    if (this.parser.compilerEnv.getLanguageVersion() >= 200) {
                        return result;
                    }
                    if (!this.parser.compilerEnv.isReservedKeywordAsIdentifier()) {
                        return result;
                    }
                }
            } else if (TokenStream.isKeyword(str, this.parser.compilerEnv.getLanguageVersion(), this.parser.inUseStrictDirective())) {
                str = TokenStream.convertLastCharToHex(str);
            }
            this.string = (String)this.allStrings.intern(str);
            return 39;
        }
        if (TokenStream.isDigit(c) || c == 46 && TokenStream.isDigit(this.peekChar())) {
            double dval;
            String numString;
            boolean es6;
            this.stringBufferTop = 0;
            int base = 10;
            this.isBinary = false;
            this.isOctal = false;
            this.isOldOctal = false;
            this.isHex = false;
            boolean bl = es6 = this.parser.compilerEnv.getLanguageVersion() >= 200;
            if (c == 48) {
                c = this.getChar();
                if (c == 120 || c == 88) {
                    base = 16;
                    this.isHex = true;
                    c = this.getChar();
                } else if (es6 && (c == 111 || c == 79)) {
                    base = 8;
                    this.isOctal = true;
                    c = this.getChar();
                } else if (es6 && (c == 98 || c == 66)) {
                    base = 2;
                    this.isBinary = true;
                    c = this.getChar();
                } else if (TokenStream.isDigit(c)) {
                    base = 8;
                    this.isOldOctal = true;
                } else {
                    this.addToString(48);
                }
            }
            int emptyDetector = this.stringBufferTop;
            if (base == 10 || base == 16 || base == 8 && !this.isOldOctal || base == 2) {
                if ((c = this.readDigits(base, c)) == -2) {
                    this.parser.addError("msg.caught.nfe");
                    return -1;
                }
            } else {
                while (TokenStream.isDigit(c)) {
                    if (c >= 56) {
                        this.parser.addWarning("msg.bad.octal.literal", c == 56 ? "8" : "9");
                        base = 10;
                        c = this.readDigits(base, c);
                        if (c != -2) break;
                        this.parser.addError("msg.caught.nfe");
                        return -1;
                    }
                    this.addToString(c);
                    c = this.getChar();
                }
            }
            if (this.stringBufferTop == emptyDetector && (this.isBinary || this.isOctal || this.isHex)) {
                this.parser.addError("msg.caught.nfe");
                return -1;
            }
            boolean isInteger = true;
            boolean isBigInt = false;
            if (es6 && c == 110) {
                isBigInt = true;
                c = this.getChar();
            } else if (base == 10 && (c == 46 || c == 101 || c == 69)) {
                isInteger = false;
                if (c == 46) {
                    isInteger = false;
                    this.addToString(c);
                    c = this.getChar();
                    c = this.readDigits(base, c);
                    if (c == -2) {
                        this.parser.addError("msg.caught.nfe");
                        return -1;
                    }
                }
                if (c == 101 || c == 69) {
                    isInteger = false;
                    this.addToString(c);
                    c = this.getChar();
                    if (c == 43 || c == 45) {
                        this.addToString(c);
                        c = this.getChar();
                    }
                    if (!TokenStream.isDigit(c)) {
                        this.parser.addError("msg.missing.exponent");
                        return -1;
                    }
                    if ((c = this.readDigits(base, c)) == -2) {
                        this.parser.addError("msg.caught.nfe");
                        return -1;
                    }
                }
            }
            this.ungetChar(c);
            this.string = numString = this.getStringFromBuffer();
            int pos = numString.indexOf(95);
            if (pos != -1) {
                char[] chars = numString.toCharArray();
                for (int i = pos + 1; i < chars.length; ++i) {
                    if (chars[i] == '_') continue;
                    chars[pos++] = chars[i];
                }
                numString = new String(chars, 0, pos);
            }
            if (isBigInt) {
                this.bigInt = new BigInteger(numString, base);
                return 83;
            }
            if (base == 10 && !isInteger) {
                try {
                    dval = Double.parseDouble(numString);
                }
                catch (NumberFormatException ex) {
                    this.parser.addError("msg.caught.nfe");
                    return -1;
                }
            } else {
                dval = ScriptRuntime.stringPrefixToNumber(numString, 0, base);
            }
            this.number = dval;
            return 40;
        }
        if (c == 34 || c == 39) {
            this.quoteChar = c;
            this.stringBufferTop = 0;
            c = this.getCharIgnoreLineEnd(false);
            block49: while (c != this.quoteChar) {
                boolean unterminated = false;
                if (c == -1) {
                    unterminated = true;
                } else if (c == 10) {
                    switch (this.lineEndChar) {
                        case 10: 
                        case 13: {
                            unterminated = true;
                            break;
                        }
                        case 8232: 
                        case 8233: {
                            c = this.lineEndChar;
                            break;
                        }
                    }
                }
                if (unterminated) {
                    this.ungetCharIgnoreLineEnd(c);
                    this.tokenEnd = this.cursor;
                    this.parser.addError("msg.unterminated.string.lit");
                    return -1;
                }
                if (c == 92) {
                    c = this.getChar();
                    switch (c) {
                        case 98: {
                            c = 8;
                            break;
                        }
                        case 102: {
                            c = 12;
                            break;
                        }
                        case 110: {
                            c = 10;
                            break;
                        }
                        case 114: {
                            c = 13;
                            break;
                        }
                        case 116: {
                            c = 9;
                            break;
                        }
                        case 118: {
                            c = 11;
                            break;
                        }
                        case 117: {
                            int escapeStart = this.stringBufferTop;
                            this.addToString(117);
                            int escapeVal = 0;
                            for (int i = 0; i != 4; ++i) {
                                c = this.getChar();
                                escapeVal = Kit.xDigitToInt(c, escapeVal);
                                if (escapeVal < 0) continue block49;
                                this.addToString(c);
                            }
                            this.stringBufferTop = escapeStart;
                            c = escapeVal;
                            break;
                        }
                        case 120: {
                            c = this.getChar();
                            int escapeVal = Kit.xDigitToInt(c, 0);
                            if (escapeVal < 0) {
                                this.addToString(120);
                                continue block49;
                            }
                            int c1 = c;
                            c = this.getChar();
                            escapeVal = Kit.xDigitToInt(c, escapeVal);
                            if (escapeVal < 0) {
                                this.addToString(120);
                                this.addToString(c1);
                                continue block49;
                            }
                            c = escapeVal;
                            break;
                        }
                        case 10: {
                            c = this.getChar();
                            continue block49;
                        }
                        default: {
                            if (48 > c || c >= 56) break;
                            int val = c - 48;
                            c = this.getChar();
                            if (48 <= c && c < 56) {
                                val = 8 * val + c - 48;
                                c = this.getChar();
                                if (48 <= c && c < 56 && val <= 31) {
                                    val = 8 * val + c - 48;
                                    c = this.getChar();
                                }
                            }
                            this.ungetChar(c);
                            c = val;
                        }
                    }
                }
                this.addToString(c);
                c = this.getChar(false);
            }
            String str = this.getStringFromBuffer();
            this.string = (String)this.allStrings.intern(str);
            return 41;
        }
        switch (c) {
            case 59: {
                return 85;
            }
            case 91: {
                return 86;
            }
            case 93: {
                return 87;
            }
            case 123: {
                return 88;
            }
            case 125: {
                return 89;
            }
            case 40: {
                return 90;
            }
            case 41: {
                return 91;
            }
            case 44: {
                return 92;
            }
            case 63: {
                return 106;
            }
            case 58: {
                if (this.matchChar(58)) {
                    return 148;
                }
                return 107;
            }
            case 46: {
                if (this.matchChar(46)) {
                    return 147;
                }
                if (this.matchChar(40)) {
                    return 150;
                }
                return 112;
            }
            case 124: {
                if (this.matchChar(124)) {
                    return 108;
                }
                if (this.matchChar(61)) {
                    return 94;
                }
                return 9;
            }
            case 94: {
                if (this.matchChar(61)) {
                    return 95;
                }
                return 10;
            }
            case 38: {
                if (this.matchChar(38)) {
                    return 109;
                }
                if (this.matchChar(61)) {
                    return 96;
                }
                return 11;
            }
            case 61: {
                if (this.matchChar(61)) {
                    if (this.matchChar(61)) {
                        return 46;
                    }
                    return 12;
                }
                if (this.matchChar(62)) {
                    return 168;
                }
                return 93;
            }
            case 33: {
                if (this.matchChar(61)) {
                    if (this.matchChar(61)) {
                        return 47;
                    }
                    return 13;
                }
                return 26;
            }
            case 60: {
                if (this.matchChar(33)) {
                    if (this.matchChar(45)) {
                        if (this.matchChar(45)) {
                            this.tokenBeg = this.cursor - 4;
                            this.skipLine();
                            this.commentType = Token.CommentType.HTML;
                            return 165;
                        }
                        this.ungetCharIgnoreLineEnd(45);
                    }
                    this.ungetCharIgnoreLineEnd(33);
                }
                if (this.matchChar(60)) {
                    if (this.matchChar(61)) {
                        return 97;
                    }
                    return 18;
                }
                if (this.matchChar(61)) {
                    return 15;
                }
                return 14;
            }
            case 62: {
                if (this.matchChar(62)) {
                    if (this.matchChar(62)) {
                        if (this.matchChar(61)) {
                            return 99;
                        }
                        return 20;
                    }
                    if (this.matchChar(61)) {
                        return 98;
                    }
                    return 19;
                }
                if (this.matchChar(61)) {
                    return 17;
                }
                return 16;
            }
            case 42: {
                if (this.parser.compilerEnv.getLanguageVersion() >= 200 && this.matchChar(42)) {
                    if (this.matchChar(61)) {
                        return 105;
                    }
                    return 75;
                }
                if (this.matchChar(61)) {
                    return 102;
                }
                return 23;
            }
            case 47: {
                this.markCommentStart();
                if (this.matchChar(47)) {
                    this.tokenBeg = this.cursor - 2;
                    this.skipLine();
                    this.commentType = Token.CommentType.LINE;
                    return 165;
                }
                if (this.matchChar(42)) {
                    boolean lookForSlash = false;
                    this.tokenBeg = this.cursor - 2;
                    if (this.matchChar(42)) {
                        lookForSlash = true;
                        this.commentType = Token.CommentType.JSDOC;
                    } else {
                        this.commentType = Token.CommentType.BLOCK_COMMENT;
                    }
                    while (true) {
                        if ((c = this.getChar()) == -1) {
                            this.tokenEnd = this.cursor - 1;
                            this.parser.addError("msg.unterminated.comment");
                            return 165;
                        }
                        if (c == 42) {
                            lookForSlash = true;
                            continue;
                        }
                        if (c == 47) {
                            if (!lookForSlash) continue;
                            this.tokenEnd = this.cursor;
                            return 165;
                        }
                        lookForSlash = false;
                        this.tokenEnd = this.cursor;
                    }
                }
                if (this.matchChar(61)) {
                    return 103;
                }
                return 24;
            }
            case 37: {
                if (this.matchChar(61)) {
                    return 104;
                }
                return 25;
            }
            case 126: {
                return 27;
            }
            case 43: {
                if (this.matchChar(61)) {
                    return 100;
                }
                if (this.matchChar(43)) {
                    return 110;
                }
                return 21;
            }
            case 45: {
                if (this.matchChar(61)) {
                    c = 101;
                } else if (this.matchChar(45)) {
                    if (!this.dirtyLine && this.matchChar(62)) {
                        this.markCommentStart("--");
                        this.skipLine();
                        this.commentType = Token.CommentType.HTML;
                        return 165;
                    }
                    c = 111;
                } else {
                    c = 22;
                }
                this.dirtyLine = true;
                return c;
            }
            case 96: {
                return 170;
            }
        }
        this.parser.addError("msg.illegal.character", c);
        return -1;
    }

    private int readDigits(int base, int c) throws IOException {
        if (TokenStream.isDigit(base, c)) {
            block6: {
                this.addToString(c);
                c = this.getChar();
                if (c == -1) {
                    return -1;
                }
                while (true) {
                    if (c == 95) {
                        c = this.getChar();
                        if (c == 10 || c == -1) {
                            return -2;
                        }
                        if (!TokenStream.isDigit(base, c)) {
                            this.ungetChar(c);
                            return 95;
                        }
                        this.addToString(95);
                        continue;
                    }
                    if (!TokenStream.isDigit(base, c)) break block6;
                    this.addToString(c);
                    c = this.getChar();
                    if (c == -1) break;
                }
                return -1;
            }
            return c;
        }
        return c;
    }

    private static boolean isAlpha(int c) {
        if (c <= 90) {
            return 65 <= c;
        }
        return 97 <= c && c <= 122;
    }

    private static boolean isDigit(int base, int c) {
        return base == 10 && TokenStream.isDigit(c) || base == 16 && TokenStream.isHexDigit(c) || base == 8 && TokenStream.isOctalDigit(c) || base == 2 && TokenStream.isDualDigit(c);
    }

    private static boolean isDualDigit(int c) {
        return 48 == c || c == 49;
    }

    private static boolean isOctalDigit(int c) {
        return 48 <= c && c <= 55;
    }

    private static boolean isDigit(int c) {
        return 48 <= c && c <= 57;
    }

    private static boolean isHexDigit(int c) {
        return 48 <= c && c <= 57 || 97 <= c && c <= 102 || 65 <= c && c <= 70;
    }

    private static boolean isJSSpace(int c) {
        if (c <= 127) {
            return c == 32 || c == 9 || c == 12 || c == 11;
        }
        return c == 160 || c == 65279 || Character.getType((char)c) == 12;
    }

    private static boolean isJSFormatChar(int c) {
        return c > 127 && Character.getType((char)c) == 16;
    }

    void readRegExp(int startToken) throws IOException {
        int c;
        int start = this.tokenBeg;
        this.stringBufferTop = 0;
        if (startToken == 103) {
            this.addToString(61);
        } else {
            if (startToken != 24) {
                Kit.codeBug();
            }
            if (this.peekChar() == 42) {
                this.tokenEnd = this.cursor - 1;
                this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
                this.parser.reportError("msg.unterminated.re.lit");
                return;
            }
        }
        boolean inCharSet = false;
        while ((c = this.getChar()) != 47 || inCharSet) {
            if (c == 10 || c == -1) {
                this.ungetChar(c);
                this.tokenEnd = this.cursor - 1;
                this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
                this.parser.reportError("msg.unterminated.re.lit");
                return;
            }
            if (c == 92) {
                this.addToString(c);
                c = this.getChar();
                if (c == 10 || c == -1) {
                    this.ungetChar(c);
                    this.tokenEnd = this.cursor - 1;
                    this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
                    this.parser.reportError("msg.unterminated.re.lit");
                    return;
                }
            } else if (c == 91) {
                inCharSet = true;
            } else if (c == 93) {
                inCharSet = false;
            }
            this.addToString(c);
        }
        int reEnd = this.stringBufferTop;
        while (true) {
            if (this.matchChar(103)) {
                this.addToString(103);
                continue;
            }
            if (this.matchChar(105)) {
                this.addToString(105);
                continue;
            }
            if (this.matchChar(109)) {
                this.addToString(109);
                continue;
            }
            if (!this.matchChar(121)) break;
            this.addToString(121);
        }
        this.tokenEnd = start + this.stringBufferTop + 2;
        if (TokenStream.isAlpha(this.peekChar())) {
            this.parser.reportError("msg.invalid.re.flag");
        }
        this.string = new String(this.stringBuffer, 0, reEnd);
        this.regExpFlags = new String(this.stringBuffer, reEnd, this.stringBufferTop - reEnd);
    }

    String readAndClearRegExpFlags() {
        String flags = this.regExpFlags;
        this.regExpFlags = null;
        return flags;
    }

    String getRawString() {
        if (this.rawString.length() == 0) {
            return "";
        }
        return this.rawString.toString();
    }

    private int getTemplateLiteralChar() throws IOException {
        int c = this.getCharIgnoreLineEnd(false);
        if (c == 10) {
            switch (this.lineEndChar) {
                case 13: {
                    if (this.charAt(this.cursor) != 10) break;
                    this.getCharIgnoreLineEnd(false);
                    break;
                }
                case 8232: 
                case 8233: {
                    c = this.lineEndChar;
                    break;
                }
            }
            this.lineEndChar = -1;
            this.lineStart = this.sourceCursor - 1;
            ++this.lineno;
        }
        this.rawString.append((char)c);
        return c;
    }

    private void ungetTemplateLiteralChar(int c) {
        this.ungetCharIgnoreLineEnd(c);
        this.rawString.setLength(this.rawString.length() - 1);
    }

    private boolean matchTemplateLiteralChar(int test) throws IOException {
        int c = this.getTemplateLiteralChar();
        if (c == test) {
            return true;
        }
        this.ungetTemplateLiteralChar(c);
        return false;
    }

    private int peekTemplateLiteralChar() throws IOException {
        int c = this.getTemplateLiteralChar();
        this.ungetTemplateLiteralChar(c);
        return c;
    }

    int readTemplateLiteral(boolean isTaggedLiteral) throws IOException {
        this.rawString.setLength(0);
        this.stringBufferTop = 0;
        boolean hasInvalidEscapeSequences = false;
        block20: while (true) {
            int c = this.getTemplateLiteralChar();
            switch (c) {
                case -1: {
                    this.string = hasInvalidEscapeSequences ? null : this.getStringFromBuffer();
                    this.tokenEnd = this.cursor - 1;
                    this.parser.reportError("msg.unexpected.eof");
                    return -1;
                }
                case 96: {
                    this.rawString.setLength(this.rawString.length() - 1);
                    this.string = hasInvalidEscapeSequences ? null : this.getStringFromBuffer();
                    return 170;
                }
                case 36: {
                    if (this.matchTemplateLiteralChar(123)) {
                        this.rawString.setLength(this.rawString.length() - 2);
                        this.string = hasInvalidEscapeSequences ? null : this.getStringFromBuffer();
                        this.tokenEnd = this.cursor - 1;
                        return 172;
                    }
                    this.addToString(c);
                    continue block20;
                }
                case 92: {
                    c = this.getTemplateLiteralChar();
                    switch (c) {
                        case 10: 
                        case 8232: 
                        case 8233: {
                            continue block20;
                        }
                        case 34: 
                        case 39: 
                        case 92: {
                            break;
                        }
                        case 98: {
                            c = 8;
                            break;
                        }
                        case 102: {
                            c = 12;
                            break;
                        }
                        case 110: {
                            c = 10;
                            break;
                        }
                        case 114: {
                            c = 13;
                            break;
                        }
                        case 116: {
                            c = 9;
                            break;
                        }
                        case 118: {
                            c = 11;
                            break;
                        }
                        case 120: {
                            int i;
                            int escapeVal = 0;
                            for (i = 0; i < 2; ++i) {
                                if (this.peekTemplateLiteralChar() == 96) {
                                    escapeVal = -1;
                                    break;
                                }
                                escapeVal = Kit.xDigitToInt(this.getTemplateLiteralChar(), escapeVal);
                            }
                            if (escapeVal < 0) {
                                if (isTaggedLiteral) {
                                    hasInvalidEscapeSequences = true;
                                    continue block20;
                                }
                                this.parser.reportError("msg.syntax");
                                return -1;
                            }
                            c = escapeVal;
                            break;
                        }
                        case 117: {
                            int i;
                            int escapeVal = 0;
                            if (this.matchTemplateLiteralChar(123)) {
                                while (true) {
                                    if (this.peekTemplateLiteralChar() == 96) {
                                        escapeVal = -1;
                                        break;
                                    }
                                    c = this.getTemplateLiteralChar();
                                    if (c == 125) break;
                                    escapeVal = Kit.xDigitToInt(c, escapeVal);
                                }
                                if (escapeVal < 0 || escapeVal > 0x10FFFF) {
                                    if (isTaggedLiteral) {
                                        hasInvalidEscapeSequences = true;
                                        continue block20;
                                    }
                                    this.parser.reportError("msg.syntax");
                                    return -1;
                                }
                                if (escapeVal > 65535) {
                                    this.addToString(Character.highSurrogate(escapeVal));
                                    this.addToString(Character.lowSurrogate(escapeVal));
                                    continue block20;
                                }
                                c = escapeVal;
                                break;
                            }
                            for (i = 0; i < 4; ++i) {
                                if (this.peekTemplateLiteralChar() == 96) {
                                    escapeVal = -1;
                                    break;
                                }
                                escapeVal = Kit.xDigitToInt(this.getTemplateLiteralChar(), escapeVal);
                            }
                            if (escapeVal < 0) {
                                if (isTaggedLiteral) {
                                    hasInvalidEscapeSequences = true;
                                    continue block20;
                                }
                                this.parser.reportError("msg.syntax");
                                return -1;
                            }
                            c = escapeVal;
                            break;
                        }
                        case 48: {
                            int d = this.peekTemplateLiteralChar();
                            if (d >= 48 && d <= 57) {
                                if (isTaggedLiteral) {
                                    hasInvalidEscapeSequences = true;
                                    continue block20;
                                }
                                this.parser.reportError("msg.syntax");
                                return -1;
                            }
                            c = 0;
                            break;
                        }
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 53: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: {
                            if (isTaggedLiteral) {
                                hasInvalidEscapeSequences = true;
                                continue block20;
                            }
                            this.parser.reportError("msg.syntax");
                            return -1;
                        }
                    }
                    this.addToString(c);
                    continue block20;
                }
            }
            this.addToString(c);
        }
    }

    boolean isXMLAttribute() {
        return this.xmlIsAttribute;
    }

    int getFirstXMLToken() throws IOException {
        this.xmlOpenTagsCount = 0;
        this.xmlIsAttribute = false;
        this.xmlIsTagContent = false;
        if (!this.canUngetChar()) {
            return -1;
        }
        this.ungetChar(60);
        return this.getNextXMLToken();
    }

    int getNextXMLToken() throws IOException {
        this.tokenBeg = this.cursor;
        this.stringBufferTop = 0;
        int c = this.getChar();
        while (c != -1) {
            if (this.xmlIsTagContent) {
                switch (c) {
                    case 62: {
                        this.addToString(c);
                        this.xmlIsTagContent = false;
                        this.xmlIsAttribute = false;
                        break;
                    }
                    case 47: {
                        this.addToString(c);
                        if (this.peekChar() != 62) break;
                        c = this.getChar();
                        this.addToString(c);
                        this.xmlIsTagContent = false;
                        --this.xmlOpenTagsCount;
                        break;
                    }
                    case 123: {
                        this.ungetChar(c);
                        this.string = this.getStringFromBuffer();
                        return 149;
                    }
                    case 34: 
                    case 39: {
                        this.addToString(c);
                        if (this.readQuotedString(c)) break;
                        return -1;
                    }
                    case 61: {
                        this.addToString(c);
                        this.xmlIsAttribute = true;
                        break;
                    }
                    case 9: 
                    case 10: 
                    case 13: 
                    case 32: {
                        this.addToString(c);
                        break;
                    }
                    default: {
                        this.addToString(c);
                        this.xmlIsAttribute = false;
                    }
                }
                if (!this.xmlIsTagContent && this.xmlOpenTagsCount == 0) {
                    this.string = this.getStringFromBuffer();
                    return 152;
                }
            } else {
                block8 : switch (c) {
                    case 60: {
                        this.addToString(c);
                        c = this.peekChar();
                        switch (c) {
                            case 33: {
                                c = this.getChar();
                                this.addToString(c);
                                c = this.peekChar();
                                switch (c) {
                                    case 45: {
                                        c = this.getChar();
                                        this.addToString(c);
                                        c = this.getChar();
                                        if (c == 45) {
                                            this.addToString(c);
                                            if (this.readXmlComment()) break block8;
                                            return -1;
                                        }
                                        this.stringBufferTop = 0;
                                        this.string = null;
                                        this.parser.addError("msg.XML.bad.form");
                                        return -1;
                                    }
                                    case 91: {
                                        c = this.getChar();
                                        this.addToString(c);
                                        if (this.getChar() == 67 && this.getChar() == 68 && this.getChar() == 65 && this.getChar() == 84 && this.getChar() == 65 && this.getChar() == 91) {
                                            this.addToString(67);
                                            this.addToString(68);
                                            this.addToString(65);
                                            this.addToString(84);
                                            this.addToString(65);
                                            this.addToString(91);
                                            if (this.readCDATA()) break block8;
                                            return -1;
                                        }
                                        this.stringBufferTop = 0;
                                        this.string = null;
                                        this.parser.addError("msg.XML.bad.form");
                                        return -1;
                                    }
                                    default: {
                                        if (this.readEntity()) break block8;
                                        return -1;
                                    }
                                }
                            }
                            case 63: {
                                c = this.getChar();
                                this.addToString(c);
                                if (this.readPI()) break block8;
                                return -1;
                            }
                            case 47: {
                                c = this.getChar();
                                this.addToString(c);
                                if (this.xmlOpenTagsCount == 0) {
                                    this.stringBufferTop = 0;
                                    this.string = null;
                                    this.parser.addError("msg.XML.bad.form");
                                    return -1;
                                }
                                this.xmlIsTagContent = true;
                                --this.xmlOpenTagsCount;
                                break;
                            }
                            default: {
                                this.xmlIsTagContent = true;
                                ++this.xmlOpenTagsCount;
                                break;
                            }
                        }
                        break;
                    }
                    case 123: {
                        this.ungetChar(c);
                        this.string = this.getStringFromBuffer();
                        return 149;
                    }
                    default: {
                        this.addToString(c);
                    }
                }
            }
            c = this.getChar();
        }
        this.tokenEnd = this.cursor;
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return -1;
    }

    private boolean readQuotedString(int quote) throws IOException {
        int c = this.getChar();
        while (c != -1) {
            this.addToString(c);
            if (c == quote) {
                return true;
            }
            c = this.getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private boolean readXmlComment() throws IOException {
        int c = this.getChar();
        while (c != -1) {
            this.addToString(c);
            if (c == 45 && this.peekChar() == 45) {
                c = this.getChar();
                this.addToString(c);
                if (this.peekChar() != 62) continue;
                c = this.getChar();
                this.addToString(c);
                return true;
            }
            c = this.getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private boolean readCDATA() throws IOException {
        int c = this.getChar();
        while (c != -1) {
            this.addToString(c);
            if (c == 93 && this.peekChar() == 93) {
                c = this.getChar();
                this.addToString(c);
                if (this.peekChar() != 62) continue;
                c = this.getChar();
                this.addToString(c);
                return true;
            }
            c = this.getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private boolean readEntity() throws IOException {
        int declTags = 1;
        int c = this.getChar();
        while (c != -1) {
            this.addToString(c);
            switch (c) {
                case 60: {
                    ++declTags;
                    break;
                }
                case 62: {
                    if (--declTags != 0) break;
                    return true;
                }
            }
            c = this.getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private boolean readPI() throws IOException {
        int c = this.getChar();
        while (c != -1) {
            this.addToString(c);
            if (c == 63 && this.peekChar() == 62) {
                c = this.getChar();
                this.addToString(c);
                return true;
            }
            c = this.getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return false;
    }

    private String getStringFromBuffer() {
        this.tokenEnd = this.cursor;
        return new String(this.stringBuffer, 0, this.stringBufferTop);
    }

    private void addToString(int c) {
        int N = this.stringBufferTop;
        if (N == this.stringBuffer.length) {
            char[] tmp = new char[this.stringBuffer.length * 2];
            System.arraycopy(this.stringBuffer, 0, tmp, 0, N);
            this.stringBuffer = tmp;
        }
        this.stringBuffer[N] = (char)c;
        this.stringBufferTop = N + 1;
    }

    private boolean canUngetChar() {
        return this.ungetCursor == 0 || this.ungetBuffer[this.ungetCursor - 1] != 10;
    }

    private void ungetChar(int c) {
        if (this.ungetCursor != 0 && this.ungetBuffer[this.ungetCursor - 1] == 10) {
            Kit.codeBug();
        }
        this.ungetBuffer[this.ungetCursor++] = c;
        --this.cursor;
    }

    private boolean matchChar(int test) throws IOException {
        int c = this.getCharIgnoreLineEnd();
        if (c == test) {
            this.tokenEnd = this.cursor;
            return true;
        }
        this.ungetCharIgnoreLineEnd(c);
        return false;
    }

    private int peekChar() throws IOException {
        int c = this.getChar();
        this.ungetChar(c);
        return c;
    }

    private int getChar() throws IOException {
        return this.getChar(true, false);
    }

    private int getChar(boolean skipFormattingChars) throws IOException {
        return this.getChar(skipFormattingChars, false);
    }

    private int getChar(boolean skipFormattingChars, boolean ignoreLineEnd) throws IOException {
        int c;
        block12: {
            if (this.ungetCursor != 0) {
                ++this.cursor;
                return this.ungetBuffer[--this.ungetCursor];
            }
            while (true) {
                if (this.sourceString != null) {
                    if (this.sourceCursor == this.sourceEnd) {
                        this.hitEOF = true;
                        return -1;
                    }
                    ++this.cursor;
                    c = this.sourceString.charAt(this.sourceCursor++);
                } else {
                    if (this.sourceCursor == this.sourceEnd && !this.fillSourceBuffer()) {
                        this.hitEOF = true;
                        return -1;
                    }
                    ++this.cursor;
                    c = this.sourceBuffer[this.sourceCursor++];
                }
                if (!ignoreLineEnd && this.lineEndChar >= 0) {
                    if (this.lineEndChar == 13 && c == 10) {
                        this.lineEndChar = 10;
                        continue;
                    }
                    this.lineEndChar = -1;
                    this.lineStart = this.sourceCursor - 1;
                    ++this.lineno;
                }
                if (c <= 127) {
                    if (c == 10 || c == 13) {
                        this.lineEndChar = c;
                        c = 10;
                    }
                    break block12;
                }
                if (c == 65279) {
                    return c;
                }
                if (!skipFormattingChars || !TokenStream.isJSFormatChar(c)) break;
            }
            if (ScriptRuntime.isJSLineTerminator(c)) {
                this.lineEndChar = c;
                c = 10;
            }
        }
        return c;
    }

    private int getCharIgnoreLineEnd() throws IOException {
        return this.getChar(true, true);
    }

    private int getCharIgnoreLineEnd(boolean skipFormattingChars) throws IOException {
        return this.getChar(skipFormattingChars, true);
    }

    private void ungetCharIgnoreLineEnd(int c) {
        this.ungetBuffer[this.ungetCursor++] = c;
        --this.cursor;
    }

    private void skipLine() throws IOException {
        int c;
        while ((c = this.getChar()) != -1 && c != 10) {
        }
        this.ungetChar(c);
        this.tokenEnd = this.cursor;
    }

    final int getOffset() {
        int n = this.sourceCursor - this.lineStart;
        if (this.lineEndChar >= 0) {
            --n;
        }
        return n;
    }

    private final int charAt(int index) {
        if (index < 0) {
            return -1;
        }
        if (this.sourceString != null) {
            if (index >= this.sourceEnd) {
                return -1;
            }
            return this.sourceString.charAt(index);
        }
        if (index >= this.sourceEnd) {
            int oldSourceCursor = this.sourceCursor;
            try {
                if (!this.fillSourceBuffer()) {
                    return -1;
                }
            }
            catch (IOException ioe) {
                return -1;
            }
            index -= oldSourceCursor - this.sourceCursor;
        }
        return this.sourceBuffer[index];
    }

    private final String substring(int beginIndex, int endIndex) {
        if (this.sourceString != null) {
            return this.sourceString.substring(beginIndex, endIndex);
        }
        int count = endIndex - beginIndex;
        return new String(this.sourceBuffer, beginIndex, count);
    }

    final String getLine() {
        int lineEnd = this.sourceCursor;
        if (this.lineEndChar >= 0) {
            if (this.lineEndChar == 10 && this.charAt(--lineEnd - 1) == 13) {
                --lineEnd;
            }
        } else {
            int c;
            int lineLength = lineEnd - this.lineStart;
            while ((c = this.charAt(this.lineStart + lineLength)) != -1 && !ScriptRuntime.isJSLineTerminator(c)) {
                ++lineLength;
            }
            lineEnd = this.lineStart + lineLength;
        }
        return this.substring(this.lineStart, lineEnd);
    }

    final String getLine(int position, int[] linep) {
        assert (position >= 0 && position <= this.cursor);
        assert (linep.length == 2);
        int delta = this.cursor + this.ungetCursor - position;
        int cur = this.sourceCursor;
        if (delta > cur) {
            return null;
        }
        int end = 0;
        int lines = 0;
        while (delta > 0) {
            assert (cur > 0);
            int c = this.charAt(cur - 1);
            if (ScriptRuntime.isJSLineTerminator(c)) {
                if (c == 10 && this.charAt(cur - 2) == 13) {
                    --delta;
                    --cur;
                }
                ++lines;
                end = cur - 1;
            }
            --delta;
            --cur;
        }
        int start = 0;
        int offset = 0;
        while (cur > 0) {
            int c = this.charAt(cur - 1);
            if (ScriptRuntime.isJSLineTerminator(c)) {
                start = cur;
                break;
            }
            --cur;
            ++offset;
        }
        linep[0] = this.lineno - lines + (this.lineEndChar >= 0 ? 1 : 0);
        linep[1] = offset;
        if (lines == 0) {
            return this.getLine();
        }
        return this.substring(start, end);
    }

    private boolean fillSourceBuffer() throws IOException {
        int n;
        if (this.sourceString != null) {
            Kit.codeBug();
        }
        if (this.sourceEnd == this.sourceBuffer.length) {
            if (this.lineStart != 0 && !this.isMarkingComment()) {
                System.arraycopy(this.sourceBuffer, this.lineStart, this.sourceBuffer, 0, this.sourceEnd - this.lineStart);
                this.sourceEnd -= this.lineStart;
                this.sourceCursor -= this.lineStart;
                this.lineStart = 0;
            } else {
                char[] tmp = new char[this.sourceBuffer.length * 2];
                System.arraycopy(this.sourceBuffer, 0, tmp, 0, this.sourceEnd);
                this.sourceBuffer = tmp;
            }
        }
        if ((n = this.sourceReader.read(this.sourceBuffer, this.sourceEnd, this.sourceBuffer.length - this.sourceEnd)) < 0) {
            return false;
        }
        this.sourceEnd += n;
        return true;
    }

    public int getCursor() {
        return this.cursor;
    }

    public int getTokenBeg() {
        return this.tokenBeg;
    }

    public int getTokenEnd() {
        return this.tokenEnd;
    }

    public int getTokenLength() {
        return this.tokenEnd - this.tokenBeg;
    }

    public Token.CommentType getCommentType() {
        return this.commentType;
    }

    private void markCommentStart() {
        this.markCommentStart("");
    }

    private void markCommentStart(String prefix) {
        if (this.parser.compilerEnv.isRecordingComments() && this.sourceReader != null) {
            this.commentPrefix = prefix;
            this.commentCursor = this.sourceCursor - 1;
        }
    }

    private boolean isMarkingComment() {
        return this.commentCursor != -1;
    }

    final String getAndResetCurrentComment() {
        if (this.sourceString != null) {
            if (this.isMarkingComment()) {
                Kit.codeBug();
            }
            return this.sourceString.substring(this.tokenBeg, this.tokenEnd);
        }
        if (!this.isMarkingComment()) {
            Kit.codeBug();
        }
        StringBuilder comment = new StringBuilder(this.commentPrefix);
        comment.append(this.sourceBuffer, this.commentCursor, this.getTokenLength() - this.commentPrefix.length());
        this.commentCursor = -1;
        return comment.toString();
    }

    private static String convertLastCharToHex(String str) {
        int lastIndex = str.length() - 1;
        StringBuilder buf = new StringBuilder(str.substring(0, lastIndex));
        buf.append("\\u");
        String hexCode = Integer.toHexString(str.charAt(lastIndex));
        for (int i = 0; i < 4 - hexCode.length(); ++i) {
            buf.append('0');
        }
        buf.append(hexCode);
        return buf.toString();
    }
}

