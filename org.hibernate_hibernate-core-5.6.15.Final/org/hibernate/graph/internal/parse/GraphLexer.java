/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ByteBuffer
 *  antlr.CharBuffer
 *  antlr.CharScanner
 *  antlr.CharStreamException
 *  antlr.InputBuffer
 *  antlr.LexerSharedInputState
 *  antlr.NoViableAltForCharException
 *  antlr.RecognitionException
 *  antlr.Token
 *  antlr.TokenStream
 *  antlr.TokenStreamException
 *  antlr.collections.impl.BitSet
 */
package org.hibernate.graph.internal.parse;

import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharScanner;
import antlr.CharStreamException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import org.hibernate.graph.internal.parse.HEGLTokenTypes;

public class GraphLexer
extends CharScanner
implements HEGLTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(GraphLexer.mk_tokenSet_0());

    public GraphLexer(InputStream in) {
        this((InputBuffer)new ByteBuffer(in));
    }

    public GraphLexer(Reader in) {
        this((InputBuffer)new CharBuffer(in));
    }

    public GraphLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public GraphLexer(LexerSharedInputState state) {
        super(state);
        this.caseSensitiveLiterals = true;
        this.setCaseSensitive(false);
        this.literals = new Hashtable();
    }

    /*
     * Exception decompiling
     */
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [13[DOLOOP]], but top level block is 1[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 8;
        this.match(':');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 4;
        this.match(',');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mDOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 6;
        this.match('.');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 7;
        this.match('(');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 9;
        this.match(')');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mWHITESPACE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 10;
        switch (this.LA(1)) {
            case ' ': {
                this.match(' ');
                break;
            }
            case '\t': {
                this.match('\t');
                break;
            }
            case '\n': {
                this.match('\n');
                this.newline();
                break;
            }
            default: {
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match('\r');
                    this.match('\n');
                    this.newline();
                    break;
                }
                if (this.LA(1) == '\r') {
                    this.match('\r');
                    this.newline();
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        _ttype = -1;
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 5;
        this.mNAME_START(false);
        while (_tokenSet_0.member((int)this.LA(1))) {
            this.mNAME_CONTINUATION(false);
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mNAME_START(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 11;
        switch (this.LA(1)) {
            case '_': {
                this.match('_');
                break;
            }
            case '$': {
                this.match('$');
                break;
            }
            case 'a': 
            case 'b': 
            case 'c': 
            case 'd': 
            case 'e': 
            case 'f': 
            case 'g': 
            case 'h': 
            case 'i': 
            case 'j': 
            case 'k': 
            case 'l': 
            case 'm': 
            case 'n': 
            case 'o': 
            case 'p': 
            case 'q': 
            case 'r': 
            case 's': 
            case 't': 
            case 'u': 
            case 'v': 
            case 'w': 
            case 'x': 
            case 'y': 
            case 'z': {
                this.matchRange('a', 'z');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mNAME_CONTINUATION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 12;
        switch (this.LA(1)) {
            case '$': 
            case '_': 
            case 'a': 
            case 'b': 
            case 'c': 
            case 'd': 
            case 'e': 
            case 'f': 
            case 'g': 
            case 'h': 
            case 'i': 
            case 'j': 
            case 'k': 
            case 'l': 
            case 'm': 
            case 'n': 
            case 'o': 
            case 'p': 
            case 'q': 
            case 'r': 
            case 's': 
            case 't': 
            case 'u': 
            case 'v': 
            case 'w': 
            case 'x': 
            case 'y': 
            case 'z': {
                this.mNAME_START(false);
                break;
            }
            case '0': 
            case '1': 
            case '2': 
            case '3': 
            case '4': 
            case '5': 
            case '6': 
            case '7': 
            case '8': 
            case '9': {
                this.matchRange('0', '9');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[1025];
        data[0] = 287948969894477824L;
        data[1] = 576460745860972544L;
        return data;
    }
}

