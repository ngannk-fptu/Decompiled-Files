/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.math.BigInteger;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.UintMap;

public class Decompiler {
    public static final int ONLY_BODY_FLAG = 1;
    public static final int TO_SOURCE_FLAG = 2;
    public static final int INITIAL_INDENT_PROP = 1;
    public static final int INDENT_GAP_PROP = 2;
    public static final int CASE_GAP_PROP = 3;
    private static final int FUNCTION_END = 174;
    private char[] sourceBuffer = new char[128];
    private int sourceTop;
    private static final boolean printSource = false;

    String getEncodedSource() {
        return this.sourceToString(0);
    }

    int getCurrentOffset() {
        return this.sourceTop;
    }

    int markFunctionStart(int functionType) {
        int savedOffset = this.getCurrentOffset();
        if (functionType != 4) {
            this.addToken(113);
            this.append((char)functionType);
        }
        return savedOffset;
    }

    int markFunctionEnd(int functionStart) {
        int offset = this.getCurrentOffset();
        this.append('\u00ae');
        return offset;
    }

    void addToken(int token) {
        if (0 > token || token > 173) {
            throw new IllegalArgumentException();
        }
        this.append((char)token);
    }

    void addEOL(int token) {
        if (0 > token || token > 173) {
            throw new IllegalArgumentException();
        }
        this.append((char)token);
        this.append('\u0001');
    }

    void addName(String str) {
        this.addToken(39);
        this.appendString(str);
    }

    void addString(String str) {
        this.addToken(41);
        this.appendString(str);
    }

    void addTemplateLiteral(String str) {
        this.addToken(171);
        this.appendString(str);
    }

    void addRegexp(String regexp, String flags) {
        this.addToken(48);
        this.appendString('/' + regexp + '/' + flags);
    }

    void addNumber(double n) {
        this.addToken(40);
        long lbits = (long)n;
        if ((double)lbits != n) {
            lbits = Double.doubleToLongBits(n);
            this.append('D');
            this.append((char)(lbits >> 48));
            this.append((char)(lbits >> 32));
            this.append((char)(lbits >> 16));
            this.append((char)lbits);
        } else {
            if (lbits < 0L) {
                Kit.codeBug();
            }
            if (lbits <= 65535L) {
                this.append('S');
                this.append((char)lbits);
            } else {
                this.append('J');
                this.append((char)(lbits >> 48));
                this.append((char)(lbits >> 32));
                this.append((char)(lbits >> 16));
                this.append((char)lbits);
            }
        }
    }

    void addBigInt(BigInteger n) {
        this.addToken(83);
        this.appendString(n.toString());
    }

    private void appendString(String str) {
        int nextTop;
        int L = str.length();
        int lengthEncodingSize = 1;
        if (L >= 32768) {
            lengthEncodingSize = 2;
        }
        if ((nextTop = this.sourceTop + lengthEncodingSize + L) > this.sourceBuffer.length) {
            this.increaseSourceCapacity(nextTop);
        }
        if (L >= 32768) {
            this.sourceBuffer[this.sourceTop] = (char)(0x8000 | L >>> 16);
            ++this.sourceTop;
        }
        this.sourceBuffer[this.sourceTop] = (char)L;
        ++this.sourceTop;
        str.getChars(0, L, this.sourceBuffer, this.sourceTop);
        this.sourceTop = nextTop;
    }

    private void append(char c) {
        if (this.sourceTop == this.sourceBuffer.length) {
            this.increaseSourceCapacity(this.sourceTop + 1);
        }
        this.sourceBuffer[this.sourceTop] = c;
        ++this.sourceTop;
    }

    private void increaseSourceCapacity(int minimalCapacity) {
        int newCapacity;
        if (minimalCapacity <= this.sourceBuffer.length) {
            Kit.codeBug();
        }
        if ((newCapacity = this.sourceBuffer.length * 2) < minimalCapacity) {
            newCapacity = minimalCapacity;
        }
        char[] tmp = new char[newCapacity];
        System.arraycopy(this.sourceBuffer, 0, tmp, 0, this.sourceTop);
        this.sourceBuffer = tmp;
    }

    private String sourceToString(int offset) {
        if (offset < 0 || this.sourceTop < offset) {
            Kit.codeBug();
        }
        return new String(this.sourceBuffer, offset, this.sourceTop - offset);
    }

