/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.io.IOException;
import java.io.PrintStream;
import org.springframework.ldap.core.DnParserImplConstants;
import org.springframework.ldap.core.SimpleCharStream;
import org.springframework.ldap.core.Token;
import org.springframework.ldap.core.TokenMgrError;

public class DnParserImplTokenManager
implements DnParserImplConstants {
    public PrintStream debugStream = System.out;
    static final long[] jjbitVec0 = new long[]{-2L, -1L, -1L, -1L};
    static final long[] jjbitVec2 = new long[]{0L, 0L, -1L, -1L};
    static final int[] jjnextStates = new int[]{4, 5, 6, 14, 15, 7, 8, 10, 11};
    public static final String[] jjstrLiteralImages = new String[]{"", null, null, null, null, null, null, null, null, null, null, null, "\"", "#", null, null, " ", null, null, ",", ";", "+"};
    public static final String[] lexStateNames = new String[]{"DEFAULT", "ATTRVALUE_S", "SPACED_EQUALS_S"};
    public static final int[] jjnewLexState = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[17];
    private final int[] jjstateSet = new int[34];
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

    private int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1(0, 0);
    }

    private int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 17;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block44: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xA7FFE7F2FFFFFFFFL & l) != 0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            if (this.curChar != '#') break;
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0L) continue block44;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAdd(1);
                            break;
                        }
                        case 3: {
                            if ((0xA7FFE7F2FFFFFFFFL & l) == 0L) continue block44;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 4: {
                            if ((0xA7FFE7FBFFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 5: {
                            if ((0xA7FFE7FAFFFFFFFFL & l) == 0L || kind <= 17) continue block44;
                            kind = 17;
                            break;
                        }
                        case 7: {
                            if ((0x7800180D00002000L & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 8: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 10: {
                            if ((0x7800180D00002000L & l) == 0L || kind <= 17) continue block44;
                            kind = 17;
                            break;
                        }
                        case 11: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 12: {
                            if ((0x3FF000000000000L & l) == 0L || kind <= 17) continue block44;
                            kind = 17;
                            break;
                        }
                        case 14: {
                            if ((0x7800180D00002000L & l) == 0L) continue block44;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & l) == 0L) continue block44;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block45: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            if (this.curChar != '\\') break;
                            this.jjAddStates(3, 4);
                            break;
                        }
                        case 1: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            break;
                        }
                        case 2: {
                            if ((0x7E0000007EL & l) == 0L) continue block45;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) continue block45;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 4: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 5: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) == 0L || kind <= 17) continue block45;
                            kind = 17;
                            break;
                        }
                        case 6: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(5, 8);
                            break;
                        }
                        case 7: {
                            if (this.curChar != '\\') break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 8: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                            break;
                        }
                        case 9: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 10: {
                            if (this.curChar != '\\' || kind <= 17) continue block45;
                            kind = 17;
                            break;
                        }
                        case 11: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 12;
                            break;
                        }
                        case 12: {
                            if ((0x7E0000007EL & l) == 0L || kind <= 17) continue block45;
                            kind = 17;
                            break;
                        }
                        case 13: {
                            if (this.curChar != '\\') break;
                            this.jjAddStates(3, 4);
                            break;
                        }
                        case 14: {
                            if (this.curChar != '\\') continue block45;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 15: {
                            if ((0x7E0000007EL & l) == 0L) break;
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 16: {
                            if ((0x7E0000007EL & l) == 0L) continue block45;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
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
                block46: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!DnParserImplTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) continue block46;
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 4: {
                            if (!DnParserImplTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2)) break;
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 5: {
                            if (!DnParserImplTokenManager.jjCanMove_0(hiByte, i1, i2, l1, l2) || kind <= 17) continue block46;
                            kind = 17;
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
            if (i == (startsAt = 17 - this.jjnewStateCnt)) {
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

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            default: 
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
            case ' ': {
                return this.jjStopAtPos(0, 16);
            }
            case '\"': {
                return this.jjStopAtPos(0, 12);
            }
            case '#': {
                return this.jjStopAtPos(0, 13);
            }
            case '+': {
                return this.jjStopAtPos(0, 21);
            }
            case ',': {
                return this.jjStopAtPos(0, 19);
            }
            case ';': {
                return this.jjStopAtPos(0, 20);
            }
        }
        return this.jjMoveNfa_0(0, 0);
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 5;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                long l = 1L << this.curChar;
                block14: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0L) continue block14;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            break;
                        }
                        case 1: {
                            if ((0x3FF200000000000L & l) == 0L) continue block14;
                            if (kind > 14) {
                                kind = 14;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '.') break;
                            this.jjCheckNAdd(4);
                            break;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0L) continue block14;
                            if (kind > 15) {
                                kind = 15;
                            }
                            this.jjCheckNAddTwoStates(3, 4);
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0L) continue block15;
                            if (kind > 14) {
                                kind = 14;
                            }
                            this.jjCheckNAdd(1);
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
            if (i == (startsAt = 5 - this.jjnewStateCnt)) {
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
        return this.jjMoveNfa_2(3, 0);
    }

    private int jjMoveNfa_2(int startState, int curPos) {
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
                block13: do {
                    switch (this.jjstateSet[--i]) {
                        case 3: {
                            if (this.curChar == '=') {
                                if (kind > 18) {
                                    kind = 18;
                                }
                                this.jjCheckNAdd(2);
                                break;
                            }
                            if (this.curChar != ' ') break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 0: {
                            if (this.curChar != ' ') break;
                            this.jjCheckNAddTwoStates(0, 1);
                            break;
                        }
                        case 1: {
                            if (this.curChar != '=') continue block13;
                            kind = 18;
                            this.jjCheckNAdd(2);
                            break;
                        }
                        case 2: {
                            if (this.curChar != ' ') continue block13;
                            if (kind > 18) {
                                kind = 18;
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

    public DnParserImplTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public DnParserImplTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 17;
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
                break;
            }
            case 2: {
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_2();
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

