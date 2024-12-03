/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.parse;

import com.google.template.soy.types.parse.SimpleCharStream;
import com.google.template.soy.types.parse.Token;
import com.google.template.soy.types.parse.TokenMgrError;
import com.google.template.soy.types.parse.TypeParserConstants;
import java.io.IOException;
import java.io.PrintStream;

public class TypeParserTokenManager
implements TypeParserConstants {
    public PrintStream debugStream = System.out;
    static final int[] jjnextStates = new int[0];
    public static final String[] jjstrLiteralImages = new String[]{"", "<", ">", "[", "]", ",", "|", ":", ".", "?", "list", "map", null, null};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    static final long[] jjtoToken = new long[]{8191L};
    static final long[] jjtoSkip = new long[]{8192L};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[5];
    private final int[] jjstateSet = new int[10];
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
                if ((active0 & 0xC00L) != 0L) {
                    this.jjmatchedKind = 12;
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0xC00L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 1;
                    return 1;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x800L) != 0L) {
                    return 1;
                }
                if ((active0 & 0x400L) != 0L) {
                    this.jjmatchedKind = 12;
                    this.jjmatchedPos = 2;
                    return 1;
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
            case ',': {
                return this.jjStopAtPos(0, 5);
            }
            case '.': {
                return this.jjStopAtPos(0, 8);
            }
            case ':': {
                return this.jjStopAtPos(0, 7);
            }
            case '<': {
                return this.jjStopAtPos(0, 1);
            }
            case '>': {
                return this.jjStopAtPos(0, 2);
            }
            case '?': {
                return this.jjStopAtPos(0, 9);
            }
            case '[': {
                return this.jjStopAtPos(0, 3);
            }
            case ']': {
                return this.jjStopAtPos(0, 4);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(1024L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(2048L);
            }
            case '|': {
                return this.jjStopAtPos(0, 6);
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
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2048L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1024L);
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
            case 'p': {
                if ((active0 & 0x800L) == 0L) break;
                return this.jjStartNfaWithStates_0(2, 11, 1);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1024L);
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
            case 't': {
                if ((active0 & 0x400L) == 0L) break;
                return this.jjStartNfaWithStates_0(3, 10, 1);
            }
        }
        return this.jjStartNfa_0(2, active0);
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
                block15: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x100002600L & l) != 0L && kind > 13) {
                                kind = 13;
                            }
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0L) continue block15;
                            kind = 12;
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            break;
                        }
                        case 2: {
                            if ((0x100002600L & l) == 0L || kind <= 13) continue block15;
                            kind = 13;
                            break;
                        }
                        case 3: {
                            if (this.curChar != '\n' || kind <= 13) continue block15;
                            kind = 13;
                            break;
                        }
                        case 4: {
                            if (this.curChar != '\r') break;
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                            break;
                        }
                    }
                } while (i != startsAt);
            } else if (this.curChar < '\u0080') {
                long l = 1L << (this.curChar & 0x3F);
                block16: do {
                    switch (this.jjstateSet[--i]) {
                        case 0: 
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0L) continue block16;
                            if (kind > 12) {
                                kind = 12;
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

    public TypeParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public TypeParserTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 5;
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
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                curPos = this.jjMoveStringLiteralDfa0_0();
                if (this.jjmatchedKind == Integer.MAX_VALUE) break block7;
                if (this.jjmatchedPos + 1 >= curPos) continue;
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            } while ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0L);
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
}

