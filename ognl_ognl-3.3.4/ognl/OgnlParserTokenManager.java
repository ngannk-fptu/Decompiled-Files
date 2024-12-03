/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import ognl.DynamicSubscript;
import ognl.JavaCharStream;
import ognl.OgnlParserConstants;
import ognl.Token;
import ognl.TokenMgrError;

public class OgnlParserTokenManager
implements OgnlParserConstants {
    Object literalValue;
    private char charValue;
    private char charLiteralStartQuote;
    private StringBuffer stringBuffer;
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{2301339413881290750L, -16384L, 0xFFFFFFFFL, 0x600000000000000L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, 0L, -36028797027352577L};
    static final long[] jjbitVec3 = new long[]{0L, -1L, -1L, -1L};
    static final long[] jjbitVec4 = new long[]{-1L, -1L, 65535L, 0L};
    static final long[] jjbitVec5 = new long[]{-1L, -1L, 0L, 0L};
    static final long[] jjbitVec6 = new long[]{0x3FFFFFFFFFFFL, 0L, 0L, 0L};
    static final long[] jjbitVec7 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec8 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{15, 16, 18, 19, 22, 13, 24, 25, 7, 9, 10, 13, 17, 10, 13, 11, 12, 20, 21, 1, 2, 3};
    public static final String[] jjstrLiteralImages = new String[]{"", ",", "=", "?", ":", "||", "or", "&&", "and", "|", "bor", "^", "xor", "&", "band", "==", "eq", "!=", "neq", "<", "lt", ">", "gt", "<=", "lte", ">=", "gte", "in", "not", "<<", "shl", ">>", "shr", ">>>", "ushr", "+", "-", "*", "/", "%", "~", "!", "instanceof", ".", "(", ")", "true", "false", "null", "#this", "#root", "#", "[", "]", "{", "}", "@", "new", "$", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "WithinCharLiteral", "WithinBackCharLiteral", "WithinStringLiteral"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 3, -1, -1, 0, -1, -1, 0, -1, -1, 0, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{0x7FFFFFFFFFFFFFFL, 233993L};
    static final long[] jjtoSkip = new long[]{-576460752303423488L, 0L};
    static final long[] jjtoMore = new long[]{0L, 28144L};
    protected JavaCharStream input_stream;
    private final int[] jjrounds = new int[27];
    private final int[] jjstateSet = new int[54];
    private final StringBuffer image = new StringBuffer();
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    private char escapeChar() {
        int ofs = this.image.length() - 1;
        switch (this.image.charAt(ofs)) {
            case 'n': {
                return '\n';
            }
            case 'r': {
                return '\r';
            }
            case 't': {
                return '\t';
            }
            case 'b': {
                return '\b';
            }
            case 'f': {
                return '\f';
            }
            case '\\': {
                return '\\';
            }
            case '\'': {
                return '\'';
            }
            case '\"': {
                return '\"';
            }
        }
        while (this.image.charAt(--ofs) != '\\') {
        }
        int value = 0;
        while (++ofs < this.image.length()) {
            value = value << 3 | this.image.charAt(ofs) - 48;
        }
        return (char)value;
    }

    private Object makeInt() {
        Number result;
        String s = this.image.toString();
        int base = 10;
        if (s.charAt(0) == '0') {
            int n = base = s.length() > 1 && (s.charAt(1) == 'x' || s.charAt(1) == 'X') ? 16 : 8;
        }
        if (base == 16) {
            s = s.substring(2);
        }
        switch (s.charAt(s.length() - 1)) {
            case 'L': 
            case 'l': {
                result = Long.valueOf(s.substring(0, s.length() - 1), base);
                break;
            }
            case 'H': 
            case 'h': {
                result = new BigInteger(s.substring(0, s.length() - 1), base);
                break;
            }
            default: {
                result = Integer.valueOf(s, base);
            }
        }
        return result;
    }

    private Object makeFloat() {
        String s = this.image.toString();
        switch (s.charAt(s.length() - 1)) {
            case 'F': 
            case 'f': {
                return Float.valueOf(s);
            }
            case 'B': 
            case 'b': {
                return new BigDecimal(s.substring(0, s.length() - 1));
            }
        }
        return Double.valueOf(s);
    }

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x201C4055D555540L) != 0L) {
                    this.jjmatchedKind = 64;
                    return 1;
                }
                if ((active0 & 0x400000000000000L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x10000000000000L) != 0L) {
                    return 3;
                }
                if ((active0 & 0x80000000000L) != 0L) {
                    return 9;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x201C00550045500L) != 0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 64;
                        this.jjmatchedPos = 1;
                    }
                    return 1;
                }
                if ((active0 & 0x4000D510040L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x1C40400004000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 2;
                    return 1;
                }
                if ((active0 & 0x200000155041500L) != 0L) {
                    return 1;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x1400400004000L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x840000000000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 3;
                    return 1;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x800000000000L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 4;
                    return 1;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 5;
                    return 1;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 6;
                    return 1;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 7;
                    return 1;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x40000000000L) != 0L) {
                    this.jjmatchedKind = 64;
                    this.jjmatchedPos = 8;
                    return 1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0, long active1) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '!': {
                this.jjmatchedKind = 41;
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case '\"': {
                return this.jjStopAtPos(0, 70);
            }
            case '#': {
                this.jjmatchedKind = 51;
                return this.jjMoveStringLiteralDfa1_0(0x6000000000000L);
            }
            case '$': {
                return this.jjStartNfaWithStates_0(0, 58, 1);
            }
            case '%': {
                return this.jjStopAtPos(0, 39);
            }
            case '&': {
                this.jjmatchedKind = 13;
                return this.jjMoveStringLiteralDfa1_0(128L);
            }
            case '\'': {
                return this.jjStopAtPos(0, 69);
            }
            case '(': {
                return this.jjStopAtPos(0, 44);
            }
            case ')': {
                return this.jjStopAtPos(0, 45);
            }
            case '*': {
                return this.jjStopAtPos(0, 37);
            }
            case '+': {
                return this.jjStopAtPos(0, 35);
            }
            case ',': {
                return this.jjStopAtPos(0, 1);
            }
            case '-': {
                return this.jjStopAtPos(0, 36);
            }
            case '.': {
                return this.jjStartNfaWithStates_0(0, 43, 9);
            }
            case '/': {
                return this.jjStopAtPos(0, 38);
            }
            case ':': {
                return this.jjStopAtPos(0, 4);
            }
            case '<': {
                this.jjmatchedKind = 19;
                return this.jjMoveStringLiteralDfa1_0(0x20800000L);
            }
            case '=': {
                this.jjmatchedKind = 2;
                return this.jjMoveStringLiteralDfa1_0(32768L);
            }
            case '>': {
                this.jjmatchedKind = 21;
                return this.jjMoveStringLiteralDfa1_0(0x282000000L);
            }
            case '?': {
                return this.jjStopAtPos(0, 3);
            }
            case '@': {
                return this.jjStopAtPos(0, 56);
            }
            case '[': {
                return this.jjStartNfaWithStates_0(0, 52, 3);
            }
            case ']': {
                return this.jjStopAtPos(0, 53);
            }
            case '^': {
                return this.jjStopAtPos(0, 11);
            }
            case '`': {
                return this.jjStopAtPos(0, 68);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(256L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(17408L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(65536L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(0x800000000000L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(0x4400000L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(0x40008000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(0x1100000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(144396663321264128L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(64L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(0x140000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(0x400000000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_0(0x400000000L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case '{': {
                return this.jjStopAtPos(0, 54);
            }
            case '|': {
                this.jjmatchedKind = 9;
                return this.jjMoveStringLiteralDfa1_0(32L);
            }
            case '}': {
                return this.jjStopAtPos(0, 55);
            }
            case '~': {
                return this.jjStopAtPos(0, 40);
            }
        }
        return this.jjMoveNfa_0(0, 0);
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '&': {
                if ((active0 & 0x80L) == 0L) break;
                return this.jjStopAtPos(1, 7);
            }
            case '<': {
                if ((active0 & 0x20000000L) == 0L) break;
                return this.jjStopAtPos(1, 29);
            }
            case '=': {
                if ((active0 & 0x8000L) != 0L) {
                    return this.jjStopAtPos(1, 15);
                }
                if ((active0 & 0x20000L) != 0L) {
                    return this.jjStopAtPos(1, 17);
                }
                if ((active0 & 0x800000L) != 0L) {
                    return this.jjStopAtPos(1, 23);
                }
                if ((active0 & 0x2000000L) == 0L) break;
                return this.jjStopAtPos(1, 25);
            }
            case '>': {
                if ((active0 & 0x80000000L) != 0L) {
                    this.jjmatchedKind = 31;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x200000000L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x800000004000L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x200000000040000L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x140000000L);
            }
            case 'n': {
                if ((active0 & 0x8000000L) != 0L) {
                    this.jjmatchedKind = 27;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x40000000100L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x10001400L);
            }
            case 'q': {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 16, 1);
            }
            case 'r': {
                if ((active0 & 0x40L) != 0L) {
                    return this.jjStartNfaWithStates_0(1, 6, 1);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x4400000000000L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x400000000L);
            }
            case 't': {
                if ((active0 & 0x100000L) != 0L) {
                    this.jjmatchedKind = 20;
                    this.jjmatchedPos = 1;
                } else if ((active0 & 0x400000L) != 0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0x2000005000000L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0x1000000000000L);
            }
            case '|': {
                if ((active0 & 0x20L) == 0L) break;
                return this.jjStopAtPos(1, 5);
            }
        }
        return this.jjStartNfa_0(0, active0, 0L);
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case '>': {
                if ((active0 & 0x200000000L) == 0L) break;
                return this.jjStopAtPos(2, 33);
            }
            case 'd': {
                if ((active0 & 0x100L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 8, 1);
            }
            case 'e': {
                if ((active0 & 0x1000000L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 24, 1);
                }
                if ((active0 & 0x4000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 26, 1);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x2000400000000L);
            }
            case 'l': {
                if ((active0 & 0x40000000L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 30, 1);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0x1800000000000L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa3_0(active0, 16384L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x4000000000000L);
            }
            case 'q': {
                if ((active0 & 0x40000L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 18, 1);
            }
            case 'r': {
                if ((active0 & 0x400L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 10, 1);
                }
                if ((active0 & 0x1000L) != 0L) {
                    return this.jjStartNfaWithStates_0(2, 12, 1);
                }
                if ((active0 & 0x100000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 32, 1);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x40000000000L);
            }
            case 't': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 28, 1);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0x400000000000L);
            }
            case 'w': {
                if ((active0 & 0x200000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 57, 1);
            }
        }
        return this.jjStartNfa_0(1, active0, 0L);
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(1, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 'd': {
                if ((active0 & 0x4000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 14, 1);
            }
            case 'e': {
                if ((active0 & 0x400000000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 46, 1);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x2000000000000L);
            }
            case 'l': {
                if ((active0 & 0x1000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 48, 1);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x4000000000000L);
            }
            case 'r': {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 34, 1);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x800000000000L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x40000000000L);
            }
        }
        return this.jjStartNfa_0(2, active0, 0L);
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(2, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0x40000000000L);
            }
            case 'e': {
                if ((active0 & 0x800000000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(4, 47, 1);
            }
            case 's': {
                if ((active0 & 0x2000000000000L) == 0L) break;
                return this.jjStopAtPos(4, 49);
            }
            case 't': {
                if ((active0 & 0x4000000000000L) == 0L) break;
                return this.jjStopAtPos(4, 50);
            }
        }
        return this.jjStartNfa_0(3, active0, 0L);
    }

    private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(3, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 'n': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0x40000000000L);
            }
        }
        return this.jjStartNfa_0(4, active0, 0L);
    }

    private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(4, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0x40000000000L);
            }
        }
        return this.jjStartNfa_0(5, active0, 0L);
    }

    private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(5, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0x40000000000L);
            }
        }
        return this.jjStartNfa_0(6, active0, 0L);
    }

    private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(6, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0, 0L);
            return 8;
        }
        switch (this.curChar) {
            case 'o': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0x40000000000L);
            }
        }
        return this.jjStartNfa_0(7, active0, 0L);
    }

    private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(7, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0, 0L);
            return 9;
        }
        switch (this.curChar) {
            case 'f': {
                if ((active0 & 0x40000000000L) == 0L) break;
                return this.jjStartNfaWithStates_0(9, 42, 1);
            }
        }
        return this.jjStartNfa_0(8, active0, 0L);
    }

    private int jjStartNfaWithStates_0(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_0(state, pos + 1);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 27;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block41: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                this.jjCheckNAddStates(0, 5);
                            } else if (this.curChar == '.') {
                                this.jjCheckNAdd(9);
                            } else if (this.curChar == '$') {
                                if (kind > 64) {
                                    kind = 64;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if ((0x3FE000000000000L & l) != 0L) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                this.jjCheckNAddTwoStates(6, 7);
                                break;
                            }
                            if (this.curChar != '0') break;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 1: {
                            if ((0x3FF001000000000L & l) == 0L) continue block41;
                            if (kind > 64) {
                                kind = 64;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 3: {
                            if ((0x41000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 5: {
                            if ((0x3FE000000000000L & l) == 0L) continue block41;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddTwoStates(6, 7);
                            break;
                        }
                        case 6: {
                            if ((0x3FF000000000000L & l) == 0L) continue block41;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddTwoStates(6, 7);
                            break;
                        }
                        case 8: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & l) == 0L) continue block41;
                            if (kind > 81) {
                                kind = 81;
                            }
                            this.jjCheckNAddStates(9, 11);
                            break;
                        }
                        case 11: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(12);
                            break;
                        }
                        case 12: {
                            if ((0x3FF000000000000L & l) == 0L) continue block41;
                            if (kind > 81) {
                                kind = 81;
                            }
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(0, 5);
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != '.') continue block41;
                            if (kind > 81) {
                                kind = 81;
                            }
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) continue block41;
                            if (kind > 81) {
                                kind = 81;
                            }
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(18, 19);
                            break;
                        }
                        case 20: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(21);
                            break;
                        }
                        case 21: {
                            if ((0x3FF000000000000L & l) == 0L) continue block41;
                            if (kind > 81) {
                                kind = 81;
                            }
                            this.jjCheckNAddTwoStates(21, 13);
                            break;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(22, 13);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '0') continue block41;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddStates(6, 8);
                            break;
                        }
                        case 24: {
                            if ((0xFF000000000000L & l) == 0L) continue block41;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddTwoStates(24, 7);
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) continue block41;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddTwoStates(26, 7);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block42: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 64) {
                                    kind = 64;
                                }
                                this.jjCheckNAdd(1);
                                break;
                            }
                            if (this.curChar != '[') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block42;
                            if (kind > 64) {
                                kind = 64;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '[') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0x1000000040000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if (this.curChar != ']') break;
                            kind = 67;
                            break;
                        }
                        case 7: {
                            if ((0x110000001100L & l) == 0L || kind <= 80) continue block42;
                            kind = 80;
                            break;
                        }
                        case 10: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(15, 16);
                            break;
                        }
                        case 13: {
                            if ((0x5400000054L & l) == 0L || kind <= 81) continue block42;
                            kind = 81;
                            break;
                        }
                        case 19: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(17, 18);
                            break;
                        }
                        case 25: {
                            if ((0x100000001000000L & l) == 0L) break;
                            this.jjCheckNAdd(26);
                            break;
                        }
                        case 26: {
                            if ((0x7E0000007EL & l) == 0L) continue block42;
                            if (kind > 80) {
                                kind = 80;
                            }
                            this.jjCheckNAddTwoStates(26, 7);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block43: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if (!OgnlParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block43;
                            if (kind > 64) {
                                kind = 64;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            if (i == (startsAt = 27 - this.jjnewStateCnt)) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0, long active1) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '`': {
                return this.jjStopAtPos(0, 76);
            }
        }
        return this.jjMoveNfa_2(0, 0);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 6;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 75) break;
                            kind = 75;
                            break;
                        }
                        case 1: {
                            if ((0x8400000000L & l) == 0L || kind <= 74) continue block18;
                            kind = 74;
                            break;
                        }
                        case 2: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0xFF000000000000L & l) == 0L) continue block18;
                            if (kind > 74) {
                                kind = 74;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if ((0xFF000000000000L & l) == 0L || kind <= 74) continue block18;
                            kind = 74;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEEFFFFFFFL & l) != 0L) {
                                if (kind <= 75) break;
                                kind = 75;
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjAddStates(19, 21);
                            break;
                        }
                        case 1: {
                            if ((0x14404510000000L & l) == 0L || kind <= 74) continue block19;
                            kind = 74;
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFFEEFFFFFFFL & l) == 0L || kind <= 75) continue block19;
                            kind = 75;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!OgnlParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 75) continue block20;
                            kind = 75;
                            break;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            if (i == (startsAt = 6 - this.jjnewStateCnt)) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0, long active1) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '\'': {
                return this.jjStopAtPos(0, 73);
            }
        }
        return this.jjMoveNfa_1(0, 0);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 6;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L || kind <= 72) continue block18;
                            kind = 72;
                            break;
                        }
                        case 1: {
                            if ((0x8400000000L & l) == 0L || kind <= 71) continue block18;
                            kind = 71;
                            break;
                        }
                        case 2: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0xFF000000000000L & l) == 0L) continue block18;
                            if (kind > 71) {
                                kind = 71;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if ((0xFF000000000000L & l) == 0L || kind <= 71) continue block18;
                            kind = 71;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                if (kind <= 72) break;
                                kind = 72;
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjAddStates(19, 21);
                            break;
                        }
                        case 1: {
                            if ((0x14404510000000L & l) == 0L || kind <= 71) continue block19;
                            kind = 71;
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L || kind <= 72) continue block19;
                            kind = 72;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!OgnlParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 72) continue block20;
                            kind = 72;
                            break;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            if (i == (startsAt = 6 - this.jjnewStateCnt)) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_3(int pos, long active0, long active1) {
        return this.jjMoveNfa_3(this.jjStopStringLiteralDfa_3(pos, active0, active1), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '\"': {
                return this.jjStopAtPos(0, 79);
            }
        }
        return this.jjMoveNfa_3(0, 0);
    }

    private int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 6;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block18: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L || kind <= 78) continue block18;
                            kind = 78;
                            break;
                        }
                        case 1: {
                            if ((0x8400000000L & l) == 0L || kind <= 77) continue block18;
                            kind = 77;
                            break;
                        }
                        case 2: {
                            if ((0xF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 3: {
                            if ((0xFF000000000000L & l) == 0L) continue block18;
                            if (kind > 77) {
                                kind = 77;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if ((0xFF000000000000L & l) == 0L || kind <= 77) continue block18;
                            kind = 77;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block19: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                if (kind <= 78) break;
                                kind = 78;
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjAddStates(19, 21);
                            break;
                        }
                        case 1: {
                            if ((0x14404510000000L & l) == 0L || kind <= 77) continue block19;
                            kind = 77;
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L || kind <= 78) continue block19;
                            kind = 78;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1L << (hiByte & 0x3F);
                int i2 = (this.curChar & 0xFF) >> 6;
                long l2 = 1L << (this.curChar & 0x3F);
                block20: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!OgnlParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2) || kind <= 78) continue block20;
                            kind = 78;
                            break;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            i = this.jjnewStateCnt;
            this.jjnewStateCnt = startsAt;
            if (i == (startsAt = 6 - this.jjnewStateCnt)) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec3[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec4[i2] & l2) != 0L;
            }
            case 51: {
                return (jjbitVec5[i2] & l2) != 0L;
            }
            case 61: {
                return (jjbitVec6[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec8[i2] & l2) != 0L;
            }
        }
        return (jjbitVec7[i1] & l1) != 0L;
    }

    public OgnlParserTokenManager(JavaCharStream stream) {
        this.input_stream = stream;
    }

    public OgnlParserTokenManager(JavaCharStream stream, int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(JavaCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 27;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(JavaCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 4 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String tokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, tokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        int curPos = 0;
        block14: while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            this.image.setLength(0);
            this.jjimageLen = 0;
            while (true) {
                switch (this.curLexState) {
                    case 0: {
                        try {
                            this.input_stream.backup(0);
                            while (this.curChar <= ' ' && (0x100003600L & 1L << this.curChar) != 0L) {
                                this.curChar = this.input_stream.BeginToken();
                            }
                        }
                        catch (IOException e1) {
                            continue block14;
                        }
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        break;
                    }
                    case 2: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_2();
                        break;
                    }
                    case 3: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_3();
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block14;
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    if (jjnewLexState[this.jjmatchedKind] == -1) continue block14;
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                    continue block14;
                }
                this.MoreLexicalActions();
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }
                curPos = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;
                try {
                    this.curChar = this.input_stream.readChar();
                }
                catch (IOException e1) {
                    // empty catch block
                    break block14;
                }
            }
            break;
        }
        int error_line = this.input_stream.getEndLine();
        int error_column = this.input_stream.getEndColumn();
        String error_after = null;
        boolean EOFSeen = false;
        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        }
        catch (IOException e1) {
            EOFSeen = true;
            String string = error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
            if (this.curChar == '\n' || this.curChar == '\r') {
                ++error_line;
                error_column = 0;
            }
            ++error_column;
        }
        if (!EOFSeen) {
            this.input_stream.backup(1);
            error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
        }
        throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
    }

    void MoreLexicalActions() {
        this.lengthOfMatch = this.jjmatchedPos + 1;
        this.jjimageLen += this.lengthOfMatch;
        switch (this.jjmatchedKind) {
            case 69: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.stringBuffer = new StringBuffer();
                break;
            }
            case 70: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.stringBuffer = new StringBuffer();
                break;
            }
            case 71: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.charValue = this.escapeChar();
                this.stringBuffer.append(this.charValue);
                break;
            }
            case 72: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.charValue = this.image.charAt(this.image.length() - 1);
                this.stringBuffer.append(this.charValue);
                break;
            }
            case 74: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.charValue = this.escapeChar();
                break;
            }
            case 75: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.charValue = this.image.charAt(this.image.length() - 1);
                break;
            }
            case 77: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.stringBuffer.append(this.escapeChar());
                break;
            }
            case 78: {
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.stringBuffer.append(this.image.charAt(this.image.length() - 1));
                break;
            }
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 67: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                switch (this.image.charAt(1)) {
                    case '^': {
                        this.literalValue = DynamicSubscript.first;
                        break;
                    }
                    case '|': {
                        this.literalValue = DynamicSubscript.mid;
                        break;
                    }
                    case '$': {
                        this.literalValue = DynamicSubscript.last;
                        break;
                    }
                    case '*': {
                        this.literalValue = DynamicSubscript.all;
                    }
                }
                break;
            }
            case 73: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.stringBuffer.length() == 1) {
                    this.literalValue = new Character(this.charValue);
                    break;
                }
                this.literalValue = new String(this.stringBuffer);
                break;
            }
            case 76: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.literalValue = new Character(this.charValue);
                break;
            }
            case 79: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.literalValue = new String(this.stringBuffer);
                break;
            }
            case 80: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.literalValue = this.makeInt();
                break;
            }
            case 81: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.literalValue = this.makeFloat();
                break;
            }
        }
    }

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private void jjAddStates(int start, int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        do {
            this.jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }
}

