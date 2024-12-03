/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 */
package com.google.template.soy.exprparse;

import com.google.common.base.CharMatcher;
import com.google.template.soy.exprparse.ExpressionParserConstants;
import com.google.template.soy.exprparse.SimpleCharStream;
import com.google.template.soy.exprparse.Token;
import com.google.template.soy.exprparse.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class ExpressionParserTokenManager
implements ExpressionParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{71, 73, 74, 65, 67, 68, 42, 43, 44, 49, 50, 54, 56, 57, 59, 61, 62, 12, 13, 20, 21, 64, 70, 54, 56, 57, 59, 61, 62, 47, 48, 51, 52};
    public static final String[] jjstrLiteralImages = new String[]{"", "null", null, null, null, null, null, null, "-", "not", null, null, null, null, null, null, null, null, ",", "?:", "?", ":", "(", ")", "[", "]", "$ij.", "$ij?.", "?["};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    static final long[] jjtoToken = new long[]{536739647L};
    static final long[] jjtoSkip = new long[]{131072L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[75];
    private final int[] jjstateSet = new int[150];
    private final StringBuilder jjimage;
    private StringBuilder image = this.jjimage = new StringBuilder();
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x202L) != 0L) {
                    this.jjmatchedKind = 11;
                    return 34;
                }
                if ((active0 & 0xC000000L) != 0L) {
                    return 36;
                }
                if ((active0 & 0x10180000L) != 0L) {
                    return 64;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x202L) != 0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 1;
                    return 34;
                }
                if ((active0 & 0xC000000L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 1;
                    return 37;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x200L) != 0L) {
                    return 34;
                }
                if ((active0 & 0xC000000L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 2;
                    return 37;
                }
                if ((active0 & 2L) != 0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 2;
                    return 34;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 2L) != 0L) {
                    return 34;
                }
                if ((active0 & 0x8000000L) != 0L) {
                    if (this.jjmatchedPos < 2) {
                        this.jjmatchedKind = 12;
                        this.jjmatchedPos = 2;
                    }
                    return -1;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '$': {
                return this.jjMoveStringLiteralDfa1_0(0xC000000L);
            }
            case '(': {
                return this.jjStopAtPos(0, 22);
            }
            case ')': {
                return this.jjStopAtPos(0, 23);
            }
            case ',': {
                return this.jjStopAtPos(0, 18);
            }
            case '-': {
                return this.jjStopAtPos(0, 8);
            }
            case ':': {
                return this.jjStopAtPos(0, 21);
            }
            case '?': {
                this.jjmatchedKind = 20;
                return this.jjMoveStringLiteralDfa1_0(0x10080000L);
            }
            case '[': {
                return this.jjStopAtPos(0, 24);
            }
            case ']': {
                return this.jjStopAtPos(0, 25);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(514L);
            }
        }
        return this.jjMoveNfa_0(3, 0);
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case ':': {
                if ((active0 & 0x80000L) == 0L) break;
                return this.jjStopAtPos(1, 19);
            }
            case '[': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStopAtPos(1, 28);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0xC000000L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 512L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 'j': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0xC000000L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2L);
            }
            case 't': {
                if ((active0 & 0x200L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 9, 34);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case '.': {
                if ((active0 & 0x4000000L) == 0L) break;
                return this.jjStopAtPos(3, 26);
            }
            case '?': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0x8000000L);
            }
            case 'l': {
                if ((active0 & 2L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 1, 34);
            }
        }
        return this.jjStartNfa_0(2, active0);
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case '.': {
                if ((active0 & 0x8000000L) == 0L) break;
                return this.jjStopAtPos(4, 27);
            }
        }
        return this.jjStartNfa_0(3, active0);
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
        this.jjnewStateCnt = 75;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block93: do {
                    switch (this.jjstateSet[--i]) {
                        case 64: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 3: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 3) {
                                    kind = 3;
                                }
                                this.jjCheckNAddStates(6, 10);
                            } else if ((0x50008C2000000000L & l) != 0L) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                            } else if (this.curChar == '.') {
                                this.jjCheckNAddStates(11, 16);
                            } else if (this.curChar == '$') {
                                this.jjstateSet[this.jjnewStateCnt++] = 36;
                            } else if (this.curChar == '!') {
                                this.jjCheckNAdd(23);
                            } else if (this.curChar == '=') {
                                this.jjCheckNAdd(23);
                            } else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(17, 20);
                            } else if (this.curChar == '?') {
                                this.jjAddStates(21, 22);
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 39;
                                break;
                            }
                            if (this.curChar == '>') {
                                this.jjCheckNAdd(23);
                                break;
                            }
                            if (this.curChar == '<') {
                                this.jjCheckNAdd(23);
                                break;
                            }
                            if (this.curChar != '0') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 10: {
                            if (this.curChar != '0') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 11: {
                            if (this.curChar != '\'') break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFF7FFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 14: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 21: {
                            if (this.curChar != '\'' || kind <= 5) continue block93;
                            kind = 5;
                            break;
                        }
                        case 22: {
                            if ((0x50008C2000000000L & l) == 0L || kind <= 10) continue block93;
                            kind = 10;
                            break;
                        }
                        case 23: {
                            if (this.curChar != '=' || kind <= 10) continue block93;
                            kind = 10;
                            break;
                        }
                        case 24: {
                            if (this.curChar != '<') break;
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '>') break;
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 26: {
                            if (this.curChar != '=') break;
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 27: {
                            if (this.curChar != '!') break;
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 35: {
                            if (this.curChar != '$') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 37: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 38: {
                            if ((0x100002600L & l) == 0L || kind <= 17) continue block93;
                            kind = 17;
                            break;
                        }
                        case 39: {
                            if (this.curChar != '\n' || kind <= 17) continue block93;
                            kind = 17;
                            break;
                        }
                        case 40: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 39;
                            break;
                        }
                        case 41: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(6, 10);
                            break;
                        }
                        case 42: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAdd(42);
                            break;
                        }
                        case 43: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(43, 44);
                            break;
                        }
                        case 44: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(45);
                            break;
                        }
                        case 45: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjCheckNAddTwoStates(45, 46);
                            break;
                        }
                        case 47: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(48);
                            break;
                        }
                        case 48: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjCheckNAdd(48);
                            break;
                        }
                        case 49: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(49, 50);
                            break;
                        }
                        case 51: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 52: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjCheckNAdd(52);
                            break;
                        }
                        case 53: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(11, 16);
                            break;
                        }
                        case 54: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 55: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 56: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 58: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 59: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 60: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 61: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 60;
                            break;
                        }
                        case 62: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAdd(62);
                            break;
                        }
                        case 63: {
                            if (this.curChar != '?') break;
                            this.jjAddStates(21, 22);
                            break;
                        }
                        case 65: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 66: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 67: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 69: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 14) {
                                kind = 14;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 70: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 71: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 72: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 73: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 72;
                            break;
                        }
                        case 74: {
                            if ((0x3FF000000000000L & l) == 0L) continue block93;
                            if (kind > 16) {
                                kind = 16;
                            }
                            this.jjCheckNAdd(74);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block94: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                                this.jjCheckNAdd(34);
                            }
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 31;
                                break;
                            }
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
                                break;
                            }
                            if (this.curChar == 'f') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                break;
                            }
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 0: {
                            if (this.curChar != 'e' || kind <= 2) continue block94;
                            kind = 2;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 'u') break;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 2: {
                            if (this.curChar != 'r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 4: {
                            if (this.curChar != 's') break;
                            this.jjCheckNAdd(0);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'f') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'x') break;
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 9: {
                            if ((0x7EL & l) == 0L) continue block94;
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 14: {
                            if ((0x14404410000000L & l) == 0L) break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'u') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 16: {
                            if ((0x7EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 17: {
                            if ((0x7EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0x7EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: {
                            if ((0x7EL & l) == 0L) break;
                            this.jjCheckNAddStates(17, 20);
                            break;
                        }
                        case 20: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'd' || kind <= 10) continue block94;
                            kind = 10;
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 31: {
                            if (this.curChar != 'r' || kind <= 10) continue block94;
                            kind = 10;
                            break;
                        }
                        case 32: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 33: 
                        case 34: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block94;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(34);
                            break;
                        }
                        case 36: 
                        case 37: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block94;
                            if (kind > 12) {
                                kind = 12;
                            }
                            this.jjCheckNAdd(37);
                            break;
                        }
                        case 46: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(29, 30);
                            break;
                        }
                        case 50: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(31, 32);
                            break;
                        }
                        case 57: 
                        case 58: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block94;
                            if (kind > 13) {
                                kind = 13;
                            }
                            this.jjCheckNAdd(58);
                            break;
                        }
                        case 68: 
                        case 69: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block94;
                            if (kind > 14) {
                                kind = 14;
                            }
                            this.jjCheckNAdd(69);
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
                do {
                    switch (this.jjstateSet[--i]) {
                        case 12: {
                            if (!ExpressionParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(17, 20);
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
            if (i == (startsAt = 75 - this.jjnewStateCnt)) {
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
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    public ExpressionParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ExpressionParserTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(SimpleCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 75;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    public Token getNextToken() {
        int curPos;
        block7: {
            curPos = 0;
            do {
                try {
                    this.curChar = this.input_stream.BeginToken();
                }
                catch (IOException e) {
                    this.jjmatchedKind = 0;
                    Token matchedToken = this.jjFillToken();
                    return matchedToken;
                }
                this.image = this.jjimage;
                this.image.setLength(0);
                this.jjimageLen = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_0();
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block7;
                if (this.jjmatchedPos + 1 >= curPos) continue;
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            } while ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0L);
            Token matchedToken = this.jjFillToken();
            this.TokenLexicalActions(matchedToken);
            return matchedToken;
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

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 13: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                matchedToken.image = CharMatcher.whitespace().removeFrom((CharSequence)this.image.toString());
                break;
            }
            case 14: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                matchedToken.image = CharMatcher.whitespace().removeFrom((CharSequence)this.image.toString());
                break;
            }
            case 15: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                matchedToken.image = CharMatcher.whitespace().removeFrom((CharSequence)this.image.toString());
                break;
            }
            case 16: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                matchedToken.image = CharMatcher.whitespace().removeFrom((CharSequence)this.image.toString());
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

