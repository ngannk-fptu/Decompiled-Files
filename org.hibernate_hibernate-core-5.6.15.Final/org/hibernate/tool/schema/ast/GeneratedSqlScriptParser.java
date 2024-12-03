/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.LLkParser
 *  antlr.NoViableAltException
 *  antlr.ParserSharedInputState
 *  antlr.RecognitionException
 *  antlr.Token
 *  antlr.TokenBuffer
 *  antlr.TokenStream
 *  antlr.TokenStreamException
 *  antlr.collections.impl.BitSet
 */
package org.hibernate.tool.schema.ast;

import antlr.LLkParser;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;
import org.hibernate.tool.schema.ast.GeneratedSqlScriptParserTokenTypes;

public class GeneratedSqlScriptParser
extends LLkParser
implements GeneratedSqlScriptParserTokenTypes {
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "DELIMITER", "QUOTED_TEXT", "NEWLINE", "SPACE", "TAB", "CHAR", "ESCqs", "LINE_COMMENT", "BLOCK_COMMENT"};
    public static final BitSet _tokenSet_0 = new BitSet(GeneratedSqlScriptParser.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(GeneratedSqlScriptParser.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(GeneratedSqlScriptParser.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(GeneratedSqlScriptParser.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(GeneratedSqlScriptParser.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(GeneratedSqlScriptParser.mk_tokenSet_5());

    protected void out(String stmt) {
    }

    protected void out(Token token) {
    }

    protected void statementStarted() {
    }

    protected void statementEnded() {
    }

    protected void skip() {
    }

    protected GeneratedSqlScriptParser(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        this.tokenNames = _tokenNames;
    }

    public GeneratedSqlScriptParser(TokenBuffer tokenBuf) {
        this(tokenBuf, 3);
    }

    protected GeneratedSqlScriptParser(TokenStream lexer, int k) {
        super(lexer, k);
        this.tokenNames = _tokenNames;
    }

    public GeneratedSqlScriptParser(TokenStream lexer) {
        this(lexer, 3);
    }

    public GeneratedSqlScriptParser(ParserSharedInputState state) {
        super(state, 3);
        this.tokenNames = _tokenNames;
    }

    public final void script() throws RecognitionException, TokenStreamException {
        this.traceIn("script");
        try {
            try {
                this.blankSpacesToSkip();
                while (this.LA(1) == 5 || this.LA(1) == 9) {
                    this.statement();
                    this.blankSpacesToSkip();
                }
                this.match(1);
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
        }
        finally {
            this.traceOut("script");
        }
    }

    public final void blankSpacesToSkip() throws RecognitionException, TokenStreamException {
        this.traceIn("blankSpacesToSkip");
        try {
            try {
                block10: while (true) {
                    switch (this.LA(1)) {
                        case 6: {
                            this.newLineToSkip();
                            continue block10;
                        }
                        case 7: {
                            this.spaceToSkip();
                            continue block10;
                        }
                        case 8: {
                            this.tabToSkip();
                            continue block10;
                        }
                    }
                    break;
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_1);
            }
        }
        finally {
            this.traceOut("blankSpacesToSkip");
        }
    }

    public final void statement() throws RecognitionException, TokenStreamException {
        this.traceIn("statement");
        try {
            try {
                this.statementStarted();
                this.statementFirstPart();
                while (_tokenSet_2.member(this.LA(1))) {
                    this.statementPart();
                    while (this.LA(1) == 6) {
                        this.afterStatementPartNewline();
                    }
                }
                this.match(4);
                this.statementEnded();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_3);
            }
        }
        finally {
            this.traceOut("statement");
        }
    }

    public final void statementFirstPart() throws RecognitionException, TokenStreamException {
        this.traceIn("statementFirstPart");
        try {
            try {
                switch (this.LA(1)) {
                    case 5: {
                        this.quotedString();
                        break;
                    }
                    case 9: {
                        this.nonSkippedChar();
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_4);
            }
        }
        finally {
            this.traceOut("statementFirstPart");
        }
    }

    public final void statementPart() throws RecognitionException, TokenStreamException {
        this.traceIn("statementPart");
        try {
            try {
                switch (this.LA(1)) {
                    case 5: {
                        this.quotedString();
                        break;
                    }
                    case 9: {
                        this.nonSkippedChar();
                        break;
                    }
                    case 7: {
                        this.nonSkippedSpace();
                        break;
                    }
                    case 8: {
                        this.nonSkippedTab();
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
        }
        finally {
            this.traceOut("statementPart");
        }
    }

    public final void afterStatementPartNewline() throws RecognitionException, TokenStreamException {
        this.traceIn("afterStatementPartNewline");
        try {
            Token n = null;
            try {
                n = this.LT(1);
                this.match(6);
                this.out(" ");
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
        }
        finally {
            this.traceOut("afterStatementPartNewline");
        }
    }

    public final void quotedString() throws RecognitionException, TokenStreamException {
        this.traceIn("quotedString");
        try {
            Token q = null;
            try {
                q = this.LT(1);
                this.match(5);
                this.out(q);
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
        }
        finally {
            this.traceOut("quotedString");
        }
    }

    public final void nonSkippedChar() throws RecognitionException, TokenStreamException {
        this.traceIn("nonSkippedChar");
        try {
            Token c = null;
            try {
                c = this.LT(1);
                this.match(9);
                this.out(c);
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
        }
        finally {
            this.traceOut("nonSkippedChar");
        }
    }

    public final void nonSkippedSpace() throws RecognitionException, TokenStreamException {
        this.traceIn("nonSkippedSpace");
        try {
            Token s = null;
            try {
                s = this.LT(1);
                this.match(7);
                this.out(s);
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
        }
        finally {
            this.traceOut("nonSkippedSpace");
        }
    }

    public final void nonSkippedTab() throws RecognitionException, TokenStreamException {
        this.traceIn("nonSkippedTab");
        try {
            Token t = null;
            try {
                t = this.LT(1);
                this.match(8);
                this.out(t);
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
        }
        finally {
            this.traceOut("nonSkippedTab");
        }
    }

    public final void newLineToSkip() throws RecognitionException, TokenStreamException {
        this.traceIn("newLineToSkip");
        try {
            try {
                this.match(6);
                this.skip();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_3);
            }
        }
        finally {
            this.traceOut("newLineToSkip");
        }
    }

    public final void spaceToSkip() throws RecognitionException, TokenStreamException {
        this.traceIn("spaceToSkip");
        try {
            try {
                this.match(7);
                this.skip();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_3);
            }
        }
        finally {
            this.traceOut("spaceToSkip");
        }
    }

    public final void tabToSkip() throws RecognitionException, TokenStreamException {
        this.traceIn("tabToSkip");
        try {
            try {
                this.match(8);
                this.skip();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_3);
            }
        }
        finally {
            this.traceOut("tabToSkip");
        }
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[]{2L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[]{546L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[]{928L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[]{994L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[]{944L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[]{1008L, 0L};
        return data;
    }
}

