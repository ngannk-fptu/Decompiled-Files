/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.parser.ConfluenceMacroConstants;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import com.atlassian.plugins.conversion.confluence.parser.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class ConfluenceMacroTokenManager
implements ConfluenceMacroConstants {
    public PrintStream debugStream = System.out;
    static final int[] jjnextStates = new int[0];
    public static final String[] jjstrLiteralImages = new String[]{"", "{", "}", "\\{", "\\}", ":", "|", " ", "=", null};
    public static final String[] lexStateNames = new String[]{"DEFAULT"};
    protected SimpleCharStream input_stream;
    private final int[] jjrounds = new int[0];
    private final int[] jjstateSet = new int[0];
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

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case ' ': {
                return this.jjStopAtPos(0, 7);
            }
            case ':': {
                return this.jjStopAtPos(0, 5);
            }
            case '=': {
                return this.jjStopAtPos(0, 8);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_0(24L);
            }
            case '{': {
                return this.jjStopAtPos(0, 1);
            }
            case '|': {
                return this.jjStopAtPos(0, 6);
            }
            case '}': {
                return this.jjStopAtPos(0, 2);
            }
        }
        return 1;
    }

    private final int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 1;
        }
        switch (this.curChar) {
            case '{': {
                if ((active0 & 8L) == 0L) break;
                return this.jjStopAtPos(1, 3);
            }
            case '}': {
                if ((active0 & 0x10L) == 0L) break;
                return this.jjStopAtPos(1, 4);
            }
            default: {
                return 2;
            }
        }
        return 2;
    }

    public ConfluenceMacroTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ConfluenceMacroTokenManager(SimpleCharStream stream, int lexState) {
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
        int i = 0;
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

