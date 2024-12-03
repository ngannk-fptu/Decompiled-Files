/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soyparse;

import com.google.template.soy.soyparse.SimpleCharStream;
import com.google.template.soy.soyparse.SoyFileParserConstants;
import com.google.template.soy.soyparse.Token;
import com.google.template.soy.soyparse.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class SoyFileParserTokenManager
implements SoyFileParserConstants {
    private boolean seenDelpackage = false;
    private boolean seenNamespace = false;
    private boolean seenAlias = false;
    private boolean seenTemplate = false;
    private String currTemplateCmdName = null;
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{139, 149, 161, 171, 177, 180, 181, 88, 90, 91, 6, 8, 9, 50, 52, 53, 10, 11, 17, 18, 25, 12, 14, 15, 11, 16, 17, 18, 25, 19, 21, 11, 22, 24, 17, 19, 21, 11, 22, 24, 17, 38, 39, 54, 55, 57, 58, 64, 65, 55, 57, 58, 59, 61, 62, 55, 57, 58, 63, 64, 65, 77, 78, 92, 93, 99, 100, 117, 94, 96, 97, 93, 98, 99, 100, 117, 101, 103, 93, 104, 106, 116, 113, 115, 99, 101, 103, 93, 104, 106, 116, 108, 110, 111, 112, 113, 115, 99, 113, 115, 99, 102, 105, 114, 125, 126, 205, 206, 208, 229, 230, 231, 232, 189, 199, 88, 90, 6, 8, 217, 227, 239, 249, 257, 267, 34, 48, 73, 86, 121, 130, 131, 50, 52, 108, 110, 202, 210, 203, 204, 64, 65, 93, 103, 74, 84, 52, 62, 11, 21, 66, 67, 43, 63, 85, 31, 41, 5, 10, 6, 7, 9};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "IN_TEMPLATE", "IN_SOY_DOC"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, 0, -1, -1, 1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = new long[]{0xFEE1FFFL};
    static final long[] jjtoSkip = new long[]{1610735616L};
    static final long[] jjtoSpecial = new long[]{1610735616L};
    static final long[] jjtoMore = new long[]{0x100000L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[268];
    private final int[] jjstateSet = new int[536];
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

    private static void throwTokenMgrError(String message) throws TokenMgrError {
        throw new TokenMgrError(message, 0);
    }

    private static void throwTokenMgrError(String msg, Token matchedToken) throws TokenMgrError {
        throw new TokenMgrError(msg + " [line " + matchedToken.beginLine + ", column " + matchedToken.beginColumn + "].", 0);
    }

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x2000L) != 0L) {
                    this.jjmatchedKind = 30;
                    return 268;
                }
                if ((active0 & 0x1110L) != 0L) {
                    this.jjmatchedKind = 30;
                    return 34;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 72;
                }
                if ((active0 & 0x2000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 120;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 33;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 32;
                }
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 119;
                }
                if ((active0 & 0x2000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 71;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 31;
                }
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 70;
                }
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 118;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x1000L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 87;
                }
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 69;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 30;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x1000L) != 0L) {
                    return 269;
                }
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 68;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 29;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 67;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 28;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 66;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 27;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 49;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
                    return 26;
                }
                return -1;
            }
            case 9: {
                if ((active0 & 0x100L) != 0L) {
                    return 270;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = 0;
                    }
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
            case '/': {
                return this.jjMoveStringLiteralDfa1_0(8192L);
            }
            case '{': {
                return this.jjMoveStringLiteralDfa1_0(4368L);
            }
        }
        return this.jjMoveNfa_0(0, 0);
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
            case '*': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8192L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4096L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_0(active0, 16L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 256L);
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
            case '*': {
                if ((active0 & 0x2000L) == 0L) break;
                return this.jjStopAtPos(2, 13);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 256L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_0(active0, 16L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4096L);
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
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 4096L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_0(active0, 16L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa4_0(active0, 256L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(active0, 4096L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa5_0(active0, 256L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 16L);
            }
        }
        return this.jjStartNfa_0(3, active0);
    }

    private int jjMoveStringLiteralDfa5_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 16L);
            }
            case 's': {
                if ((active0 & 0x1000L) != 0L) {
                    return this.jjStartNfaWithStates_0(5, 12, 269);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 256L);
            }
        }
        return this.jjStartNfa_0(4, active0);
    }

    private int jjMoveStringLiteralDfa6_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa7_0(active0, 16L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa7_0(active0, 256L);
            }
        }
        return this.jjStartNfa_0(5, active0);
    }

    private int jjMoveStringLiteralDfa7_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa8_0(active0, 256L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa8_0(active0, 16L);
            }
        }
        return this.jjStartNfa_0(6, active0);
    }

    private int jjMoveStringLiteralDfa8_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa9_0(active0, 16L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa9_0(active0, 256L);
            }
        }
        return this.jjStartNfa_0(7, active0);
    }

    private int jjMoveStringLiteralDfa9_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x100L) == 0L) break;
                return this.jjStartNfaWithStates_0(9, 8, 270);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa10_0(active0, 16L);
            }
        }
        return this.jjStartNfa_0(8, active0);
    }

    private int jjMoveStringLiteralDfa10_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_0(8, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(9, active0);
            return 10;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x10L) == 0L) break;
                return this.jjStartNfaWithStates_0(10, 4, 271);
            }
        }
        return this.jjStartNfa_0(9, active0);
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
        this.jjnewStateCnt = 268;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block306: do {
                    switch (this.jjstateSet[--i]) {
                        case 34: {
                            if (this.curChar == '/') {
                                this.jjAddStates(0, 1);
                            }
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 179;
                            break;
                        }
                        case 0: {
                            if (kind > 30) {
                                kind = 30;
                            }
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0L) {
                                this.jjAddStates(2, 6);
                            } else if ((0x2400L & l) != 0L && kind > 29) {
                                kind = 29;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 269: {
                            this.jjCheckNAddTwoStates(123, 124);
                            if ((0x100002600L & l) != 0L) {
                                this.jjCheckNAddStates(7, 9);
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 89;
                            break;
                        }
                        case 180: 
                        case 268: {
                            if (this.curChar != '/') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 179;
                            break;
                        }
                        case 271: {
                            this.jjCheckNAddTwoStates(36, 37);
                            if ((0x100002600L & l) != 0L) {
                                this.jjCheckNAddStates(10, 12);
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 270: {
                            this.jjCheckNAddTwoStates(75, 76);
                            if ((0x100002600L & l) != 0L) {
                                this.jjCheckNAddStates(13, 15);
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 29) continue block306;
                            kind = 29;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (kind <= 30) break;
                            kind = 30;
                            break;
                        }
                        case 6: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 8: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 10: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(16, 20);
                            break;
                        }
                        case 11: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(21, 23);
                            break;
                        }
                        case 12: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(21, 23);
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(21, 23);
                            break;
                        }
                        case 14: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(24, 28);
                            break;
                        }
                        case 18: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(29, 34);
                            break;
                        }
                        case 19: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(35, 37);
                            break;
                        }
                        case 20: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(35, 37);
                            break;
                        }
                        case 21: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(20);
                            break;
                        }
                        case 22: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(38, 40);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(38, 40);
                            break;
                        }
                        case 24: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(23);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAddTwoStates(20, 23);
                            break;
                        }
                        case 36: {
                            this.jjCheckNAddTwoStates(36, 37);
                            break;
                        }
                        case 38: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjAddStates(41, 42);
                            break;
                        }
                        case 39: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 3) continue block306;
                            kind = 3;
                            break;
                        }
                        case 50: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 51: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 52: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 54: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(43, 48);
                            break;
                        }
                        case 55: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(49, 51);
                            break;
                        }
                        case 56: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(49, 51);
                            break;
                        }
                        case 57: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 56;
                            break;
                        }
                        case 58: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(52, 54);
                            break;
                        }
                        case 59: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(52, 54);
                            break;
                        }
                        case 60: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(52, 54);
                            break;
                        }
                        case 61: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 60;
                            break;
                        }
                        case 63: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(55, 60);
                            break;
                        }
                        case 64: {
                            this.jjCheckNAddTwoStates(64, 65);
                            break;
                        }
                        case 75: {
                            this.jjCheckNAddTwoStates(75, 76);
                            break;
                        }
                        case 77: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjAddStates(61, 62);
                            break;
                        }
                        case 78: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 7) continue block306;
                            kind = 7;
                            break;
                        }
                        case 88: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(7, 9);
                            break;
                        }
                        case 89: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(7, 9);
                            break;
                        }
                        case 90: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 89;
                            break;
                        }
                        case 92: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(63, 67);
                            break;
                        }
                        case 93: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAddStates(68, 70);
                            break;
                        }
                        case 94: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(68, 70);
                            break;
                        }
                        case 95: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(68, 70);
                            break;
                        }
                        case 96: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 95;
                            break;
                        }
                        case 98: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(71, 75);
                            break;
                        }
                        case 100: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(76, 84);
                            break;
                        }
                        case 101: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(85, 87);
                            break;
                        }
                        case 102: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(85, 87);
                            break;
                        }
                        case 103: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(102);
                            break;
                        }
                        case 104: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(88, 90);
                            break;
                        }
                        case 105: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(88, 90);
                            break;
                        }
                        case 106: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(105);
                            break;
                        }
                        case 108: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(91, 93);
                            break;
                        }
                        case 109: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(91, 93);
                            break;
                        }
                        case 110: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 109;
                            break;
                        }
                        case 112: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(94, 97);
                            break;
                        }
                        case 113: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAddStates(98, 100);
                            break;
                        }
                        case 114: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAddStates(98, 100);
                            break;
                        }
                        case 115: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAdd(114);
                            break;
                        }
                        case 117: {
                            if (this.curChar != '\r') break;
                            this.jjCheckNAddStates(101, 103);
                            break;
                        }
                        case 123: {
                            this.jjCheckNAddTwoStates(123, 124);
                            break;
                        }
                        case 125: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjAddStates(104, 105);
                            break;
                        }
                        case 126: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 11) continue block306;
                            kind = 11;
                            break;
                        }
                        case 131: {
                            if (this.curChar != '/') break;
                            this.jjAddStates(0, 1);
                            break;
                        }
                        case 150: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjAddStates(2, 6);
                            break;
                        }
                        case 178: {
                            if (this.curChar != '*' || kind <= 17) continue block306;
                            kind = 17;
                            break;
                        }
                        case 179: {
                            if (this.curChar != '*') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 178;
                            break;
                        }
                        case 202: {
                            if ((0x100002600L & l) == 0L) break;
                            this.jjCheckNAdd(203);
                            break;
                        }
                        case 203: {
                            this.jjCheckNAddTwoStates(203, 204);
                            break;
                        }
                        case 205: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjAddStates(106, 108);
                            break;
                        }
                        case 206: {
                            if ((0x2400L & l) == 0L || kind <= 19) continue block306;
                            kind = 19;
                            break;
                        }
                        case 207: {
                            if (this.curChar != '\n' || kind <= 19) continue block306;
                            kind = 19;
                            break;
                        }
                        case 208: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 207;
                            break;
                        }
                        case 209: {
                            if (this.curChar != '\n') break;
                            this.jjCheckNAdd(203);
                            break;
                        }
                        case 210: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 209;
                            break;
                        }
                        case 229: {
                            this.jjAddStates(109, 110);
                            break;
                        }
                        case 231: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjAddStates(111, 112);
                            break;
                        }
                        case 232: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 23) continue block306;
                            kind = 23;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block307: do {
                    switch (this.jjstateSet[--i]) {
                        case 70: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 82;
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 67: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 79;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 34: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 266;
                            } else if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 256;
                            } else if (this.curChar == '{') {
                                this.jjAddStates(113, 114);
                            } else if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 129;
                            } else if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 85;
                            }
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 248;
                            } else if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 238;
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 176;
                            } else if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 120;
                            } else if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                            }
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 226;
                            } else if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 216;
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 170;
                            }
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 47;
                            } else if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 160;
                            }
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 87: {
                            if (this.curChar == 's') {
                                this.jjCheckNAddTwoStates(123, 124);
                            }
                            if (this.curChar != 's') break;
                            this.jjAddStates(115, 116);
                            break;
                        }
                        case 5: {
                            if (this.curChar == 'e') {
                                this.jjCheckNAddTwoStates(36, 37);
                            }
                            if (this.curChar != 'e') break;
                            this.jjAddStates(117, 118);
                            break;
                        }
                        case 68: {
                            if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 80;
                            }
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 67;
                            break;
                        }
                        case 0: {
                            this.jjAddStates(2, 6);
                            if (kind > 30) {
                                kind = 30;
                            }
                            if (this.curChar == '{') {
                                this.jjAddStates(119, 124);
                            }
                            if (this.curChar != '{') break;
                            this.jjAddStates(125, 131);
                            break;
                        }
                        case 118: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 122;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 87;
                            break;
                        }
                        case 119: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 127;
                            }
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 118;
                            break;
                        }
                        case 33: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 265;
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 247;
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 225;
                            }
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 46;
                            }
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 69: {
                            if (this.curChar == 's') {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                            }
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 68;
                            break;
                        }
                        case 32: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 264;
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 246;
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 224;
                            }
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 45;
                            }
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 31;
                            break;
                        }
                        case 269: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddTwoStates(123, 124);
                                break;
                            }
                            if (this.curChar != '}') break;
                            this.jjAddStates(104, 105);
                            break;
                        }
                        case 26: {
                            if (this.curChar == 'g') {
                                this.jjstateSet[this.jjnewStateCnt++] = 35;
                            }
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 27: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 40;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 66: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                            }
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 71: {
                            if (this.curChar == 'm') {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
                            }
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 49: {
                            if (this.curChar == 'e') {
                                this.jjCheckNAddTwoStates(75, 76);
                            }
                            if (this.curChar != 'e') break;
                            this.jjAddStates(132, 133);
                            break;
                        }
                        case 268: {
                            if (this.curChar == '{') {
                                this.jjAddStates(113, 114);
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 176;
                            }
                            if (this.curChar == '{') {
                                this.jjstateSet[this.jjnewStateCnt++] = 170;
                            }
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 160;
                            break;
                        }
                        case 28: {
                            if (this.curChar == 'k') {
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                            }
                            if (this.curChar != 'k') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 72: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 71;
                            break;
                        }
                        case 29: {
                            if (this.curChar == 'c') {
                                this.jjstateSet[this.jjnewStateCnt++] = 42;
                            }
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 271: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddTwoStates(36, 37);
                                break;
                            }
                            if (this.curChar != '}') break;
                            this.jjAddStates(41, 42);
                            break;
                        }
                        case 31: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 263;
                            } else if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 245;
                            } else if (this.curChar == 'p') {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                            }
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 223;
                            break;
                        }
                        case 120: {
                            if (this.curChar == 'l') {
                                this.jjstateSet[this.jjnewStateCnt++] = 128;
                            }
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 119;
                            break;
                        }
                        case 270: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0L) {
                                this.jjCheckNAddTwoStates(75, 76);
                                break;
                            }
                            if (this.curChar != '}') break;
                            this.jjAddStates(61, 62);
                            break;
                        }
                        case 30: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                            }
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 3: {
                            if (kind <= 30) break;
                            kind = 30;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(125, 131);
                            break;
                        }
                        case 9: 
                        case 10: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(16, 20);
                            break;
                        }
                        case 15: 
                        case 16: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(24, 28);
                            break;
                        }
                        case 17: {
                            if (this.curChar != '}' || kind <= 1) continue block307;
                            kind = 1;
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'e') break;
                            this.jjCheckNAddTwoStates(36, 37);
                            break;
                        }
                        case 36: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(36, 37);
                            break;
                        }
                        case 37: {
                            if (this.curChar != '}') break;
                            this.jjAddStates(41, 42);
                            break;
                        }
                        case 39: {
                            if (kind <= 3) break;
                            kind = 3;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 35;
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 42: {
                            if (this.curChar != 'k') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 43: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 44: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 43;
                            break;
                        }
                        case 45: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 44;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 47: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 48: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 47;
                            break;
                        }
                        case 53: 
                        case 54: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(43, 48);
                            break;
                        }
                        case 62: 
                        case 63: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(55, 60);
                            break;
                        }
                        case 64: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(64, 65);
                            break;
                        }
                        case 65: {
                            if (this.curChar != '}' || kind <= 5) continue block307;
                            kind = 5;
                            break;
                        }
                        case 73: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 72;
                            break;
                        }
                        case 74: {
                            if (this.curChar != 'e') break;
                            this.jjCheckNAddTwoStates(75, 76);
                            break;
                        }
                        case 75: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(75, 76);
                            break;
                        }
                        case 76: {
                            if (this.curChar != '}') break;
                            this.jjAddStates(61, 62);
                            break;
                        }
                        case 78: {
                            if (kind <= 7) break;
                            kind = 7;
                            break;
                        }
                        case 79: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 74;
                            break;
                        }
                        case 80: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 79;
                            break;
                        }
                        case 81: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 80;
                            break;
                        }
                        case 82: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 81;
                            break;
                        }
                        case 83: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 82;
                            break;
                        }
                        case 84: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 83;
                            break;
                        }
                        case 85: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 84;
                            break;
                        }
                        case 86: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 85;
                            break;
                        }
                        case 91: 
                        case 92: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(63, 67);
                            break;
                        }
                        case 97: 
                        case 98: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(71, 75);
                            break;
                        }
                        case 99: {
                            if (this.curChar != '}' || kind <= 9) continue block307;
                            kind = 9;
                            break;
                        }
                        case 107: {
                            if (this.curChar != 's') break;
                            this.jjAddStates(134, 135);
                            break;
                        }
                        case 111: 
                        case 112: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) break;
                            this.jjCheckNAddStates(94, 97);
                            break;
                        }
                        case 116: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 107;
                            break;
                        }
                        case 121: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 120;
                            break;
                        }
                        case 122: {
                            if (this.curChar != 's') break;
                            this.jjCheckNAddTwoStates(123, 124);
                            break;
                        }
                        case 123: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(123, 124);
                            break;
                        }
                        case 124: {
                            if (this.curChar != '}') break;
                            this.jjAddStates(104, 105);
                            break;
                        }
                        case 126: {
                            if (kind <= 11) break;
                            kind = 11;
                            break;
                        }
                        case 127: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 122;
                            break;
                        }
                        case 128: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 127;
                            break;
                        }
                        case 129: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 128;
                            break;
                        }
                        case 130: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 129;
                            break;
                        }
                        case 132: {
                            if (this.curChar != 'e' || kind <= 27) continue block307;
                            kind = 27;
                            break;
                        }
                        case 133: 
                        case 140: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(132);
                            break;
                        }
                        case 134: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 133;
                            break;
                        }
                        case 135: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 134;
                            break;
                        }
                        case 136: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 135;
                            break;
                        }
                        case 137: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 136;
                            break;
                        }
                        case 138: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 137;
                            break;
                        }
                        case 139: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 138;
                            break;
                        }
                        case 141: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 140;
                            break;
                        }
                        case 142: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 141;
                            break;
                        }
                        case 143: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 142;
                            break;
                        }
                        case 144: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 143;
                            break;
                        }
                        case 145: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 144;
                            break;
                        }
                        case 146: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 145;
                            break;
                        }
                        case 147: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 146;
                            break;
                        }
                        case 148: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 147;
                            break;
                        }
                        case 149: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 148;
                            break;
                        }
                        case 150: {
                            this.jjAddStates(2, 6);
                            break;
                        }
                        case 151: {
                            if (this.curChar != 'e' || kind <= 2) continue block307;
                            kind = 2;
                            break;
                        }
                        case 152: {
                            if (this.curChar != 'g') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 151;
                            break;
                        }
                        case 153: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 152;
                            break;
                        }
                        case 154: {
                            if (this.curChar != 'k') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 153;
                            break;
                        }
                        case 155: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 154;
                            break;
                        }
                        case 156: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 155;
                            break;
                        }
                        case 157: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 156;
                            break;
                        }
                        case 158: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 157;
                            break;
                        }
                        case 159: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 158;
                            break;
                        }
                        case 160: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 159;
                            break;
                        }
                        case 161: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 160;
                            break;
                        }
                        case 162: {
                            if (this.curChar != 'e' || kind <= 6) continue block307;
                            kind = 6;
                            break;
                        }
                        case 163: {
                            if (this.curChar != 'c') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 162;
                            break;
                        }
                        case 164: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 163;
                            break;
                        }
                        case 165: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 164;
                            break;
                        }
                        case 166: {
                            if (this.curChar != 's') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 165;
                            break;
                        }
                        case 167: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 166;
                            break;
                        }
                        case 168: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 167;
                            break;
                        }
                        case 169: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 168;
                            break;
                        }
                        case 170: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 169;
                            break;
                        }
                        case 171: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 170;
                            break;
                        }
                        case 172: {
                            if (this.curChar != 's' || kind <= 10) continue block307;
                            kind = 10;
                            break;
                        }
                        case 173: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 172;
                            break;
                        }
                        case 174: {
                            if (this.curChar != 'i') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 173;
                            break;
                        }
                        case 175: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 174;
                            break;
                        }
                        case 176: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 175;
                            break;
                        }
                        case 177: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 176;
                            break;
                        }
                        case 181: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(113, 114);
                            break;
                        }
                        case 182: {
                            if (this.curChar != 'e' || kind <= 22) continue block307;
                            kind = 22;
                            break;
                        }
                        case 183: 
                        case 190: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(182);
                            break;
                        }
                        case 184: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 183;
                            break;
                        }
                        case 185: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 184;
                            break;
                        }
                        case 186: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 185;
                            break;
                        }
                        case 187: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 186;
                            break;
                        }
                        case 188: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 187;
                            break;
                        }
                        case 189: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 188;
                            break;
                        }
                        case 191: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 190;
                            break;
                        }
                        case 192: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 191;
                            break;
                        }
                        case 193: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 192;
                            break;
                        }
                        case 194: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 193;
                            break;
                        }
                        case 195: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 194;
                            break;
                        }
                        case 196: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 195;
                            break;
                        }
                        case 197: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 196;
                            break;
                        }
                        case 198: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 197;
                            break;
                        }
                        case 199: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 198;
                            break;
                        }
                        case 200: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(119, 124);
                            break;
                        }
                        case 201: {
                            if (this.curChar != 'e') break;
                            this.jjAddStates(136, 137);
                            break;
                        }
                        case 203: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjAddStates(138, 139);
                            break;
                        }
                        case 204: {
                            if (this.curChar != '}') break;
                            this.jjAddStates(106, 108);
                            break;
                        }
                        case 211: 
                        case 218: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 212: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 211;
                            break;
                        }
                        case 213: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 212;
                            break;
                        }
                        case 214: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 213;
                            break;
                        }
                        case 215: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 214;
                            break;
                        }
                        case 216: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 215;
                            break;
                        }
                        case 217: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 216;
                            break;
                        }
                        case 219: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 218;
                            break;
                        }
                        case 220: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 219;
                            break;
                        }
                        case 221: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 220;
                            break;
                        }
                        case 222: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 221;
                            break;
                        }
                        case 223: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 222;
                            break;
                        }
                        case 224: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 223;
                            break;
                        }
                        case 225: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 224;
                            break;
                        }
                        case 226: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 225;
                            break;
                        }
                        case 227: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 226;
                            break;
                        }
                        case 228: {
                            if (this.curChar != 'e') break;
                            this.jjCheckNAdd(229);
                            break;
                        }
                        case 229: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(229, 230);
                            break;
                        }
                        case 230: {
                            if (this.curChar != '}') break;
                            this.jjAddStates(111, 112);
                            break;
                        }
                        case 232: {
                            if (kind <= 23) break;
                            kind = 23;
                            break;
                        }
                        case 233: 
                        case 240: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(228);
                            break;
                        }
                        case 234: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 233;
                            break;
                        }
                        case 235: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 234;
                            break;
                        }
                        case 236: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 235;
                            break;
                        }
                        case 237: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 236;
                            break;
                        }
                        case 238: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 237;
                            break;
                        }
                        case 239: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 238;
                            break;
                        }
                        case 241: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 240;
                            break;
                        }
                        case 242: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 241;
                            break;
                        }
                        case 243: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 242;
                            break;
                        }
                        case 244: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 243;
                            break;
                        }
                        case 245: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 244;
                            break;
                        }
                        case 246: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 245;
                            break;
                        }
                        case 247: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 246;
                            break;
                        }
                        case 248: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 247;
                            break;
                        }
                        case 249: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 248;
                            break;
                        }
                        case 250: {
                            if (this.curChar != 'e' || kind <= 26) continue block307;
                            kind = 26;
                            break;
                        }
                        case 251: 
                        case 258: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(250);
                            break;
                        }
                        case 252: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 251;
                            break;
                        }
                        case 253: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 252;
                            break;
                        }
                        case 254: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 253;
                            break;
                        }
                        case 255: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 254;
                            break;
                        }
                        case 256: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 255;
                            break;
                        }
                        case 257: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 256;
                            break;
                        }
                        case 259: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 258;
                            break;
                        }
                        case 260: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 259;
                            break;
                        }
                        case 261: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 260;
                            break;
                        }
                        case 262: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 261;
                            break;
                        }
                        case 263: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 262;
                            break;
                        }
                        case 264: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 263;
                            break;
                        }
                        case 265: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 264;
                            break;
                        }
                        case 266: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 265;
                            break;
                        }
                        case 267: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 266;
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
                block308: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 30) {
                                kind = 30;
                            }
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 6);
                            break;
                        }
                        case 123: 
                        case 269: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(123, 124);
                            break;
                        }
                        case 36: 
                        case 271: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(36, 37);
                            break;
                        }
                        case 75: 
                        case 270: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(75, 76);
                            break;
                        }
                        case 3: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 30) continue block308;
                            kind = 30;
                            break;
                        }
                        case 39: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 3) continue block308;
                            kind = 3;
                            break;
                        }
                        case 64: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(140, 141);
                            break;
                        }
                        case 78: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 7) continue block308;
                            kind = 7;
                            break;
                        }
                        case 126: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 11) continue block308;
                            kind = 11;
                            break;
                        }
                        case 150: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(2, 6);
                            break;
                        }
                        case 203: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(138, 139);
                            break;
                        }
                        case 229: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(109, 110);
                            break;
                        }
                        case 232: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 23) continue block308;
                            kind = 23;
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
            if (i == (startsAt = 268 - this.jjnewStateCnt)) {
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
                if ((active0 & 0x1110L) != 0L) {
                    this.jjmatchedKind = 20;
                    return 43;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x1100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return 40;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x1100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return 39;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x1100L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                if ((active0 & 0x10L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return 38;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x1110L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x1110L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x110L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x110L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x110L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 9: {
                if ((active0 & 0x110L) != 0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 20;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
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
            case '{': {
                return this.jjMoveStringLiteralDfa1_1(4368L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa2_1(active0, 4096L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_1(active0, 16L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_1(active0, 256L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa3_1(active0, 256L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa3_1(active0, 16L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_1(active0, 4096L);
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
            case 'i': {
                return this.jjMoveStringLiteralDfa4_1(active0, 4096L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_1(active0, 16L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa4_1(active0, 256L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa5_1(active0, 4096L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa5_1(active0, 256L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_1(active0, 16L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa6_1(active0, 16L);
            }
            case 's': {
                if ((active0 & 0x1000L) != 0L) {
                    return this.jjStopAtPos(5, 12);
                }
                return this.jjMoveStringLiteralDfa6_1(active0, 256L);
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
            case 'c': {
                return this.jjMoveStringLiteralDfa7_1(active0, 16L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa7_1(active0, 256L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa8_1(active0, 256L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa8_1(active0, 16L);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa9_1(active0, 16L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa9_1(active0, 256L);
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
            case 'e': {
                if ((active0 & 0x100L) == 0L) break;
                return this.jjStopAtPos(9, 8);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa10_1(active0, 16L);
            }
        }
        return this.jjStartNfa_1(8, active0);
    }

    private int jjMoveStringLiteralDfa10_1(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return this.jjStartNfa_1(8, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_1(9, active0);
            return 10;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x10L) == 0L) break;
                return this.jjStopAtPos(10, 4);
            }
        }
        return this.jjStartNfa_1(9, active0);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 104;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block115: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 20) {
                                kind = 20;
                            }
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 43: {
                            if (this.curChar == '/') {
                                this.jjAddStates(142, 143);
                            }
                            if (this.curChar == '/') {
                                this.jjAddStates(144, 145);
                            }
                            if (this.curChar != '/') break;
                            this.jjAddStates(146, 147);
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '/') break;
                            this.jjAddStates(148, 149);
                            break;
                        }
                        case 63: {
                            if (this.curChar != '/') break;
                            this.jjAddStates(144, 145);
                            break;
                        }
                        case 66: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjAddStates(150, 151);
                            break;
                        }
                        case 67: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 25) continue block115;
                            kind = 25;
                            break;
                        }
                        case 85: {
                            if (this.curChar != '/') break;
                            this.jjAddStates(142, 143);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block116: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            if (kind > 20) {
                                kind = 20;
                            }
                            if (this.curChar == '{') {
                                this.jjAddStates(152, 154);
                            }
                            if (this.curChar != '{') break;
                            this.jjAddStates(155, 156);
                            break;
                        }
                        case 43: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 40;
                                break;
                            }
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                break;
                            }
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 1: {
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '}' || kind <= 24) continue block116;
                            kind = 24;
                            break;
                        }
                        case 5: 
                        case 12: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(3);
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 7;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 10;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 13;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 18: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 19: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 18;
                            break;
                        }
                        case 20: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 21: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 22: {
                            if (this.curChar != '{') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 23: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(155, 156);
                            break;
                        }
                        case 24: {
                            if (this.curChar != 'e' || kind <= 26) continue block116;
                            kind = 26;
                            break;
                        }
                        case 25: 
                        case 32: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(24);
                            break;
                        }
                        case 26: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            break;
                        }
                        case 27: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 28: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 27;
                            break;
                        }
                        case 29: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 28;
                            break;
                        }
                        case 30: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 31: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 30;
                            break;
                        }
                        case 33: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 32;
                            break;
                        }
                        case 34: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 33;
                            break;
                        }
                        case 35: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 34;
                            break;
                        }
                        case 36: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 35;
                            break;
                        }
                        case 37: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 38: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 37;
                            break;
                        }
                        case 39: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 40: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 39;
                            break;
                        }
                        case 41: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 40;
                            break;
                        }
                        case 42: {
                            if (this.curChar != '{') break;
                            this.jjAddStates(152, 154);
                            break;
                        }
                        case 44: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            break;
                        }
                        case 45: {
                            if (this.curChar != '}' || kind <= 21) continue block116;
                            kind = 21;
                            break;
                        }
                        case 46: 
                        case 53: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(44);
                            break;
                        }
                        case 47: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 46;
                            break;
                        }
                        case 48: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 47;
                            break;
                        }
                        case 49: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 48;
                            break;
                        }
                        case 50: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 49;
                            break;
                        }
                        case 51: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 50;
                            break;
                        }
                        case 52: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            break;
                        }
                        case 54: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 53;
                            break;
                        }
                        case 55: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 56: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 55;
                            break;
                        }
                        case 57: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 56;
                            break;
                        }
                        case 58: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 57;
                            break;
                        }
                        case 59: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 58;
                            break;
                        }
                        case 60: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 59;
                            break;
                        }
                        case 61: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 60;
                            break;
                        }
                        case 62: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 64: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 65;
                            break;
                        }
                        case 65: {
                            if (this.curChar != '}') break;
                            this.jjAddStates(150, 151);
                            break;
                        }
                        case 67: {
                            if (kind <= 25) break;
                            kind = 25;
                            break;
                        }
                        case 68: 
                        case 75: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(64);
                            break;
                        }
                        case 69: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 68;
                            break;
                        }
                        case 70: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 69;
                            break;
                        }
                        case 71: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 72: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 71;
                            break;
                        }
                        case 73: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 72;
                            break;
                        }
                        case 74: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 73;
                            break;
                        }
                        case 76: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 75;
                            break;
                        }
                        case 77: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 76;
                            break;
                        }
                        case 78: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 77;
                            break;
                        }
                        case 79: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 78;
                            break;
                        }
                        case 80: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 79;
                            break;
                        }
                        case 81: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 80;
                            break;
                        }
                        case 82: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 81;
                            break;
                        }
                        case 83: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 82;
                            break;
                        }
                        case 84: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 83;
                            break;
                        }
                        case 86: {
                            if (this.curChar != 'e' || kind <= 27) continue block116;
                            kind = 27;
                            break;
                        }
                        case 87: 
                        case 94: {
                            if (this.curChar != 't') break;
                            this.jjCheckNAdd(86);
                            break;
                        }
                        case 88: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 87;
                            break;
                        }
                        case 89: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 88;
                            break;
                        }
                        case 90: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 89;
                            break;
                        }
                        case 91: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 90;
                            break;
                        }
                        case 92: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 91;
                            break;
                        }
                        case 93: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 92;
                            break;
                        }
                        case 95: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 94;
                            break;
                        }
                        case 96: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 95;
                            break;
                        }
                        case 97: {
                            if (this.curChar != 'p') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 96;
                            break;
                        }
                        case 98: {
                            if (this.curChar != 'm') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 97;
                            break;
                        }
                        case 99: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 98;
                            break;
                        }
                        case 100: {
                            if (this.curChar != 't') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 99;
                            break;
                        }
                        case 101: {
                            if (this.curChar != 'l') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 100;
                            break;
                        }
                        case 102: {
                            if (this.curChar != 'e') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 101;
                            break;
                        }
                        case 103: {
                            if (this.curChar != 'd') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 102;
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
                block117: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 20) {
                                kind = 20;
                            }
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 1: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                            break;
                        }
                        case 67: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 25) continue block117;
                            kind = 25;
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
            if (i == (startsAt = 104 - this.jjnewStateCnt)) {
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

    private int jjMoveStringLiteralDfa0_2() {
        return this.jjMoveNfa_2(0, 0);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 13;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block26: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind > 15) {
                                kind = 15;
                            }
                            if ((0x2400L & l) != 0L) {
                                if (kind > 14) {
                                    kind = 14;
                                }
                            } else if (this.curChar == '*') {
                                this.jjAddStates(157, 158);
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 1: {
                            if (this.curChar != '\n' || kind <= 14) continue block26;
                            kind = 14;
                            break;
                        }
                        case 2: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (kind <= 15) break;
                            kind = 15;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '*') break;
                            this.jjAddStates(157, 158);
                            break;
                        }
                        case 5: {
                            if (this.curChar != '/') break;
                            this.jjCheckNAddStates(159, 161);
                            break;
                        }
                        case 6: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddStates(159, 161);
                            break;
                        }
                        case 7: {
                            if ((0x2400L & l) == 0L || kind <= 16) continue block26;
                            kind = 16;
                            break;
                        }
                        case 8: {
                            if (this.curChar != '\n' || kind <= 16) continue block26;
                            kind = 16;
                            break;
                        }
                        case 9: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != '/') break;
                            this.jjCheckNAddTwoStates(11, 12);
                            break;
                        }
                        case 11: {
                            if ((0x100000200L & l) == 0L) break;
                            this.jjCheckNAddTwoStates(11, 12);
                            break;
                        }
                        case 12: {
                            if ((0xFFFFFFFEFFFFD9FFL & l) == 0L || kind <= 18) continue block26;
                            kind = 18;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (kind <= 15) break;
                            kind = 15;
                            break;
                        }
                        case 12: {
                            if (kind <= 18) break;
                            kind = 18;
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
                block28: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 15) continue block28;
                            kind = 15;
                            break;
                        }
                        case 12: {
                            if (!SoyFileParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 18) continue block28;
                            kind = 18;
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
            if (i == (startsAt = 13 - this.jjnewStateCnt)) {
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

    public SoyFileParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public SoyFileParserTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 268;
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
        Token specialToken = null;
        int curPos = 0;
        block11: while (true) {
            Token matchedToken;
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
            this.image = this.jjimage;
            this.image.setLength(0);
            this.jjimageLen = 0;
            while (true) {
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
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block11;
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    matchedToken = this.jjFillToken();
                    matchedToken.specialToken = specialToken;
                    this.TokenLexicalActions(matchedToken);
                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                    if ((jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
                        matchedToken = this.jjFillToken();
                        if (specialToken == null) {
                            specialToken = matchedToken;
                        } else {
                            matchedToken.specialToken = specialToken;
                            specialToken = specialToken.next = matchedToken;
                        }
                        this.SkipLexicalActions(matchedToken);
                    } else {
                        this.SkipLexicalActions(null);
                    }
                    if (jjnewLexState[this.jjmatchedKind] == -1) continue block11;
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                    continue block11;
                }
                this.jjimageLen += this.jjmatchedPos + 1;
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }
                curPos = 0;
                this.jjmatchedKind = Integer.MAX_VALUE;
                try {
                    this.curChar = this.input_stream.readChar();
                }
                catch (IOException e) {
                    // empty catch block
                    break block11;
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

    void SkipLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            default: 
        }
    }

    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 1: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.seenDelpackage) {
                    SoyFileParserTokenManager.throwTokenMgrError("Found multiple 'delpackage' declarations.");
                }
                if (this.seenNamespace) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'delpackage' declaration must appear before the 'namespace' declaration.");
                }
                if (this.seenAlias) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'delpackage' declaration must appear before any 'alias' declarations.");
                }
                if (this.seenTemplate) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'delpackage' declaration must appear before any templates.");
                }
                this.seenDelpackage = true;
                break;
            }
            case 2: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Tag 'delpackage' not at start of line.");
                break;
            }
            case 3: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End of tag 'delpackage' not at end of line.");
                break;
            }
            case 4: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Invalid 'delpackage' tag", matchedToken);
                break;
            }
            case 5: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (this.seenNamespace) {
                    SoyFileParserTokenManager.throwTokenMgrError("Found multiple 'namespace' declarations.");
                }
                if (this.seenAlias) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'namespace' declaration must appear before any 'alias' declarations.");
                }
                if (this.seenTemplate) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'namespace' declaration must appear before any templates.");
                }
                this.seenNamespace = true;
                break;
            }
            case 6: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Tag 'namespace' not at start of line.");
                break;
            }
            case 7: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End of tag 'namespace' not at end of line.");
                break;
            }
            case 8: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Invalid 'namespace' tag", matchedToken);
                break;
            }
            case 9: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                if (!this.seenNamespace) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'alias' declarations must appear after the 'namespace' declaration.");
                }
                if (this.seenTemplate) {
                    SoyFileParserTokenManager.throwTokenMgrError("The 'alias' declarations must appear before any templates.");
                }
                this.seenAlias = true;
                break;
            }
            case 10: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Tag 'alias' not at start of line.");
                break;
            }
            case 11: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End of tag 'alias' not at end of line.");
                break;
            }
            case 12: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Invalid 'alias' tag", matchedToken);
                break;
            }
            case 17: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("SoyDoc not at start of line", matchedToken);
                break;
            }
            case 18: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End of SoyDoc not at end of line", matchedToken);
                break;
            }
            case 19: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                this.seenTemplate = true;
                if (this.image.substring(0, 9).equals("{template")) {
                    this.currTemplateCmdName = "template";
                } else if (this.image.substring(0, 12).equals("{deltemplate")) {
                    this.currTemplateCmdName = "deltemplate";
                } else {
                    throw new AssertionError();
                }
                int rbIndex = this.image.lastIndexOf("}");
                matchedToken.image = this.image.substring(0, rbIndex + 1);
                break;
            }
            case 21: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                int lbIndex = this.image.lastIndexOf("{");
                switch (this.image.length() - lbIndex) {
                    case 11: {
                        if (this.currTemplateCmdName.equals("template")) break;
                        SoyFileParserTokenManager.throwTokenMgrError("Cannot start a template with 'deltemplate' and end it with '/template'.");
                        break;
                    }
                    case 14: {
                        if (this.currTemplateCmdName.equals("deltemplate")) break;
                        SoyFileParserTokenManager.throwTokenMgrError("Cannot start a template with 'template' and end it with '/deltemplate'.");
                        break;
                    }
                    default: {
                        throw new AssertionError();
                    }
                }
                this.currTemplateCmdName = null;
                matchedToken.image = this.image.substring(0, lbIndex);
                break;
            }
            case 22: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Template tag not at start of line", matchedToken);
                break;
            }
            case 23: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End of template tag not at end of line", matchedToken);
                break;
            }
            case 24: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End-template tag not at start of line", matchedToken);
                break;
            }
            case 25: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("End of end-template tag not at end of line", matchedToken);
                break;
            }
            case 26: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Invalid template tag", matchedToken);
                break;
            }
            case 27: {
                this.lengthOfMatch = this.jjmatchedPos + 1;
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen + this.lengthOfMatch));
                SoyFileParserTokenManager.throwTokenMgrError("Invalid end-template tag", matchedToken);
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

