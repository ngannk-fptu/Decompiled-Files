/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.parser;

import com.atlassian.plugins.conversion.confluence.parser.ConfluencePreParserConstants;
import com.atlassian.plugins.conversion.confluence.parser.SimpleCharStream;
import com.atlassian.plugins.conversion.confluence.parser.Token;
import com.atlassian.plugins.conversion.confluence.parser.TokenMgrError;
import java.io.IOException;
import java.io.PrintStream;

public class ConfluencePreParserTokenManager
implements ConfluencePreParserConstants {
    public PrintStream debugStream = System.out;
    static final int[] jjnextStates = new int[0];
    public static final String[] jjstrLiteralImages = new String[]{"", "&nbsp;", null};
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
            case '&': {
                return this.jjMoveStringLiteralDfa1_0(2L);
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
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2L);
            }
        }
        return 2;
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return 2;
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 2;
        }
        switch (this.curChar) {
            case 'b': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2L);
            }
        }
        return 3;
    }

    private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return 3;
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 3;
        }
        switch (this.curChar) {
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2L);
            }
        }
        return 4;
    }

    private final int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return 4;
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 4;
        }
        switch (this.curChar) {
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 2L);
            }
        }
        return 5;
    }

    private final int jjMoveStringLiteralDfa5_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return 5;
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 5;
        }
        switch (this.curChar) {
            case ';': {
                if ((active0 & 2L) == 0L) break;
                return this.jjStopAtPos(5, 1);
            }
            default: {
                return 6;
            }
        }
        return 6;
    }

    public ConfluencePreParserTokenManager(SimpleCharStream stream) {
        this.input_stream = stream;
    }

    public ConfluencePreParserTokenManager(SimpleCharStream stream, int lexState) {
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
        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 2) {
            this.jjmatchedKind = 2;
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

