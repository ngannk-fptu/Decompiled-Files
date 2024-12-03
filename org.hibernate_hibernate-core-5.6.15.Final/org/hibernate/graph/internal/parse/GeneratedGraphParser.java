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
package org.hibernate.graph.internal.parse;

import antlr.LLkParser;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;
import org.hibernate.graph.internal.parse.HEGLTokenTypes;

public class GeneratedGraphParser
extends LLkParser
implements HEGLTokenTypes {
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "COMMA", "NAME", "DOT", "LPAREN", "COLON", "RPAREN", "WHITESPACE", "NAME_START", "NAME_CONTINUATION"};
    public static final BitSet _tokenSet_0 = new BitSet(GeneratedGraphParser.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(GeneratedGraphParser.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(GeneratedGraphParser.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(GeneratedGraphParser.mk_tokenSet_3());

    protected void startAttribute(Token attributeName) {
    }

    protected void startQualifiedAttribute(Token attributeName, Token qualifier) {
    }

    protected void finishAttribute() {
    }

    protected void startSubGraph(Token subType) {
    }

    protected void finishSubGraph() {
    }

    protected GeneratedGraphParser(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        this.tokenNames = _tokenNames;
    }

    public GeneratedGraphParser(TokenBuffer tokenBuf) {
        this(tokenBuf, 2);
    }

    protected GeneratedGraphParser(TokenStream lexer, int k) {
        super(lexer, k);
        this.tokenNames = _tokenNames;
    }

    public GeneratedGraphParser(TokenStream lexer) {
        this(lexer, 2);
    }

    public GeneratedGraphParser(ParserSharedInputState state) {
        super(state, 2);
        this.tokenNames = _tokenNames;
    }

    public final void graph() throws RecognitionException, TokenStreamException {
        this.traceIn("graph");
        try {
            try {
                this.attributeNode();
                while (this.LA(1) == 4) {
                    this.match(4);
                    this.attributeNode();
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
        }
        finally {
            this.traceOut("graph");
        }
    }

    public final void attributeNode() throws RecognitionException, TokenStreamException {
        this.traceIn("attributeNode");
        try {
            try {
                this.attributePath();
                switch (this.LA(1)) {
                    case 7: {
                        this.subGraph();
                        break;
                    }
                    case 1: 
                    case 4: 
                    case 9: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.finishAttribute();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_1);
            }
        }
        finally {
            this.traceOut("attributeNode");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void attributePath() throws RecognitionException, TokenStreamException {
        block10: {
            this.traceIn("attributePath");
            try {
                Token path = null;
                Token qualifier = null;
                try {
                    path = this.LT(1);
                    this.match(5);
                    switch (this.LA(1)) {
                        case 6: {
                            this.match(6);
                            qualifier = this.LT(1);
                            this.match(5);
                            break;
                        }
                        case 1: 
                        case 4: 
                        case 7: 
                        case 9: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    if (qualifier == null) {
                        this.startAttribute(path);
                        break block10;
                    }
                    this.startQualifiedAttribute(path, qualifier);
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_2);
                }
            }
            finally {
                this.traceOut("attributePath");
            }
        }
    }

    public final void subGraph() throws RecognitionException, TokenStreamException {
        this.traceIn("subGraph");
        try {
            Token subtype = null;
            try {
                this.match(7);
                if (this.LA(1) == 5 && this.LA(2) == 8) {
                    subtype = this.LT(1);
                    this.match(5);
                    this.match(8);
                } else if (this.LA(1) != 5 || !_tokenSet_3.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                this.startSubGraph(subtype);
                this.attributeNode();
                while (this.LA(1) == 4) {
                    this.match(4);
                    this.attributeNode();
                }
                this.match(9);
                this.finishSubGraph();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_1);
            }
        }
        finally {
            this.traceOut("subGraph");
        }
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[]{2L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[]{530L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[]{658L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[]{720L, 0L};
        return data;
    }
}

