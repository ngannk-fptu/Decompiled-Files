/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.el.parser.ELParserConstants;
import org.apache.el.parser.SimpleCharStream;
import org.apache.el.parser.Token;
import org.apache.el.parser.TokenMgrError;

public class ELParserTokenManager
implements ELParserConstants {
    Deque<Integer> deque = new ArrayDeque<Integer>();
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final long[] jjbitVec3 = new long[]{2301339413881290750L, -16384L, 0xFFFFFFFFL, 0x600000000000000L};
    static final long[] jjbitVec4 = new long[]{0L, 0L, 0L, -36028797027352577L};
    static final long[] jjbitVec5 = new long[]{0L, -1L, -1L, -1L};
    static final long[] jjbitVec6 = new long[]{-1L, -1L, 65535L, 0L};
    static final long[] jjbitVec7 = new long[]{-1L, -1L, 0L, 0L};
    static final long[] jjbitVec8 = new long[]{0x3FFFFFFFFFFFL, 0L, 0L, 0L};
    public static final String[] jjstrLiteralImages = new String[]{"", null, "${", "#{", null, null, null, null, "{", "}", null, null, null, null, "true", "false", "null", ".", "(", ")", "[", "]", ":", ";", ",", ">", "gt", "<", "lt", ">=", "ge", "<=", "le", "==", "eq", "!=", "ne", "!", "not", "&&", "and", "||", "or", "empty", "instanceof", "*", "+", "-", "?", "/", "div", "%", "mod", "+=", "=", "->", null, null, null, null, null, null};
    static final int[] jjnextStates = new int[]{0, 1, 3, 4, 2, 0, 1, 4, 2, 0, 1, 4, 5, 2, 0, 1, 2, 6, 16, 17, 18, 23, 24, 11, 12, 14, 6, 7, 9, 3, 4, 21, 22, 25, 26};
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    public static final String[] lexStateNames = new String[]{"DEFAULT", "IN_EXPRESSION", "IN_SET_OR_MAP"};
    public static final int[] jjnewLexState = new int[]{-1, -1, 1, 1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{2594073385365401359L};
    static final long[] jjtoSkip = new long[]{240L};
    static final long[] jjtoSpecial = new long[]{0L};
    static final long[] jjtoMore = new long[]{0L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[30];
    private final int[] jjstateSet = new int[60];
    private final StringBuilder jjimage;
    private StringBuilder image = this.jjimage = new StringBuilder();
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0xCL) != 0L) {
                    this.jjmatchedKind = 1;
                    return 5;
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
            case 35: {
                return this.jjMoveStringLiteralDfa1_0(8L);
            }
            case 36: {
                return this.jjMoveStringLiteralDfa1_0(4L);
            }
        }
        return this.jjMoveNfa_0(7, 0);
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
            case 123: {
                if ((active0 & 4L) != 0L) {
                    return this.jjStopAtPos(1, 2);
                }
                if ((active0 & 8L) == 0L) break;
                return this.jjStopAtPos(1, 3);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 8;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < 64) {
                long l = 1L << this.curChar;
                block27: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) != 0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddStates(0, 4);
                            } else if ((0x1800000000L & l) != 0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(5);
                            }
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 2: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) continue block27;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 3: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 4: {
                            if ((0x1800000000L & l) == 0L) break;
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFE7FFFFFFFFL & l) == 0L) continue block27;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(5, 8);
                            break;
                        }
                        case 6: {
                            if ((0x1800000000L & l) == 0L) continue block27;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(9, 13);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 0x3F);
                block28: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: {
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(0, 4);
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddTwoStates(0, 1);
                                break;
                            }
                            if (this.curChar != 92) break;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != 92) continue block28;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(14, 17);
                            break;
                        }
                        case 2: {
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 3: {
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 5: {
                            if ((0xF7FFFFFFEFFFFFFFL & l) == 0L) continue block28;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(5, 8);
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
                block29: do {
                    switch (this.jjstateSet[--i]) {
                        case 7: {
                            if (ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) {
                                this.jjCheckNAddTwoStates(0, 1);
                            }
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block29;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 0: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block29;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 2: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block29;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 3: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block29;
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                        case 5: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block29;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddStates(5, 8);
                            break;
                        }
                        default: {
                            if (i1 != 0 && l1 != 0L && i2 != 0 && l2 != 0L) continue block29;
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
            if (i == (startsAt = 8 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_2(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x20000L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x141D555401C000L) != 0L) {
                    this.jjmatchedKind = 56;
                    return 30;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x41554000000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x1419400001C000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 30;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x14014000000000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x18000001C000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 2;
                    return 30;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x14000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x180000008000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 3;
                    return 30;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x80000008000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 4;
                    return 30;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 5;
                    return 30;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 6;
                    return 30;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 7;
                    return 30;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 8;
                    return 30;
                }
                return -1;
            }
        }
        return -1;
    }

    private final int jjStartNfa_2(int pos, long active0) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }

    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case 33: {
                this.jjmatchedKind = 37;
                return this.jjMoveStringLiteralDfa1_2(0x800000000L);
            }
            case 37: {
                return this.jjStopAtPos(0, 51);
            }
            case 38: {
                return this.jjMoveStringLiteralDfa1_2(0x8000000000L);
            }
            case 40: {
                return this.jjStopAtPos(0, 18);
            }
            case 41: {
                return this.jjStopAtPos(0, 19);
            }
            case 42: {
                return this.jjStopAtPos(0, 45);
            }
            case 43: {
                this.jjmatchedKind = 46;
                return this.jjMoveStringLiteralDfa1_2(0x20000000000000L);
            }
            case 44: {
                return this.jjStopAtPos(0, 24);
            }
            case 45: {
                this.jjmatchedKind = 47;
                return this.jjMoveStringLiteralDfa1_2(0x80000000000000L);
            }
            case 46: {
                return this.jjStartNfaWithStates_2(0, 17, 1);
            }
            case 47: {
                return this.jjStopAtPos(0, 49);
            }
            case 58: {
                return this.jjStopAtPos(0, 22);
            }
            case 59: {
                return this.jjStopAtPos(0, 23);
            }
            case 60: {
                this.jjmatchedKind = 27;
                return this.jjMoveStringLiteralDfa1_2(0x80000000L);
            }
            case 61: {
                this.jjmatchedKind = 54;
                return this.jjMoveStringLiteralDfa1_2(0x200000000L);
            }
            case 62: {
                this.jjmatchedKind = 25;
                return this.jjMoveStringLiteralDfa1_2(0x20000000L);
            }
            case 63: {
                return this.jjStopAtPos(0, 48);
            }
            case 91: {
                return this.jjStopAtPos(0, 20);
            }
            case 93: {
                return this.jjStopAtPos(0, 21);
            }
            case 97: {
                return this.jjMoveStringLiteralDfa1_2(0x10000000000L);
            }
            case 100: {
                return this.jjMoveStringLiteralDfa1_2(0x4000000000000L);
            }
            case 101: {
                return this.jjMoveStringLiteralDfa1_2(0x80400000000L);
            }
            case 102: {
                return this.jjMoveStringLiteralDfa1_2(32768L);
            }
            case 103: {
                return this.jjMoveStringLiteralDfa1_2(0x44000000L);
            }
            case 105: {
                return this.jjMoveStringLiteralDfa1_2(0x100000000000L);
            }
            case 108: {
                return this.jjMoveStringLiteralDfa1_2(0x110000000L);
            }
            case 109: {
                return this.jjMoveStringLiteralDfa1_2(0x10000000000000L);
            }
            case 110: {
                return this.jjMoveStringLiteralDfa1_2(0x5000010000L);
            }
            case 111: {
                return this.jjMoveStringLiteralDfa1_2(0x40000000000L);
            }
            case 116: {
                return this.jjMoveStringLiteralDfa1_2(16384L);
            }
            case 123: {
                return this.jjStopAtPos(0, 8);
            }
            case 124: {
                return this.jjMoveStringLiteralDfa1_2(0x20000000000L);
            }
            case 125: {
                return this.jjStopAtPos(0, 9);
            }
        }
        return this.jjMoveNfa_2(0, 0);
    }

    private int jjMoveStringLiteralDfa1_2(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case 38: {
                if ((active0 & 0x8000000000L) == 0L) break;
                return this.jjStopAtPos(1, 39);
            }
            case 61: {
                if ((active0 & 0x20000000L) != 0L) {
                    return this.jjStopAtPos(1, 29);
                }
                if ((active0 & 0x80000000L) != 0L) {
                    return this.jjStopAtPos(1, 31);
                }
                if ((active0 & 0x200000000L) != 0L) {
                    return this.jjStopAtPos(1, 33);
                }
                if ((active0 & 0x800000000L) != 0L) {
                    return this.jjStopAtPos(1, 35);
                }
                if ((active0 & 0x20000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 53);
            }
            case 62: {
                if ((active0 & 0x80000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 55);
            }
            case 97: {
                return this.jjMoveStringLiteralDfa2_2(active0, 32768L);
            }
            case 101: {
                if ((active0 & 0x40000000L) != 0L) {
                    return this.jjStartNfaWithStates_2(1, 30, 30);
                }
                if ((active0 & 0x100000000L) != 0L) {
                    return this.jjStartNfaWithStates_2(1, 32, 30);
                }
                if ((active0 & 0x1000000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(1, 36, 30);
            }
            case 105: {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x4000000000000L);
            }
            case 109: {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x80000000000L);
            }
            case 110: {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x110000000000L);
            }
            case 111: {
                return this.jjMoveStringLiteralDfa2_2(active0, 0x10004000000000L);
            }
            case 113: {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(1, 34, 30);
            }
            case 114: {
                if ((active0 & 0x40000000000L) != 0L) {
                    return this.jjStartNfaWithStates_2(1, 42, 30);
                }
                return this.jjMoveStringLiteralDfa2_2(active0, 16384L);
            }
            case 116: {
                if ((active0 & 0x4000000L) != 0L) {
                    return this.jjStartNfaWithStates_2(1, 26, 30);
                }
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(1, 28, 30);
            }
            case 117: {
                return this.jjMoveStringLiteralDfa2_2(active0, 65536L);
            }
            case 124: {
                if ((active0 & 0x20000000000L) == 0L) break;
                return this.jjStopAtPos(1, 41);
            }
        }
        return this.jjStartNfa_2(0, active0);
    }

    private int jjMoveStringLiteralDfa2_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 100: {
                if ((active0 & 0x10000000000L) != 0L) {
                    return this.jjStartNfaWithStates_2(2, 40, 30);
                }
                if ((active0 & 0x10000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(2, 52, 30);
            }
            case 108: {
                return this.jjMoveStringLiteralDfa3_2(active0, 98304L);
            }
            case 112: {
                return this.jjMoveStringLiteralDfa3_2(active0, 0x80000000000L);
            }
            case 115: {
                return this.jjMoveStringLiteralDfa3_2(active0, 0x100000000000L);
            }
            case 116: {
                if ((active0 & 0x4000000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(2, 38, 30);
            }
            case 117: {
                return this.jjMoveStringLiteralDfa3_2(active0, 16384L);
            }
            case 118: {
                if ((active0 & 0x4000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(2, 50, 30);
            }
        }
        return this.jjStartNfa_2(1, active0);
    }

    private int jjMoveStringLiteralDfa3_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 101: {
                if ((active0 & 0x4000L) == 0L) break;
                return this.jjStartNfaWithStates_2(3, 14, 30);
            }
            case 108: {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_2(3, 16, 30);
            }
            case 115: {
                return this.jjMoveStringLiteralDfa4_2(active0, 32768L);
            }
            case 116: {
                return this.jjMoveStringLiteralDfa4_2(active0, 0x180000000000L);
            }
        }
        return this.jjStartNfa_2(2, active0);
    }

    private int jjMoveStringLiteralDfa4_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 97: {
                return this.jjMoveStringLiteralDfa5_2(active0, 0x100000000000L);
            }
            case 101: {
                if ((active0 & 0x8000L) == 0L) break;
                return this.jjStartNfaWithStates_2(4, 15, 30);
            }
            case 121: {
                if ((active0 & 0x80000000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(4, 43, 30);
            }
        }
        return this.jjStartNfa_2(3, active0);
    }

    private int jjMoveStringLiteralDfa5_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 110: {
                return this.jjMoveStringLiteralDfa6_2(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_2(4, active0);
    }

    private int jjMoveStringLiteralDfa6_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 99: {
                return this.jjMoveStringLiteralDfa7_2(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_2(5, active0);
    }

    private int jjMoveStringLiteralDfa7_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 101: {
                return this.jjMoveStringLiteralDfa8_2(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_2(6, active0);
    }

    private int jjMoveStringLiteralDfa8_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 111: {
                return this.jjMoveStringLiteralDfa9_2(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_2(7, active0);
    }

    private int jjMoveStringLiteralDfa9_2(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_2(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_2(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 102: {
                if ((active0 & 0x100000000000L) == 0L) break;
                return this.jjStartNfaWithStates_2(9, 44, 30);
            }
        }
        return this.jjStartNfa_2(8, active0);
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
        this.jjnewStateCnt = 30;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < 64) {
                long l = 1L << this.curChar;
                block54: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                this.jjCheckNAddStates(18, 22);
                                break;
                            }
                            if ((0x1800000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(23, 25);
                                break;
                            }
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(26, 28);
                                break;
                            }
                            if (this.curChar != 46) break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 30: {
                            if ((0x3FF001000000000L & l) != 0L) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                this.jjCheckNAdd(29);
                            }
                            if ((0x3FF001000000000L & l) == 0L) break;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 3: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 34) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 8: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 9: {
                            if (this.curChar != 34 || kind <= 13) continue block54;
                            kind = 13;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 39) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 13: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 14: {
                            if (this.curChar != 39 || kind <= 13) continue block54;
                            kind = 13;
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjCheckNAddStates(18, 22);
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjCheckNAdd(16);
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(17, 18);
                            break;
                        }
                        case 18: {
                            if (this.curChar != 46) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            break;
                        }
                        case 21: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(22);
                            break;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(22);
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(23, 24);
                            break;
                        }
                        case 25: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(26);
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(26);
                            break;
                        }
                        case 27: {
                            if ((0x1800000000L & l) == 0L) continue block54;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 28: {
                            if ((0x3FF001000000000L & l) == 0L) continue block54;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 29: {
                            if ((0x3FF001000000000L & l) == 0L) continue block54;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 0x3F);
                block55: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block55;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 30: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                this.jjCheckNAdd(29);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 2: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(29, 30);
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 7: {
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 92) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 12: {
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 92) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 20: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(31, 32);
                            break;
                        }
                        case 24: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(33, 34);
                            break;
                        }
                        case 28: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block55;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 29: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block55;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
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
                block56: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 30: {
                            if (ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAdd(28);
                            }
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
                            break;
                        }
                        case 6: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block56;
                            this.jjAddStates(26, 28);
                            break;
                        }
                        case 11: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block56;
                            this.jjAddStates(23, 25);
                            break;
                        }
                        case 28: {
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 29: {
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
                            break;
                        }
                        default: {
                            if (i1 != 0 && l1 != 0L && i2 != 0 && l2 != 0L) continue block56;
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
            if (i == (startsAt = 30 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x20000L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x141D555401C000L) != 0L) {
                    this.jjmatchedKind = 56;
                    return 30;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x41554000000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x1419400001C000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 1;
                    return 30;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x14014000000000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x18000001C000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 2;
                    return 30;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x14000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x180000008000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 3;
                    return 30;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x80000008000L) != 0L) {
                    return 30;
                }
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 4;
                    return 30;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 5;
                    return 30;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 6;
                    return 30;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 7;
                    return 30;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x100000000000L) != 0L) {
                    this.jjmatchedKind = 56;
                    this.jjmatchedPos = 8;
                    return 30;
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
            case 33: {
                this.jjmatchedKind = 37;
                return this.jjMoveStringLiteralDfa1_1(0x800000000L);
            }
            case 37: {
                return this.jjStopAtPos(0, 51);
            }
            case 38: {
                return this.jjMoveStringLiteralDfa1_1(0x8000000000L);
            }
            case 40: {
                return this.jjStopAtPos(0, 18);
            }
            case 41: {
                return this.jjStopAtPos(0, 19);
            }
            case 42: {
                return this.jjStopAtPos(0, 45);
            }
            case 43: {
                this.jjmatchedKind = 46;
                return this.jjMoveStringLiteralDfa1_1(0x20000000000000L);
            }
            case 44: {
                return this.jjStopAtPos(0, 24);
            }
            case 45: {
                this.jjmatchedKind = 47;
                return this.jjMoveStringLiteralDfa1_1(0x80000000000000L);
            }
            case 46: {
                return this.jjStartNfaWithStates_1(0, 17, 1);
            }
            case 47: {
                return this.jjStopAtPos(0, 49);
            }
            case 58: {
                return this.jjStopAtPos(0, 22);
            }
            case 59: {
                return this.jjStopAtPos(0, 23);
            }
            case 60: {
                this.jjmatchedKind = 27;
                return this.jjMoveStringLiteralDfa1_1(0x80000000L);
            }
            case 61: {
                this.jjmatchedKind = 54;
                return this.jjMoveStringLiteralDfa1_1(0x200000000L);
            }
            case 62: {
                this.jjmatchedKind = 25;
                return this.jjMoveStringLiteralDfa1_1(0x20000000L);
            }
            case 63: {
                return this.jjStopAtPos(0, 48);
            }
            case 91: {
                return this.jjStopAtPos(0, 20);
            }
            case 93: {
                return this.jjStopAtPos(0, 21);
            }
            case 97: {
                return this.jjMoveStringLiteralDfa1_1(0x10000000000L);
            }
            case 100: {
                return this.jjMoveStringLiteralDfa1_1(0x4000000000000L);
            }
            case 101: {
                return this.jjMoveStringLiteralDfa1_1(0x80400000000L);
            }
            case 102: {
                return this.jjMoveStringLiteralDfa1_1(32768L);
            }
            case 103: {
                return this.jjMoveStringLiteralDfa1_1(0x44000000L);
            }
            case 105: {
                return this.jjMoveStringLiteralDfa1_1(0x100000000000L);
            }
            case 108: {
                return this.jjMoveStringLiteralDfa1_1(0x110000000L);
            }
            case 109: {
                return this.jjMoveStringLiteralDfa1_1(0x10000000000000L);
            }
            case 110: {
                return this.jjMoveStringLiteralDfa1_1(0x5000010000L);
            }
            case 111: {
                return this.jjMoveStringLiteralDfa1_1(0x40000000000L);
            }
            case 116: {
                return this.jjMoveStringLiteralDfa1_1(16384L);
            }
            case 123: {
                return this.jjStopAtPos(0, 8);
            }
            case 124: {
                return this.jjMoveStringLiteralDfa1_1(0x20000000000L);
            }
            case 125: {
                return this.jjStopAtPos(0, 9);
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
            case 38: {
                if ((active0 & 0x8000000000L) == 0L) break;
                return this.jjStopAtPos(1, 39);
            }
            case 61: {
                if ((active0 & 0x20000000L) != 0L) {
                    return this.jjStopAtPos(1, 29);
                }
                if ((active0 & 0x80000000L) != 0L) {
                    return this.jjStopAtPos(1, 31);
                }
                if ((active0 & 0x200000000L) != 0L) {
                    return this.jjStopAtPos(1, 33);
                }
                if ((active0 & 0x800000000L) != 0L) {
                    return this.jjStopAtPos(1, 35);
                }
                if ((active0 & 0x20000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 53);
            }
            case 62: {
                if ((active0 & 0x80000000000000L) == 0L) break;
                return this.jjStopAtPos(1, 55);
            }
            case 97: {
                return this.jjMoveStringLiteralDfa2_1(active0, 32768L);
            }
            case 101: {
                if ((active0 & 0x40000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 30, 30);
                }
                if ((active0 & 0x100000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 32, 30);
                }
                if ((active0 & 0x1000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 36, 30);
            }
            case 105: {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x4000000000000L);
            }
            case 109: {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x80000000000L);
            }
            case 110: {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x110000000000L);
            }
            case 111: {
                return this.jjMoveStringLiteralDfa2_1(active0, 0x10004000000000L);
            }
            case 113: {
                if ((active0 & 0x400000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 34, 30);
            }
            case 114: {
                if ((active0 & 0x40000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 42, 30);
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 16384L);
            }
            case 116: {
                if ((active0 & 0x4000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(1, 26, 30);
                }
                if ((active0 & 0x10000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(1, 28, 30);
            }
            case 117: {
                return this.jjMoveStringLiteralDfa2_1(active0, 65536L);
            }
            case 124: {
                if ((active0 & 0x20000000000L) == 0L) break;
                return this.jjStopAtPos(1, 41);
            }
        }
        return this.jjStartNfa_1(0, active0);
    }

    private int jjMoveStringLiteralDfa2_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 100: {
                if ((active0 & 0x10000000000L) != 0L) {
                    return this.jjStartNfaWithStates_1(2, 40, 30);
                }
                if ((active0 & 0x10000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(2, 52, 30);
            }
            case 108: {
                return this.jjMoveStringLiteralDfa3_1(active0, 98304L);
            }
            case 112: {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x80000000000L);
            }
            case 115: {
                return this.jjMoveStringLiteralDfa3_1(active0, 0x100000000000L);
            }
            case 116: {
                if ((active0 & 0x4000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(2, 38, 30);
            }
            case 117: {
                return this.jjMoveStringLiteralDfa3_1(active0, 16384L);
            }
            case 118: {
                if ((active0 & 0x4000000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(2, 50, 30);
            }
        }
        return this.jjStartNfa_1(1, active0);
    }

    private int jjMoveStringLiteralDfa3_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 101: {
                if ((active0 & 0x4000L) == 0L) break;
                return this.jjStartNfaWithStates_1(3, 14, 30);
            }
            case 108: {
                if ((active0 & 0x10000L) == 0L) break;
                return this.jjStartNfaWithStates_1(3, 16, 30);
            }
            case 115: {
                return this.jjMoveStringLiteralDfa4_1(active0, 32768L);
            }
            case 116: {
                return this.jjMoveStringLiteralDfa4_1(active0, 0x180000000000L);
            }
        }
        return this.jjStartNfa_1(2, active0);
    }

    private int jjMoveStringLiteralDfa4_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 97: {
                return this.jjMoveStringLiteralDfa5_1(active0, 0x100000000000L);
            }
            case 101: {
                if ((active0 & 0x8000L) == 0L) break;
                return this.jjStartNfaWithStates_1(4, 15, 30);
            }
            case 121: {
                if ((active0 & 0x80000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(4, 43, 30);
            }
        }
        return this.jjStartNfa_1(3, active0);
    }

    private int jjMoveStringLiteralDfa5_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 110: {
                return this.jjMoveStringLiteralDfa6_1(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_1(4, active0);
    }

    private int jjMoveStringLiteralDfa6_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 99: {
                return this.jjMoveStringLiteralDfa7_1(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_1(5, active0);
    }

    private int jjMoveStringLiteralDfa7_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 101: {
                return this.jjMoveStringLiteralDfa8_1(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_1(6, active0);
    }

    private int jjMoveStringLiteralDfa8_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 111: {
                return this.jjMoveStringLiteralDfa9_1(active0, 0x100000000000L);
            }
        }
        return this.jjStartNfa_1(7, active0);
    }

    private int jjMoveStringLiteralDfa9_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 102: {
                if ((active0 & 0x100000000000L) == 0L) break;
                return this.jjStartNfaWithStates_1(9, 44, 30);
            }
        }
        return this.jjStartNfa_1(8, active0);
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
        this.jjnewStateCnt = 30;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < 64) {
                long l = 1L << this.curChar;
                block54: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind > 10) {
                                    kind = 10;
                                }
                                this.jjCheckNAddStates(18, 22);
                                break;
                            }
                            if ((0x1800000000L & l) != 0L) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(23, 25);
                                break;
                            }
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(26, 28);
                                break;
                            }
                            if (this.curChar != 46) break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 30: {
                            if ((0x3FF001000000000L & l) != 0L) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                this.jjCheckNAdd(29);
                            }
                            if ((0x3FF001000000000L & l) == 0L) break;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 3: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 34) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 8: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 9: {
                            if (this.curChar != 34 || kind <= 13) continue block54;
                            kind = 13;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 39) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 13: {
                            if ((0x8400000000L & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 14: {
                            if (this.curChar != 39 || kind <= 13) continue block54;
                            kind = 13;
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjCheckNAddStates(18, 22);
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 10) {
                                kind = 10;
                            }
                            this.jjCheckNAdd(16);
                            break;
                        }
                        case 17: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(17, 18);
                            break;
                        }
                        case 18: {
                            if (this.curChar != 46) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            break;
                        }
                        case 21: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(22);
                            break;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(22);
                            break;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(23, 24);
                            break;
                        }
                        case 25: {
                            if ((0x280000000000L & l) == 0L) break;
                            this.jjCheckNAdd(26);
                            break;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) == 0L) continue block54;
                            if (kind > 11) {
                                kind = 11;
                            }
                            this.jjCheckNAdd(26);
                            break;
                        }
                        case 27: {
                            if ((0x1800000000L & l) == 0L) continue block54;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 28: {
                            if ((0x3FF001000000000L & l) == 0L) continue block54;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 29: {
                            if ((0x3FF001000000000L & l) == 0L) continue block54;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 0x3F);
                block55: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block55;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 30: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0L) {
                                if (kind > 57) {
                                    kind = 57;
                                }
                                this.jjCheckNAdd(29);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 2: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(29, 30);
                            break;
                        }
                        case 6: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 7: {
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 92) break;
                            this.jjCheckNAddStates(26, 28);
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 12: {
                            if (this.curChar != 92) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 92) break;
                            this.jjCheckNAddStates(23, 25);
                            break;
                        }
                        case 20: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(31, 32);
                            break;
                        }
                        case 24: {
                            if ((0x2000000020L & l) == 0L) break;
                            this.jjAddStates(33, 34);
                            break;
                        }
                        case 28: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block55;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 29: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block55;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
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
                block56: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            break;
                        }
                        case 30: {
                            if (ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) {
                                if (kind > 56) {
                                    kind = 56;
                                }
                                this.jjCheckNAdd(28);
                            }
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
                            break;
                        }
                        case 6: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block56;
                            this.jjAddStates(26, 28);
                            break;
                        }
                        case 11: {
                            if (!ELParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block56;
                            this.jjAddStates(23, 25);
                            break;
                        }
                        case 28: {
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 56) {
                                kind = 56;
                            }
                            this.jjCheckNAdd(28);
                            break;
                        }
                        case 29: {
                            if (!ELParserTokenManager.jjCanMove_1(hiByte, i1, i2, l1, l2)) continue block56;
                            if (kind > 57) {
                                kind = 57;
                            }
                            this.jjCheckNAdd(29);
                            break;
                        }
                        default: {
                            if (i1 != 0 && l1 != 0L && i2 != 0 && l2 != 0L) continue block56;
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
            if (i == (startsAt = 30 - this.jjnewStateCnt)) {
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

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec4[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec5[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec6[i2] & l2) != 0L;
            }
            case 51: {
                return (jjbitVec7[i2] & l2) != 0L;
            }
            case 61: {
                return (jjbitVec8[i2] & l2) != 0L;
            }
        }
        return (jjbitVec3[i1] & l1) != 0L;
    }

    public Token getNextToken() {
        int curPos = 0;
        block13: while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (Exception e) {
                this.jjmatchedKind = 0;
                this.jjmatchedPos = -1;
                Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            this.image = this.jjimage;
            this.image.setLength(0);
            this.jjimageLen = 0;
            switch (this.curLexState) {
                case 0: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_0();
                    break;
                }
                case 1: {
                    try {
                        this.input_stream.backup(0);
                        while (this.curChar <= 32 && (0x100002600L & 1L << this.curChar) != 0L) {
                            this.curChar = this.input_stream.BeginToken();
                        }
                    }
                    catch (IOException e1) {
                        continue block13;
                    }
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_1();
                    if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 61) break;
                    this.jjmatchedKind = 61;
                    break;
                }
                case 2: {
                    try {
                        this.input_stream.backup(0);
                        while (this.curChar <= 32 && (0x100002600L & 1L << this.curChar) != 0L) {
                            this.curChar = this.input_stream.BeginToken();
                        }
                    }
                    catch (IOException e1) {
                        continue block13;
                    }
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_2();
                    if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 61) break;
                    this.jjmatchedKind = 61;
                }
            }
            if (this.jjmatchedKind == Integer.MAX_VALUE) break;
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
            if (this.curChar == 10 || this.curChar == 13) {
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

    void SkipLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            default: 
        }
    }

    void MoreLexicalActions() {
        this.lengthOfMatch = this.jjmatchedPos + 1;
        this.jjimageLen += this.lengthOfMatch;
        switch (this.jjmatchedKind) {
            default: 
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 2: {
                this.image.append(jjstrLiteralImages[2]);
                this.lengthOfMatch = jjstrLiteralImages[2].length();
                this.deque.push(0);
                break;
            }
            case 3: {
                this.image.append(jjstrLiteralImages[3]);
                this.lengthOfMatch = jjstrLiteralImages[3].length();
                this.deque.push(0);
                break;
            }
            case 8: {
                this.image.append(jjstrLiteralImages[8]);
                this.lengthOfMatch = jjstrLiteralImages[8].length();
                this.deque.push(this.curLexState);
                break;
            }
            case 9: {
                this.image.append(jjstrLiteralImages[9]);
                this.lengthOfMatch = jjstrLiteralImages[9].length();
                this.SwitchTo(this.deque.pop());
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

    public ELParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ELParserTokenManager(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
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
        int i = 30;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 3 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
}

