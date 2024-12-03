/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.classic;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.lucene.queryparser.classic.CharStream;
import org.apache.lucene.queryparser.classic.QueryParserConstants;
import org.apache.lucene.queryparser.classic.Token;
import org.apache.lucene.queryparser.classic.TokenMgrError;

public class QueryParserTokenManager
implements QueryParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{1L, 0L, 0L, 0L};
    static final long[] jjbitVec1 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec3 = new long[]{0L, 0L, -1L, -1L};
    static final long[] jjbitVec4 = new long[]{-281474976710658L, -1L, -1L, -1L};
    static final int[] jjnextStates = new int[]{37, 39, 40, 17, 18, 20, 42, 45, 31, 46, 43, 22, 23, 25, 26, 24, 25, 26, 45, 31, 46, 44, 47, 35, 22, 28, 29, 27, 27, 30, 30, 0, 1, 2, 4, 5};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, "+", "-", null, "(", ")", ":", "*", "^", null, null, null, null, null, null, "[", "{", null, "TO", "]", "}", null, null};
    public static final String[] lexStateNames = new String[]{"Boost", "Range", "DEFAULT"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, 1, 1, 2, -1, 2, 2, -1, -1};
    static final long[] jjtoToken = new long[]{0x1FFFFFF01L};
    static final long[] jjtoSkip = new long[]{128L};
    protected CharStream input_stream;
    private final int[] jjrounds = new int[49];
    private final int[] jjstateSet = new int[98];
    protected char curChar;
    int curLexState = 2;
    int defaultLexState = 2;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '(': {
                return this.jjStopAtPos(0, 14);
            }
            case ')': {
                return this.jjStopAtPos(0, 15);
            }
            case '*': {
                return this.jjStartNfaWithStates_2(0, 17, 49);
            }
            case '+': {
                return this.jjStartNfaWithStates_2(0, 11, 15);
            }
            case '-': {
                return this.jjStartNfaWithStates_2(0, 12, 15);
            }
            case ':': {
                return this.jjStopAtPos(0, 16);
            }
            case '[': {
                return this.jjStopAtPos(0, 25);
            }
            case '^': {
                return this.jjStopAtPos(0, 18);
            }
            case '{': {
                return this.jjStopAtPos(0, 26);
            }
        }
        return this.jjMoveNfa_2(0, 0);
    }

    private int jjStartNfaWithStates_2(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_2(state, pos + 1);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 49;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block90: do {
                    switch (this.jjstateSet[--i]) {
                        case 33: 
                        case 49: {
                            if ((0xFBFF7CF8FFFFD9FFL & l) == 0L) continue block90;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 0: {
                            if ((0xFBFF54F8FFFFD9FFL & l) != 0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 7) {
                                    kind = 7;
                                }
                            } else if ((0x280200000000L & l) != 0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                            } else if (this.curChar == '/') {
                                this.jjCheckNAddStates(0, 2);
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
                            }
                            if ((0x7BFF50F8FFFFD9FFL & l) != 0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(6, 10);
                            } else if (this.curChar == '*') {
                                if (kind > 22) {
                                    kind = 22;
                                }
                            } else if (this.curChar == '!' && kind > 10) {
                                kind = 10;
                            }
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '&' || kind <= 8) continue block90;
                            kind = 8;
                            break;
                        }
                        case 5: {
                            if (this.curChar != '&') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 13: {
                            if (this.curChar != '!' || kind <= 10) continue block90;
                            kind = 10;
                            break;
                        }
                        case 14: {
                            if ((0x280200000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 15: {
                            if ((0x100002600L & l) == 0L || kind <= 13) continue block90;
                            kind = 13;
                            break;
                        }
                        case 16: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 17: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 19: {
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 20: {
                            if (this.curChar != '\"' || kind <= 19) continue block90;
                            kind = 19;
                            break;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0L) continue block90;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddStates(11, 14);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(24);
                            break;
                        }
                        case 24: {
                            if ((0x3FF000000000000L & l) == 0L) continue block90;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddStates(15, 17);
                            break;
                        }
                        case 25: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0L) continue block90;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 27: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 28: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0L) continue block90;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 30: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 31: {
                            if (this.curChar != '*' || kind <= 22) continue block90;
                            kind = 22;
                            break;
                        }
                        case 32: {
                            if ((0xFBFF54F8FFFFD9FFL & l) == 0L) continue block90;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 35: {
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 36: 
                        case 38: {
                            if (this.curChar != '/') break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 37: {
                            if ((0xFFFF7FFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 40: {
                            if (this.curChar != '/' || kind <= 24) continue block90;
                            kind = 24;
                            break;
                        }
                        case 41: {
                            if ((0x7BFF50F8FFFFD9FFL & l) == 0L) continue block90;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            break;
                        }
                        case 42: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0L) continue block90;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 44: {
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 45: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0L) break;
                            this.jjCheckNAddStates(18, 20);
                            break;
                        }
                        case 47: {
                            this.jjCheckNAddStates(18, 20);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block91: do {
                    switch (this.jjstateSet[--i]) {
                        case 49: {
                            if ((0x97FFFFFF87FFFFFFL & l) != 0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(35, 35);
                            break;
                        }
                        case 0: {
                            if ((0x97FFFFFF87FFFFFFL & l) != 0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(6, 10);
                            } else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(21, 23);
                            } else if (this.curChar == '~') {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAddStates(24, 26);
                            }
                            if ((0x97FFFFFF87FFFFFFL & l) != 0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                break;
                            }
                            if (this.curChar == '|') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                break;
                            }
                            if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                break;
                            }
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 'D' || kind <= 8) continue block91;
                            kind = 8;
                            break;
                        }
                        case 2: {
                            if (this.curChar != 'N') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'R' || kind <= 9) continue block91;
                            kind = 9;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'O') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '|' || kind <= 9) continue block91;
                            kind = 9;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '|') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'T' || kind <= 10) continue block91;
                            kind = 10;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 'O') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'N') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 17: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: {
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 21: {
                            if (this.curChar != '~') continue block91;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddStates(24, 26);
                            break;
                        }
                        case 25: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) continue block91;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 26: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(27, 28);
                            break;
                        }
                        case 27: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 28: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) continue block91;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(29, 30);
                            break;
                        }
                        case 30: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 32: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) continue block91;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 33: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) continue block91;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 34: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(35, 35);
                            break;
                        }
                        case 35: {
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 37: {
                            this.jjAddStates(0, 2);
                            break;
                        }
                        case 39: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 41: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) continue block91;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            break;
                        }
                        case 42: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) continue block91;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 43: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(44, 44);
                            break;
                        }
                        case 44: {
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 45: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(18, 20);
                            break;
                        }
                        case 46: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddTwoStates(47, 47);
                            break;
                        }
                        case 47: {
                            this.jjCheckNAddStates(18, 20);
                            break;
                        }
                        case 48: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(21, 23);
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
                block92: do {
                    switch (this.jjstateSet[--i]) {
                        case 33: 
                        case 49: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 0: {
                            if (QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 7) {
                                kind = 7;
                            }
                            if (QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            break;
                        }
                        case 15: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 13) continue block92;
                            kind = 13;
                            break;
                        }
                        case 17: 
                        case 19: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(3, 5);
                            break;
                        }
                        case 25: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 27: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            break;
                        }
                        case 28: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 30: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 32: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 35: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            break;
                        }
                        case 37: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(0, 2);
                            break;
                        }
                        case 41: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            break;
                        }
                        case 42: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 44: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block92;
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            break;
                        }
                        case 45: {
                            if (!QueryParserTokenManager.jjCanMove_2(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(18, 20);
                            break;
                        }
                        case 47: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(18, 20);
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
            if (i == (startsAt = 49 - this.jjnewStateCnt)) {
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

    private int jjMoveStringLiteralDfa0_0() {
        return this.jjMoveNfa_0(0, 0);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block12: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) == 0L) continue block12;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjAddStates(31, 32);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0L) continue block12;
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(2);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        default: 
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
                        default: 
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
            if (i == (startsAt = 3 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x10000000L) != 0L) {
                    this.jjmatchedKind = 32;
                    return 6;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case 'T': {
                return this.jjMoveStringLiteralDfa1_1(0x10000000L);
            }
            case ']': {
                return this.jjStopAtPos(0, 29);
            }
            case '}': {
                return this.jjStopAtPos(0, 30);
            }
        }
        return this.jjMoveNfa_1(0, 0);
    }

    private int jjMoveStringLiteralDfa1_1(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case 'O': {
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 28, 6);
            }
        }
        return this.jjStartNfa_1(0, active0);
    }

    private int jjStartNfaWithStates_1(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_1(state, pos + 1);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 7;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block21: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) != 0L) {
                                if (kind > 32) {
                                    kind = 32;
                                }
                                this.jjCheckNAdd(6);
                            }
                            if ((0x100002600L & l) != 0L) {
                                if (kind <= 7) break;
                                kind = 7;
                                break;
                            }
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(2, 4);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddTwoStates(2, 4);
                            break;
                        }
                        case 2: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(33, 35);
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(33, 35);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '\"' || kind <= 31) continue block21;
                            kind = 31;
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0L) continue block21;
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block22: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 6: {
                            if ((0xDFFFFFFFDFFFFFFFL & l) == 0L) continue block22;
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                        case 2: {
                            this.jjAddStates(33, 35);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
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
                block23: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 7) {
                                kind = 7;
                            }
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
                            break;
                        }
                        case 2: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(33, 35);
                            break;
                        }
                        case 6: {
                            if (!QueryParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block23;
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
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
            if (i == (startsAt = 7 - this.jjnewStateCnt)) {
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
            case 48: {
                return (jjbitVec0[i2] & l2) != 0L;
            }
        }
        return false;
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec3[i2] & l2) != 0L;
            }
        }
        return (jjbitVec1[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec3[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec1[i2] & l2) != 0L;
            }
        }
        return (jjbitVec4[i1] & l1) != 0L;
    }

    public QueryParserTokenManager(CharStream stream) {
        this.input_stream = stream;
    }

    public QueryParserTokenManager(CharStream stream, int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }

    public void ReInit(CharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 49;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(CharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 3 || lexState < 0) {
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
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            switch (this.curLexState) {
                case 0: {
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
                }
            }
            if (this.jjmatchedKind == Integer.MAX_VALUE) break;
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                Token matchedToken = this.jjFillToken();
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }
                return matchedToken;
            }
            if (jjnewLexState[this.jjmatchedKind] == -1) continue;
            this.curLexState = jjnewLexState[this.jjmatchedKind];
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

