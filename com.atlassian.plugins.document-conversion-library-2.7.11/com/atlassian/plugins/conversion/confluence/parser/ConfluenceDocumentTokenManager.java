/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.parser.ConfluenceDocumentConstants;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import com.atlassian.plugins.conversion.confluence.parser.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class ConfluenceDocumentTokenManager
implements ConfluenceDocumentConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{47, 49, 50, 51, 32, 0, 34, 2, 31, 32, 0, 33, 34, 2, 35, 36, 7, 15, 16, 31, 32, 0, 33, 34, 2, 52, 53, 39, 42, 44, 46, 47, 49, 51, 21, 22, 1, 2};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, "{{", "|", "bq.", null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "INSIDEMACRO"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[54];
    private final int[] jjstateSet = new int[108];
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
                if ((active0 & 0x1000L) != 0L) {
                    this.jjmatchedKind = 16;
                    return 39;
                }
                if ((active0 & 0x4000L) != 0L) {
                    this.jjmatchedKind = 16;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x1000L) != 0L) {
                    return 41;
                }
                if ((active0 & 0x4000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 16;
                        this.jjmatchedPos = 0;
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

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjStartNfaWithStates_0(int pos, int kind, int state) {
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

    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(16384L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case '|': {
                return this.jjStopAtPos(0, 13);
            }
        }
        return this.jjMoveNfa_0(5, 0);
    }

    private final int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case 'q': {
                return this.jjMoveStringLiteralDfa2_0(active0, 16384L);
            }
            case '{': {
                if ((active0 & 0x1000L) == 0L) break;
                return this.jjStartNfaWithStates_0(1, 12, 41);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
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
            case '.': {
                if ((active0 & 0x4000L) == 0L) break;
                return this.jjStopAtPos(2, 14);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }

    private final void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private final void jjAddStates(int start, int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private final void jjCheckNAddTwoStates(int state1, int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }

    private final void jjCheckNAddStates(int start, int end) {
        do {
            this.jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }

    private final void jjCheckNAddStates(int start) {
        this.jjCheckNAdd(jjnextStates[start]);
        this.jjCheckNAdd(jjnextStates[start + 1]);
    }

    private final int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 54;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block76: do {
                    switch (this.jjstateSet[--i]) {
                        case 39: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0L) {
                                this.jjCheckNAddStates(0, 3);
                            }
                            if ((0x2C0000000000L & l) != 0L) {
                                this.jjCheckNAdd(40);
                                break;
                            }
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0L) {
                                if (kind > 16) {
                                    kind = 16;
                                }
                            } else if ((0x2400L & l) != 0L && kind > 3) {
                                kind = 3;
                            }
                            if ((0x40800000000L & l) != 0L) {
                                this.jjCheckNAddStates(4, 7);
                            } else if ((0x100000200L & l) != 0L) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                                this.jjCheckNAddStates(8, 16);
                            } else if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 27;
                            } else if (this.curChar == '!') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                            } else if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                            }
                            if (this.curChar == '#') {
                                this.jjCheckNAdd(3);
                                break;
                            }
                            if (this.curChar != '*') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 0: {
                            if (this.curChar != '*') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0x100000200L & l) == 0L) continue block76;
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '#') break;
                            this.jjCheckNAdd(3);
                            break;
                        }
                        case 3: {
                            if ((0x100000200L & l) == 0L) continue block76;
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(3);
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\n' || kind <= 3) continue block76;
                            kind = 3;
                            break;
                        }
                        case 6: {
                            if ((0x2400L & l) == 0L || kind <= 3) continue block76;
                            kind = 3;
                            break;
                        }
                        case 8: {
                            if ((0x3FE000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '.' || kind <= 5) continue block76;
                            kind = 5;
                            break;
                        }
                        case 10: {
                            if (this.curChar != '!') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 11: {
                            if ((0xFFFFFFFCFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFFFDFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '!' || kind <= 6) continue block76;
                            kind = 6;
                            break;
                        }
                        case 15: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjAddStates(17, 18);
                            break;
                        }
                        case 18: {
                            if (this.curChar != '!' || kind <= 10) continue block76;
                            kind = 10;
                            break;
                        }
                        case 20: {
                            if (this.curChar != '\n' || kind <= 11) continue block76;
                            kind = 11;
                            break;
                        }
                        case 21: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if ((0x2400L & l) == 0L || kind <= 11) continue block76;
                            kind = 11;
                            break;
                        }
                        case 24: {
                            if (this.curChar != '-') continue block76;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 25: {
                            if (this.curChar != '-' || kind <= 15) continue block76;
                            kind = 15;
                            break;
                        }
                        case 26: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 24;
                            break;
                        }
                        case 27: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != '-') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 29: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L || kind <= 16) continue block76;
                            kind = 16;
                            break;
                        }
                        case 30: {
                            if ((0x100000200L & l) == 0L) continue block76;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjCheckNAddStates(8, 16);
                            break;
                        }
                        case 31: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(19, 21);
                            break;
                        }
                        case 32: {
                            if ((0x40800000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(32, 0);
                            break;
                        }
                        case 33: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(22, 24);
                            break;
                        }
                        case 34: {
                            if ((0x40800000000L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(34, 2);
                            break;
                        }
                        case 35: {
                            if ((0x100000200L & l) == 0L) continue block76;
                            if (kind > 4) {
                                kind = 4;
                            }
                            this.jjCheckNAdd(35);
                            break;
                        }
                        case 36: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(36, 7);
                            break;
                        }
                        case 37: {
                            if ((0x40800000000L & l) == 0L) break;
                            this.jjCheckNAddStates(4, 7);
                            break;
                        }
                        case 45: {
                            if (this.curChar != '?') break;
                            this.jjCheckNAdd(40);
                            break;
                        }
                        case 46: {
                            if (this.curChar != '?') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 48: {
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                        case 49: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block77: do {
                    switch (this.jjstateSet[--i]) {
                        case 39: {
                            if ((0xD7FFFFFFEFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddStates(0, 3);
                            } else if (this.curChar == '\\') {
                                this.jjAddStates(25, 26);
                            } else if (this.curChar == '}') {
                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                            }
                            if ((0x40000000C0000000L & l) != 0L) {
                                this.jjCheckNAdd(40);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 5: {
                            if (kind > 16) {
                                kind = 16;
                            }
                            if (this.curChar == '{') {
                                this.jjCheckNAddStates(27, 33);
                            } else if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                            } else if (this.curChar == '[') {
                                this.jjCheckNAdd(15);
                            } else if (this.curChar == 'h') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                            }
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'h') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 11: {
                            if ((0xEFFFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 12: {
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 14: {
                            if (this.curChar != '[') break;
                            this.jjCheckNAdd(15);
                            break;
                        }
                        case 15: {
                            if ((0xFFFFFFFFD7FFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(15, 16);
                            break;
                        }
                        case 16: {
                            if (this.curChar != ']' || kind <= 7) continue block77;
                            kind = 7;
                            break;
                        }
                        case 17: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 18: {
                            if ((0x3800000028000000L & l) == 0L || kind <= 10) continue block77;
                            kind = 10;
                            break;
                        }
                        case 19: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(34, 35);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 29: {
                            if (kind <= 16) break;
                            kind = 16;
                            break;
                        }
                        case 38: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAddStates(27, 33);
                            break;
                        }
                        case 40: {
                            if (this.curChar != '}' || kind <= 8) continue block77;
                            kind = 8;
                            break;
                        }
                        case 41: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAdd(40);
                            break;
                        }
                        case 42: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 43: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAdd(40);
                            break;
                        }
                        case 44: {
                            if (this.curChar != '}') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 43;
                            break;
                        }
                        case 47: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 48: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                        case 49: {
                            if ((0xD7FFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                        case 50: {
                            if (this.curChar != '}' || kind <= 9) continue block77;
                            kind = 9;
                            break;
                        }
                        case 51: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(25, 26);
                            break;
                        }
                        case 52: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                        case 53: {
                            if (this.curChar != '}') break;
                            this.jjCheckNAddStates(0, 3);
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
                block78: do {
                    switch (this.jjstateSet[--i]) {
                        case 39: 
                        case 48: 
                        case 49: {
                            if (!ConfluenceDocumentTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(0, 3);
                            break;
                        }
                        case 5: {
                            if (!ConfluenceDocumentTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 16) continue block78;
                            kind = 16;
                            break;
                        }
                        case 11: 
                        case 12: {
                            if (!ConfluenceDocumentTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(12, 13);
                            break;
                        }
                        case 15: {
                            if (!ConfluenceDocumentTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(17, 18);
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
            if (i == (startsAt = 54 - this.jjnewStateCnt)) {
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

    private final int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1(0, 0);
    }

    private final int jjMoveNfa_1(int startState, int curPos) {
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
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1: {
                            this.jjAddStates(36, 37);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (this.curChar != '{') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(1, 2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != '}') break;
                            kind = 17;
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
                        case 1: {
                            if (!ConfluenceDocumentTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(36, 37);
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

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    public ConfluenceDocumentTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ConfluenceDocumentTokenManager(SimpleCharStream stream, int lexState) {
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

    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 54;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(SimpleCharStream stream, int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 2 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }

    protected Token jjFillToken() {
        Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        String im = jjstrLiteralImages[this.jjmatchedKind];
        t.image = im == null ? this.input_stream.GetImage() : im;
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }

    public Token getNextToken() {
        Object specialToken = null;
        int curPos = 0;
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
                if (this.jjmatchedPos != 0 || this.jjmatchedKind <= 18) break;
                this.jjmatchedKind = 18;
            }
        }
        if (this.jjmatchedKind != Integer.MAX_VALUE) {
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            Token matchedToken = this.jjFillToken();
            if (jjnewLexState[this.jjmatchedKind] != -1) {
                this.curLexState = jjnewLexState[this.jjmatchedKind];
            }
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
}

