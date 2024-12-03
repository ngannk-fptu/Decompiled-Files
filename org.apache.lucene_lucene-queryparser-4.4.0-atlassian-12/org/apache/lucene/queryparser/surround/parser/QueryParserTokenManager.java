/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.surround.parser;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.lucene.queryparser.surround.parser.CharStream;
import org.apache.lucene.queryparser.surround.parser.QueryParserConstants;
import org.apache.lucene.queryparser.surround.parser.Token;
import org.apache.lucene.queryparser.surround.parser.TokenMgrError;

public class QueryParserTokenManager
implements QueryParserConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{32, 33, 34, 35, 37, 24, 27, 28, 20, 17, 21, 18, 27, 28, 30, 24, 25, 0, 1};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, null, "(", ")", ",", ":", "^", null, null, null, null, null, null};
    public static final String[] lexStateNames = new String[]{"Boost", "DEFAULT"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 1};
    static final long[] jjtoToken = new long[]{0xFFFF01L};
    static final long[] jjtoSkip = new long[]{128L};
    protected CharStream input_stream;
    private final int[] jjrounds = new int[38];
    private final int[] jjstateSet = new int[76];
    protected char curChar;
    int curLexState = 1;
    int defaultLexState = 1;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_1(int pos, long active0) {
        switch (pos) {
            default: 
        }
        return -1;
    }

    private final int jjStartNfa_1(int pos, long active0) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '(': {
                return this.jjStopAtPos(0, 13);
            }
            case ')': {
                return this.jjStopAtPos(0, 14);
            }
            case ',': {
                return this.jjStopAtPos(0, 15);
            }
            case ':': {
                return this.jjStopAtPos(0, 16);
            }
            case '^': {
                return this.jjStopAtPos(0, 17);
            }
        }
        return this.jjMoveNfa_1(0, 0);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 38;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block63: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) != 0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(0, 4);
                            } else if ((0x100002600L & l) != 0L) {
                                if (kind > 7) {
                                    kind = 7;
                                }
                            } else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(5, 7);
                            }
                            if ((0x3FC000000000000L & l) != 0L) {
                                this.jjCheckNAddStates(8, 11);
                                break;
                            }
                            if (this.curChar != '1') break;
                            this.jjCheckNAddTwoStates(20, 21);
                            break;
                        }
                        case 19: {
                            if ((0x3FC000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(8, 11);
                            break;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(17);
                            break;
                        }
                        case 21: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAdd(18);
                            break;
                        }
                        case 22: {
                            if (this.curChar != '1') break;
                            this.jjCheckNAddTwoStates(20, 21);
                            break;
                        }
                        case 23: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(5, 7);
                            break;
                        }
                        case 24: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(24, 25);
                            break;
                        }
                        case 25: {
                            if (this.curChar != '\"') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 26;
                            break;
                        }
                        case 26: {
                            if (this.curChar != '*' || kind <= 18) continue block63;
                            kind = 18;
                            break;
                        }
                        case 27: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\"') break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 30: {
                            if (this.curChar != '\"' || kind <= 19) continue block63;
                            kind = 19;
                            break;
                        }
                        case 31: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) == 0L) continue block63;
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 32: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(32, 33);
                            break;
                        }
                        case 33: {
                            if (this.curChar != '*' || kind <= 20) continue block63;
                            kind = 20;
                            break;
                        }
                        case 34: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(34, 35);
                            break;
                        }
                        case 35: {
                            if ((0x8000040000000000L & l) == 0L) continue block63;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(35, 36);
                            break;
                        }
                        case 36: {
                            if ((0xFBFFECFAFFFFD9FFL & l) == 0L) continue block63;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAdd(36);
                            break;
                        }
                        case 37: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) == 0L) continue block63;
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(37);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block64: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) != 0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(0, 4);
                            }
                            if ((0x400000004000L & l) != 0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                            } else if ((0x80000000800000L & l) != 0L) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                            } else if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            } else if (this.curChar == 'A') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                            } else if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            } else if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                break;
                            }
                            if (this.curChar != 'N') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 1: {
                            if (this.curChar != 'R' || kind <= 8) continue block64;
                            kind = 8;
                            break;
                        }
                        case 2: {
                            if (this.curChar != 'O') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (this.curChar != 'r' || kind <= 8) continue block64;
                            kind = 8;
                            break;
                        }
                        case 4: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 5: {
                            if (this.curChar != 'D' || kind <= 9) continue block64;
                            kind = 9;
                            break;
                        }
                        case 6: {
                            if (this.curChar != 'N') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 5;
                            break;
                        }
                        case 7: {
                            if (this.curChar != 'A') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 6;
                            break;
                        }
                        case 8: {
                            if (this.curChar != 'd' || kind <= 9) continue block64;
                            kind = 9;
                            break;
                        }
                        case 9: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 8;
                            break;
                        }
                        case 10: {
                            if (this.curChar != 'a') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 11: {
                            if (this.curChar != 'T' || kind <= 10) continue block64;
                            kind = 10;
                            break;
                        }
                        case 12: {
                            if (this.curChar != 'O') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 11;
                            break;
                        }
                        case 13: {
                            if (this.curChar != 'N') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 14: {
                            if (this.curChar != 't' || kind <= 10) continue block64;
                            kind = 10;
                            break;
                        }
                        case 15: {
                            if (this.curChar != 'o') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 16: {
                            if (this.curChar != 'n') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 15;
                            break;
                        }
                        case 17: {
                            if ((0x80000000800000L & l) == 0L || kind <= 11) continue block64;
                            kind = 11;
                            break;
                        }
                        case 18: {
                            if ((0x400000004000L & l) == 0L || kind <= 12) continue block64;
                            kind = 12;
                            break;
                        }
                        case 24: {
                            this.jjAddStates(15, 16);
                            break;
                        }
                        case 27: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 28: {
                            if (this.curChar != '\\') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 29;
                            break;
                        }
                        case 29: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(12, 14);
                            break;
                        }
                        case 31: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0L) continue block64;
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 32: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(32, 33);
                            break;
                        }
                        case 34: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddTwoStates(34, 35);
                            break;
                        }
                        case 36: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0L) continue block64;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 37: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0L) continue block64;
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(37);
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
                block65: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block65;
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddStates(0, 4);
                            break;
                        }
                        case 24: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(15, 16);
                            break;
                        }
                        case 27: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjAddStates(12, 14);
                            break;
                        }
                        case 32: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(32, 33);
                            break;
                        }
                        case 34: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddTwoStates(34, 35);
                            break;
                        }
                        case 36: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block65;
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 37: {
                            if (!QueryParserTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block65;
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(37);
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
            if (i == (startsAt = 38 - this.jjnewStateCnt)) {
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
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjAddStates(17, 18);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0L) continue block12;
                            if (kind > 23) {
                                kind = 23;
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

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0: {
                return (jjbitVec2[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
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
        int i = 38;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }

    public void ReInit(CharStream stream, int lexState) {
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

