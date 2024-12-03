/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImageConstants;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import com.atlassian.plugins.conversion.confluence.parser.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class ConfluenceImageTokenManager
implements ConfluenceImageConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{0L, -16384L, -17590038560769L, 0x7FFFFFL};
    static final long[] jjbitVec2 = new long[]{0L, 0L, 0x400040000000000L, -36028797027352577L};
    static final long[] jjbitVec3 = new long[]{-1L, -1L, -1L, -270215977642229761L};
    static final long[] jjbitVec4 = new long[]{0xFFFFFFL, -65536L, 0x1FFFFFFFFFFL, 0L};
    static final long[] jjbitVec5 = new long[]{0L, 0L, -17179879616L, 4503588160110591L};
    static final long[] jjbitVec6 = new long[]{-8194L, -536936449L, -65533L, 234134404065073567L};
    static final long[] jjbitVec7 = new long[]{-562949953421312L, -8581545985L, -4900197869555810049L, 1979120929931270L};
    static final long[] jjbitVec8 = new long[]{576460743713488896L, -281474976186369L, 0x7CFFFFFFFFFFFFFFL, 68032818806783L};
    static final long[] jjbitVec9 = new long[]{-4323455642275676178L, 68703174655L, -4339783389948567570L, 844492307446175L};
    static final long[] jjbitVec10 = new long[]{-4364553187899111452L, 4503601204443527L, -2022681381666312210L, 4295048127L};
    static final long[] jjbitVec11 = new long[]{-4337531590134882322L, 15837706639L, -4341532606274353172L, 15815L};
    static final long[] jjbitVec12 = new long[]{0xFFDFFFFFDDFEEL, 0L, -4327961440926441492L, 13958659551L};
    static final long[] jjbitVec13 = new long[]{-4323457841299070996L, 12884917711L, 0L, 0L};
    static final long[] jjbitVec14 = new long[]{0x7FFFFFFFFFFFFFEL, 0xFFFFFFFL, 4323293666156225942L, 805322591L};
    static final long[] jjbitVec15 = new long[]{-4422534834027495423L, -558551906910465L, 215680200883507167L, 0L};
    static final long[] jjbitVec16 = new long[]{0L, 0L, -4294967296L, 36028797018898495L};
    static final long[] jjbitVec17 = new long[]{-1L, -1L, -4026531841L, 0x3FFFFFFFFFFFFFFL};
    static final long[] jjbitVec18 = new long[]{-3233808385L, 0x3FFFFFFFAAFF3F3FL, 0x1FDFFFFFFFFFFFFFL, 2295745090394464220L};
    static final long[] jjbitVec19 = new long[]{0L, Long.MIN_VALUE, 0L, 0L};
    static final long[] jjbitVec20 = new long[]{0L, -2L, -8186232833L, 1765411053929234431L};
    static final long[] jjbitVec21 = new long[]{35184372088800L, 0L, 0L, 0L};
    static final long[] jjbitVec22 = new long[]{-1L, -1L, 0x3FFFFFFFFFL, 0L};
    static final long[] jjbitVec23 = new long[]{-1L, -1L, 0xFFFFFFFFFL, 0L};
    static final int[] jjnextStates = new int[0];
    public static final String[] jjstrLiteralImages = new String[]{"", "!", "|", ",", null, "=", ":", "^", null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[2];
    private final int[] jjstateSet = new int[4];
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
            default: 
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
            case '!': {
                return this.jjStopAtPos(0, 1);
            }
            case ',': {
                return this.jjStopAtPos(0, 3);
            }
            case ':': {
                return this.jjStopAtPos(0, 6);
            }
            case '=': {
                return this.jjStopAtPos(0, 5);
            }
            case '^': {
                return this.jjStopAtPos(0, 7);
            }
            case '|': {
                return this.jjStopAtPos(0, 2);
            }
        }
        return this.jjMoveNfa_0(0, 0);
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
        this.jjnewStateCnt = 2;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block13: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0L) {
                                if (kind <= 8) break;
                                kind = 8;
                                break;
                            }
                            if ((0x100000200L & l) == 0L || kind <= 4) continue block13;
                            kind = 4;
                            break;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            kind = 8;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L) break;
                            kind = 8;
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
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!ConfluenceImageTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 8) continue block15;
                            kind = 8;
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
            if (i == (startsAt = 2 - this.jjnewStateCnt)) {
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
            case 1: {
                return (jjbitVec3[i2] & l2) != 0L;
            }
            case 2: {
                return (jjbitVec4[i2] & l2) != 0L;
            }
            case 3: {
                return (jjbitVec5[i2] & l2) != 0L;
            }
            case 4: {
                return (jjbitVec6[i2] & l2) != 0L;
            }
            case 5: {
                return (jjbitVec7[i2] & l2) != 0L;
            }
            case 6: {
                return (jjbitVec8[i2] & l2) != 0L;
            }
            case 9: {
                return (jjbitVec9[i2] & l2) != 0L;
            }
            case 10: {
                return (jjbitVec10[i2] & l2) != 0L;
            }
            case 11: {
                return (jjbitVec11[i2] & l2) != 0L;
            }
            case 12: {
                return (jjbitVec12[i2] & l2) != 0L;
            }
            case 13: {
                return (jjbitVec13[i2] & l2) != 0L;
            }
            case 14: {
                return (jjbitVec14[i2] & l2) != 0L;
            }
            case 15: {
                return (jjbitVec15[i2] & l2) != 0L;
            }
            case 16: {
                return (jjbitVec16[i2] & l2) != 0L;
            }
            case 30: {
                return (jjbitVec17[i2] & l2) != 0L;
            }
            case 31: {
                return (jjbitVec18[i2] & l2) != 0L;
            }
            case 32: {
                return (jjbitVec19[i2] & l2) != 0L;
            }
            case 48: {
                return (jjbitVec20[i2] & l2) != 0L;
            }
            case 49: {
                return (jjbitVec21[i2] & l2) != 0L;
            }
            case 159: {
                return (jjbitVec22[i2] & l2) != 0L;
            }
            case 215: {
                return (jjbitVec23[i2] & l2) != 0L;
            }
        }
        return (jjbitVec0[i1] & l1) != 0L;
    }

    public ConfluenceImageTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ConfluenceImageTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 2;
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
        this.jjmatchedKind = Integer.MAX_VALUE;
        this.jjmatchedPos = 0;
        curPos = this.jjMoveStringLiteralDfa0_0();
        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 9) {
            this.jjmatchedKind = 9;
        }
        if (this.jjmatchedKind != Integer.MAX_VALUE) {
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            Token matchedToken = this.jjFillToken();
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

