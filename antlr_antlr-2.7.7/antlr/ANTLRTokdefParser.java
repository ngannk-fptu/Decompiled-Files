/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRTokdefParserTokenTypes;
import antlr.ImportVocabTokenManager;
import antlr.LLkParser;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.StringLiteralSymbol;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenSymbol;
import antlr.Tool;
import antlr.collections.impl.BitSet;

public class ANTLRTokdefParser
extends LLkParser
implements ANTLRTokdefParserTokenTypes {
    private Tool antlrTool;
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "ID", "STRING", "ASSIGN", "LPAREN", "RPAREN", "INT", "WS", "SL_COMMENT", "ML_COMMENT", "ESC", "DIGIT", "XDIGIT"};
    public static final BitSet _tokenSet_0 = new BitSet(ANTLRTokdefParser.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(ANTLRTokdefParser.mk_tokenSet_1());

    public void setTool(Tool tool) {
        if (this.antlrTool != null) {
            throw new IllegalStateException("antlr.Tool already registered");
        }
        this.antlrTool = tool;
    }

    protected Tool getTool() {
        return this.antlrTool;
    }

    public void reportError(String string) {
        if (this.getTool() != null) {
            this.getTool().error(string, this.getFilename(), -1, -1);
        } else {
            super.reportError(string);
        }
    }

    public void reportError(RecognitionException recognitionException) {
        if (this.getTool() != null) {
            this.getTool().error(recognitionException.getErrorMessage(), recognitionException.getFilename(), recognitionException.getLine(), recognitionException.getColumn());
        } else {
            super.reportError(recognitionException);
        }
    }

    public void reportWarning(String string) {
        if (this.getTool() != null) {
            this.getTool().warning(string, this.getFilename(), -1, -1);
        } else {
            super.reportWarning(string);
        }
    }

    protected ANTLRTokdefParser(TokenBuffer tokenBuffer, int n) {
        super(tokenBuffer, n);
        this.tokenNames = _tokenNames;
    }

    public ANTLRTokdefParser(TokenBuffer tokenBuffer) {
        this(tokenBuffer, 3);
    }

    protected ANTLRTokdefParser(TokenStream tokenStream, int n) {
        super(tokenStream, n);
        this.tokenNames = _tokenNames;
    }

    public ANTLRTokdefParser(TokenStream tokenStream) {
        this(tokenStream, 3);
    }

    public ANTLRTokdefParser(ParserSharedInputState parserSharedInputState) {
        super(parserSharedInputState, 3);
        this.tokenNames = _tokenNames;
    }

    public final void file(ImportVocabTokenManager importVocabTokenManager) throws RecognitionException, TokenStreamException {
        Token token = null;
        try {
            token = this.LT(1);
            this.match(4);
            while (this.LA(1) == 4 || this.LA(1) == 5) {
                this.line(importVocabTokenManager);
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_0);
        }
    }

    public final void line(ImportVocabTokenManager importVocabTokenManager) throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Token token6 = null;
        Token token7 = null;
        Token token8 = null;
        Token token9 = null;
        try {
            if (this.LA(1) == 5) {
                token = this.LT(1);
                this.match(5);
                token9 = token;
            } else if (this.LA(1) == 4 && this.LA(2) == 6 && this.LA(3) == 5) {
                token2 = this.LT(1);
                this.match(4);
                token8 = token2;
                this.match(6);
                token3 = this.LT(1);
                this.match(5);
                token9 = token3;
            } else if (this.LA(1) == 4 && this.LA(2) == 7) {
                token4 = this.LT(1);
                this.match(4);
                token8 = token4;
                this.match(7);
                token5 = this.LT(1);
                this.match(5);
                this.match(8);
            } else if (this.LA(1) == 4 && this.LA(2) == 6 && this.LA(3) == 9) {
                token6 = this.LT(1);
                this.match(4);
                token8 = token6;
            } else {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.match(6);
            token7 = this.LT(1);
            this.match(9);
            Integer n = Integer.valueOf(token7.getText());
            if (token9 != null) {
                importVocabTokenManager.define(token9.getText(), n);
                if (token8 != null) {
                    StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)importVocabTokenManager.getTokenSymbol(token9.getText());
                    stringLiteralSymbol.setLabel(token8.getText());
                    importVocabTokenManager.mapToTokenSymbol(token8.getText(), stringLiteralSymbol);
                }
            } else if (token8 != null) {
                importVocabTokenManager.define(token8.getText(), n);
                if (token5 != null) {
                    TokenSymbol tokenSymbol = importVocabTokenManager.getTokenSymbol(token8.getText());
                    tokenSymbol.setParaphrase(token5.getText());
                }
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_1);
        }
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[]{2L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[]{50L, 0L};
        return lArray;
    }
}

