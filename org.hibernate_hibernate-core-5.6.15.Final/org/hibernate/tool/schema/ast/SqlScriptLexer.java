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
package org.hibernate.tool.schema.ast;

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
import org.hibernate.tool.schema.ast.GeneratedSqlScriptParserTokenTypes;

public class SqlScriptLexer
extends CharScanner
implements GeneratedSqlScriptParserTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(SqlScriptLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(SqlScriptLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(SqlScriptLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(SqlScriptLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(SqlScriptLexer.mk_tokenSet_4());

    public SqlScriptLexer(InputStream in) {
        this((InputBuffer)new ByteBuffer(in));
    }

    public SqlScriptLexer(Reader in) {
        this((InputBuffer)new CharBuffer(in));
    }

    public SqlScriptLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public SqlScriptLexer(LexerSharedInputState state) {
        super(state);
        this.caseSensitiveLiterals = true;
        this.setCaseSensitive(true);
        this.literals = new Hashtable();
    }

    /*
     * Exception decompiling
     */
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [10[DOLOOP]], but top level block is 1[TRYBLOCK]
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

    public final void mDELIMITER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 4;
        this.match(';');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mQUOTED_TEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 5;
        switch (this.LA(1)) {
            case '`': {
                this.match('`');
                while (_tokenSet_1.member((int)this.LA(1))) {
                    this.match(_tokenSet_1);
                }
                this.match('`');
                break;
            }
            case '\'': {
                this.match('\'');
                while (true) {
                    boolean synPredMatched826 = false;
                    if (this.LA(1) == '\'' && this.LA(2) == '\'') {
                        int _m826 = this.mark();
                        synPredMatched826 = true;
                        ++this.inputState.guessing;
                        try {
                            this.mESCqs(false);
                        }
                        catch (RecognitionException pe) {
                            synPredMatched826 = false;
                        }
                        this.rewind(_m826);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched826) {
                        this.mESCqs(false);
                        continue;
                    }
                    if (!_tokenSet_2.member((int)this.LA(1))) break;
                    this.match(_tokenSet_2);
                }
                this.match('\'');
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

    protected final void mESCqs(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 10;
        this.match('\'');
        this.match('\'');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 9;
        this.match(_tokenSet_0);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSPACE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 7;
        this.match(' ');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mTAB(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 8;
        this.match('\t');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNEWLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 6;
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match('\r');
            this.match('\n');
        } else if (this.LA(1) == '\r') {
            this.match('\r');
        } else if (this.LA(1) == '\n') {
            this.match('\n');
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLINE_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 11;
        switch (this.LA(1)) {
            case '/': {
                this.match("//");
                break;
            }
            case '-': {
                this.match("--");
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        while (_tokenSet_3.member((int)this.LA(1))) {
            this.match(_tokenSet_3);
        }
        if (this.inputState.guessing == 0) {
            _ttype = -1;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBLOCK_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 12;
        this.match("/*");
        while (true) {
            if (this.LA(1) == '*' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && this.LA(2) != '/') {
                this.match('*');
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                this.match('\r');
                this.match('\n');
                if (this.inputState.guessing != 0) continue;
                this.newline();
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe') {
                this.match('\r');
                if (this.inputState.guessing != 0) continue;
                this.newline();
                continue;
            }
            if (this.LA(1) == '\n') {
                this.match('\n');
                if (this.inputState.guessing != 0) continue;
                this.newline();
                continue;
            }
            if (!_tokenSet_4.member((int)this.LA(1))) break;
            this.match(_tokenSet_4);
        }
        this.match("*/");
        if (this.inputState.guessing == 0) {
            _ttype = -1;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[2048];
        data[0] = -576460756598400513L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[2048];
        data[0] = -1L;
        data[1] = -4294967297L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[2048];
        data[0] = -549755813889L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[2048];
        data[0] = -9217L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[2048];
        data[0] = -4398046520321L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
}