    public static String decompile(String source, int flags, UintMap properties) {
        int topFunctionType;
        int length = source.length();
        if (length == 0) {
            return "";
        }
        int indent = properties.getInt(1, 0);
        if (indent < 0) {
            throw new IllegalArgumentException();
        }
        int indentGap = properties.getInt(2, 4);
        if (indentGap < 0) {
            throw new IllegalArgumentException();
        }
        int caseGap = properties.getInt(3, 2);
        if (caseGap < 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder result = new StringBuilder();
        boolean justFunctionBody = 0 != (flags & 1);
        boolean toSource = 0 != (flags & 2);
        int braceNesting = 0;
        boolean afterFirstEOL = false;
        int i = 0;
        if (source.charAt(i) == '\u008c') {
            ++i;
            topFunctionType = -1;
        } else {
            topFunctionType = source.charAt(i + 1);
        }
        if (!toSource) {
            result.append('\n');
            for (int j = 0; j < indent; ++j) {
                result.append(' ');
            }
        } else if (topFunctionType == 2) {
            result.append('(');
        }
        block107: while (i < length) {
            switch (source.charAt(i)) {
                case '\u009b': 
                case '\u009c': 
                case '\u00a7': {
                    if (source.charAt(i) == '\u009b') {
                        result.append("get ");
                    } else if (source.charAt(i) == '\u009c') {
                        result.append("set ");
                    }
                    ++i;
                    i = Decompiler.printSourceString(source, i + 1, false, result);
                    ++i;
                    break;
                }
                case '\'': 
                case '0': {
                    i = Decompiler.printSourceString(source, i + 1, false, result);
                    continue block107;
                }
                case ')': {
                    i = Decompiler.printSourceString(source, i + 1, true, result);
                    continue block107;
                }
                case '(': {
                    i = Decompiler.printSourceNumber(source, i + 1, result);
                    continue block107;
                }
                case 'S': {
                    i = Decompiler.printSourceBigInt(source, i + 1, result);
                    continue block107;
                }
                case '-': {
                    result.append("true");
                    break;
                }
                case ',': {
                    result.append("false");
                    break;
                }
                case '*': {
                    result.append("null");
                    break;
                }
                case '+': {
                    result.append("this");
                    break;
                }
                case 'q': {
                    ++i;
                    result.append("function ");
                    break;
                }
                case '\u00ae': {
                    break;
                }
                case '\\': {
                    result.append(", ");
                    break;
                }
                case 'X': {
                    ++braceNesting;
                    if (1 == Decompiler.getNext(source, length, i)) {
                        indent += indentGap;
                    }
                    result.append('{');
                    break;
                }
                case 'Y': {
                    if (justFunctionBody && --braceNesting == 0) break;
                    result.append('}');
                    switch (Decompiler.getNext(source, length, i)) {
                        case 1: 
                        case 174: {
                            indent -= indentGap;
                            break;
                        }
                        case 117: 
                        case 121: {
                            indent -= indentGap;
                            result.append(' ');
                        }
                    }
                    break;
                }
                case 'Z': {
                    result.append('(');
                    break;
                }
                case '[': {
                    result.append(')');
                    if (88 != Decompiler.getNext(source, length, i)) break;
                    result.append(' ');
                    break;
                }
                case 'V': {
                    result.append('[');
                    break;
                }
                case 'W': {
                    result.append(']');
                    break;
                }
                case '\u0001': {
                    int afterName;
                    if (toSource) break;
                    boolean newLine = true;
                    if (!afterFirstEOL) {
                        afterFirstEOL = true;
                        if (justFunctionBody) {
                            result.setLength(0);
                            indent -= indentGap;
                            newLine = false;
                        }
                    }
                    if (newLine) {
                        result.append('\n');
                    }
                    if (i + 1 >= length) break;
                    int less = 0;
                    char nextToken = source.charAt(i + 1);
                    if (nextToken == 'w' || nextToken == 'x') {
                        less = indentGap - caseGap;
                    } else if (nextToken == 'Y') {
                        less = indentGap;
                    } else if (nextToken == '\'' && source.charAt(afterName = Decompiler.getSourceStringEnd(source, i + 2)) == 'k') {
                        less = indentGap;
                    }
                    while (less < indent) {
                        result.append(' ');
                        ++less;
                    }
                    break;
                }
                case 'p': {
                    result.append('.');
                    break;
                }
                case '\u001e': {
                    result.append("new ");
                    break;
                }
                case '\u001f': {
                    result.append("delete ");
                    break;
                }
                case 't': {
                    result.append("if ");
                    break;
                }
                case 'u': {
                    result.append("else ");
                    break;
                }
                case '{': {
                    result.append("for ");
                    break;
                }
                case '4': {
                    result.append(" in ");
                    break;
                }
                case '\u007f': {
                    result.append("with ");
                    break;
                }
                case 'y': {
                    result.append("while ");
                    break;
                }
                case 'z': {
                    result.append("do ");
                    break;
                }
                case 'T': {
                    result.append("try ");
                    break;
                }
                case '\u0080': {
                    result.append("catch ");
                    break;
                }
                case '\u0081': {
                    result.append("finally ");
                    break;
                }
                case '2': {
                    result.append("throw ");
                    break;
                }
                case 'v': {
                    result.append("switch ");
                    break;
                }
                case '|': {
                    result.append("break");
                    if (39 != Decompiler.getNext(source, length, i)) break;
                    result.append(' ');
                    break;
                }
                case '}': {
                    result.append("continue");
                    if (39 != Decompiler.getNext(source, length, i)) break;
                    result.append(' ');
                    break;
                }
                case 'w': {
                    result.append("case ");
                    break;
                }
                case 'x': {
                    result.append("default");
                    break;
                }
                case '\u0004': {
                    result.append("return");
                    if (85 == Decompiler.getNext(source, length, i)) break;
                    result.append(' ');
                    break;
                }
                case '~': {
                    result.append("var ");
                    break;
                }
                case '\u009d': {
                    result.append("let ");
                    break;
                }
                case 'U': {
                    result.append(';');
                    if (1 == Decompiler.getNext(source, length, i)) break;
                    result.append(' ');
                    break;
                }
                case ']': {
                    result.append(" = ");
                    break;
                }
                case 'd': {
                    result.append(" += ");
                    break;
                }
                case 'e': {
                    result.append(" -= ");
                    break;
                }
                case 'f': {
                    result.append(" *= ");
                    break;
                }
                case 'g': {
                    result.append(" /= ");
                    break;
                }
                case 'h': {
                    result.append(" %= ");
                    break;
                }
                case '^': {
                    result.append(" |= ");
                    break;
                }
                case '_': {
                    result.append(" ^= ");
                    break;
                }
                case '`': {
                    result.append(" &= ");
                    break;
                }
                case 'a': {
                    result.append(" <<= ");
                    break;
                }
                case 'b': {
                    result.append(" >>= ");
                    break;
                }
                case 'c': {
                    result.append(" >>>= ");
                    break;
                }
                case 'j': {
                    result.append(" ? ");
                    break;
                }
                case 'C': {
                    result.append(": ");
                    break;
                }
                case 'k': {
                    if (1 == Decompiler.getNext(source, length, i)) {
                        result.append(':');
                        break;
                    }
                    result.append(" : ");
                    break;
                }
                case 'l': {
                    result.append(" || ");
                    break;
                }
                case 'm': {
                    result.append(" && ");
                    break;
                }
                case '\t': {
                    result.append(" | ");
                    break;
                }
                case '\n': {
                    result.append(" ^ ");
                    break;
                }
                case '\u000b': {
                    result.append(" & ");
                    break;
                }
                case '.': {
                    result.append(" === ");
                    break;
                }
                case '/': {
                    result.append(" !== ");
                    break;
                }
                case '\f': {
                    result.append(" == ");
                    break;
                }
                case '\r': {
                    result.append(" != ");
                    break;
                }
                case '\u000f': {
                    result.append(" <= ");
                    break;
                }
                case '\u000e': {
                    result.append(" < ");
                    break;
                }
                case '\u0011': {
                    result.append(" >= ");
                    break;
                }
                case '\u0010': {
                    result.append(" > ");
                    break;
                }
                case '5': {
                    result.append(" instanceof ");
                    break;
                }
                case '\u0012': {
                    result.append(" << ");
                    break;
                }
                case '\u0013': {
                    result.append(" >> ");
                    break;
                }
                case '\u0014': {
                    result.append(" >>> ");
                    break;
                }
                case ' ': {
                    result.append("typeof ");
                    break;
                }
                case '\u0082': {
                    result.append("void ");
                    break;
                }
                case '\u009e': {
                    result.append("const ");
                    break;
                }
                case 'I': {
                    result.append("yield ");
                    break;
                }
                case '\u00a9': {
                    result.append("yield *");
                    break;
                }
                case '\u001a': {
                    result.append('!');
                    break;
                }
                case '\u001b': {
                    result.append('~');
                    break;
                }
                case '\u001c': {
                    result.append('+');
                    break;
                }
                case '\u001d': {
                    result.append('-');
                    break;
                }
                case 'n': {
                    result.append("++");
                    break;
                }
                case 'o': {
                    result.append("--");
                    break;
                }
                case '\u0015': {
                    result.append(" + ");
                    break;
                }
                case '\u0016': {
                    result.append(" - ");
                    break;
                }
                case '\u0017': {
                    result.append(" * ");
                    break;
                }
                case '\u0018': {
                    result.append(" / ");
                    break;
                }
                case '\u0019': {
                    result.append(" % ");
                    break;
                }
                case 'K': {
                    result.append(" ** ");
                    break;
                }
                case '\u0094': {
                    result.append("::");
                    break;
                }
                case '\u0093': {
                    result.append("..");
                    break;
                }
                case '\u0096': {
                    result.append(".(");
                    break;
                }
                case '\u0097': {
                    result.append('@');
                    break;
                }
                case '\u00a4': {
                    result.append("debugger;\n");
                    break;
                }
                case '\u00a8': {
                    result.append(" => ");
                    break;
                }
                case '\u00aa': {
                    result.append("`");
                    break;
                }
                case '\u00ac': {
                    result.append("${");
                    break;
                }
                case '\u00ab': {
                    i = Decompiler.printSourceString(source, i + 1, false, result);
                    continue block107;
                }
                default: {
                    throw new RuntimeException("Token: " + Token.name(source.charAt(i)));
                }
            }
            ++i;
        }
        if (!toSource) {
            if (!justFunctionBody) {
                result.append('\n');
            }
        } else if (topFunctionType == 2) {
            result.append(')');
        }
        return result.toString();
    }

    private static int getNext(String source, int length, int i) {
        return i + 1 < length ? (int)source.charAt(i + 1) : 0;
    }

    private static int getSourceStringEnd(String source, int offset) {
        return Decompiler.printSourceString(source, offset, false, null);
    }

    private static int printSourceString(String source, int offset, boolean asQuotedString, StringBuilder sb) {
        int length = source.charAt(offset);
        ++offset;
        if ((0x8000 & length) != 0) {
            length = (Short.MAX_VALUE & length) << 16 | source.charAt(offset);
            ++offset;
        }
        if (sb != null) {
            String str = source.substring(offset, offset + length);
            if (!asQuotedString) {
                sb.append(str);
            } else {
                sb.append('\"');
                sb.append(ScriptRuntime.escapeString(str));
                sb.append('\"');
            }
        }
        return offset + length;
    }

    private static int printSourceNumber(String source, int offset, StringBuilder sb) {
        double number = 0.0;
        char type = source.charAt(offset);
        ++offset;
        if (type == 'S') {
            if (sb != null) {
                char ival = source.charAt(offset);
                number = ival;
            }
            ++offset;
        } else if (type == 'J' || type == 'D') {
            if (sb != null) {
                long lbits = (long)source.charAt(offset) << 48;
                lbits |= (long)source.charAt(offset + 1) << 32;
                lbits |= (long)source.charAt(offset + 2) << 16;
                number = type == 'J' ? (double)lbits : Double.longBitsToDouble(lbits |= (long)source.charAt(offset + 3));
            }
            offset += 4;
        } else {
            throw new RuntimeException();
        }
        if (sb != null) {
            sb.append(ScriptRuntime.numberToString(number, 10));
        }
        return offset;
    }

    private static int printSourceBigInt(String source, int offset, StringBuilder sb) {
        int length = source.charAt(offset);
        ++offset;
        if ((0x8000 & length) != 0) {
            length = (Short.MAX_VALUE & length) << 16 | source.charAt(offset);
            ++offset;
        }
        if (sb != null) {
            String str = source.substring(offset, offset + length);
            sb.append(str);
            sb.append('n');
        }
        return offset + length;
    }
}

