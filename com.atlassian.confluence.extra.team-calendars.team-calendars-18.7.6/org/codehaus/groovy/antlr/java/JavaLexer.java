/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import groovyjarjarantlr.ANTLRHashString;
import groovyjarjarantlr.ByteBuffer;
import groovyjarjarantlr.CharBuffer;
import groovyjarjarantlr.CharScanner;
import groovyjarjarantlr.CharStreamException;
import groovyjarjarantlr.InputBuffer;
import groovyjarjarantlr.LexerSharedInputState;
import groovyjarjarantlr.NoViableAltForCharException;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import org.codehaus.groovy.antlr.java.JavaRecognizer;
import org.codehaus.groovy.antlr.java.JavaTokenTypes;

public class JavaLexer
extends CharScanner
implements JavaTokenTypes,
TokenStream {
    protected static final int SCS_TYPE = 3;
    protected static final int SCS_VAL = 4;
    protected static final int SCS_LIT = 8;
    protected static final int SCS_LIMIT = 16;
    protected static final int SCS_SQ_TYPE = 0;
    protected static final int SCS_TQ_TYPE = 1;
    protected static final int SCS_RE_TYPE = 2;
    protected int stringCtorState = 0;
    protected int lastSigTokenType = 1;
    private boolean assertEnabled = true;
    private boolean enumEnabled = true;
    private boolean whitespaceIncluded = false;
    protected JavaRecognizer parser;
    public static final BitSet _tokenSet_0 = new BitSet(JavaLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(JavaLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(JavaLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(JavaLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(JavaLexer.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(JavaLexer.mk_tokenSet_5());

    public void enableAssert(boolean shouldEnable) {
        this.assertEnabled = shouldEnable;
    }

    public boolean isAssertEnabled() {
        return this.assertEnabled;
    }

    public void enableEnum(boolean shouldEnable) {
        this.enumEnabled = shouldEnable;
    }

    public boolean isEnumEnabled() {
        return this.enumEnabled;
    }

    public TokenStream plumb() {
        return new TokenStream(){

            @Override
            public Token nextToken() throws TokenStreamException {
                if (JavaLexer.this.stringCtorState >= 8) {
                    int quoteType = JavaLexer.this.stringCtorState & 3;
                    JavaLexer.this.stringCtorState = 0;
                    JavaLexer.this.resetText();
                }
                Token token = JavaLexer.this.nextToken();
                int lasttype = token.getType();
                if (JavaLexer.this.whitespaceIncluded) {
                    switch (lasttype) {
                        case 165: 
                        case 166: 
                        case 167: {
                            lasttype = JavaLexer.this.lastSigTokenType;
                        }
                    }
                }
                JavaLexer.this.lastSigTokenType = lasttype;
                return token;
            }
        };
    }

    public JavaLexer(InputStream in) {
        this(new ByteBuffer(in));
    }

    public JavaLexer(Reader in) {
        this(new CharBuffer(in));
    }

    public JavaLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public JavaLexer(LexerSharedInputState state) {
        super(state);
        this.caseSensitiveLiterals = true;
        this.setCaseSensitive(true);
        this.literals = new Hashtable();
        this.literals.put(new ANTLRHashString("byte", this), new Integer(79));
        this.literals.put(new ANTLRHashString("public", this), new Integer(88));
        this.literals.put(new ANTLRHashString("case", this), new Integer(122));
        this.literals.put(new ANTLRHashString("short", this), new Integer(81));
        this.literals.put(new ANTLRHashString("break", this), new Integer(115));
        this.literals.put(new ANTLRHashString("while", this), new Integer(113));
        this.literals.put(new ANTLRHashString("new", this), new Integer(158));
        this.literals.put(new ANTLRHashString("instanceof", this), new Integer(145));
        this.literals.put(new ANTLRHashString("implements", this), new Integer(106));
        this.literals.put(new ANTLRHashString("synchronized", this), new Integer(93));
        this.literals.put(new ANTLRHashString("float", this), new Integer(83));
        this.literals.put(new ANTLRHashString("package", this), new Integer(61));
        this.literals.put(new ANTLRHashString("return", this), new Integer(117));
        this.literals.put(new ANTLRHashString("throw", this), new Integer(119));
        this.literals.put(new ANTLRHashString("null", this), new Integer(157));
        this.literals.put(new ANTLRHashString("threadsafe", this), new Integer(92));
        this.literals.put(new ANTLRHashString("protected", this), new Integer(89));
        this.literals.put(new ANTLRHashString("class", this), new Integer(101));
        this.literals.put(new ANTLRHashString("throws", this), new Integer(108));
        this.literals.put(new ANTLRHashString("do", this), new Integer(114));
        this.literals.put(new ANTLRHashString("strictfp", this), new Integer(40));
        this.literals.put(new ANTLRHashString("super", this), new Integer(71));
        this.literals.put(new ANTLRHashString("transient", this), new Integer(90));
        this.literals.put(new ANTLRHashString("native", this), new Integer(91));
        this.literals.put(new ANTLRHashString("interface", this), new Integer(102));
        this.literals.put(new ANTLRHashString("final", this), new Integer(38));
        this.literals.put(new ANTLRHashString("if", this), new Integer(111));
        this.literals.put(new ANTLRHashString("double", this), new Integer(85));
        this.literals.put(new ANTLRHashString("volatile", this), new Integer(94));
        this.literals.put(new ANTLRHashString("assert", this), new Integer(120));
        this.literals.put(new ANTLRHashString("catch", this), new Integer(125));
        this.literals.put(new ANTLRHashString("try", this), new Integer(123));
        this.literals.put(new ANTLRHashString("enum", this), new Integer(103));
        this.literals.put(new ANTLRHashString("int", this), new Integer(82));
        this.literals.put(new ANTLRHashString("for", this), new Integer(121));
        this.literals.put(new ANTLRHashString("extends", this), new Integer(70));
        this.literals.put(new ANTLRHashString("boolean", this), new Integer(78));
        this.literals.put(new ANTLRHashString("char", this), new Integer(80));
        this.literals.put(new ANTLRHashString("private", this), new Integer(87));
        this.literals.put(new ANTLRHashString("default", this), new Integer(105));
        this.literals.put(new ANTLRHashString("false", this), new Integer(156));
        this.literals.put(new ANTLRHashString("this", this), new Integer(107));
        this.literals.put(new ANTLRHashString("static", this), new Integer(64));
        this.literals.put(new ANTLRHashString("abstract", this), new Integer(39));
        this.literals.put(new ANTLRHashString("continue", this), new Integer(116));
        this.literals.put(new ANTLRHashString("finally", this), new Integer(124));
        this.literals.put(new ANTLRHashString("else", this), new Integer(112));
        this.literals.put(new ANTLRHashString("import", this), new Integer(63));
        this.literals.put(new ANTLRHashString("void", this), new Integer(77));
        this.literals.put(new ANTLRHashString("switch", this), new Integer(118));
        this.literals.put(new ANTLRHashString("true", this), new Integer(155));
        this.literals.put(new ANTLRHashString("long", this), new Integer(84));
    }

    /*
     * Exception decompiling
     */
    @Override
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [23[DOLOOP]], but top level block is 1[TRYBLOCK]
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

    public final void mQUESTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 69;
        this.match('?');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 96;
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
        int _ttype = 97;
        this.match(')');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 65;
        this.match('[');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 66;
        this.match(']');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 99;
        this.match('{');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 100;
        this.match('}');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 110;
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
        int _ttype = 74;
        this.match(',');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 98;
        this.match('=');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 142;
        this.match("==");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 154;
        this.match('!');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 153;
        this.match('~');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNOT_EQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 141;
        this.match("!=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 149;
        this.match('/');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mDIV_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 129;
        this.match("/=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 147;
        this.match('+');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mPLUS_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 126;
        this.match("+=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mINC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 151;
        this.match("++");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 148;
        this.match('-');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMINUS_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 127;
        this.match("-=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mDEC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 152;
        this.match("--");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 86;
        this.match('*');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSTAR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 128;
        this.match("*=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 150;
        this.match('%');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMOD_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 130;
        this.match("%=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 75;
        this.match(">>");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 131;
        this.match(">>=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBSR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 76;
        this.match(">>>");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBSR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 132;
        this.match(">>>=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 144;
        this.match(">=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 73;
        this.match(">");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 146;
        this.match("<<");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSL_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 133;
        this.match("<<=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 143;
        this.match("<=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 72;
        this.match('<');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBXOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 140;
        this.match('^');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBXOR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 135;
        this.match("^=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 139;
        this.match('|');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBOR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 136;
        this.match("|=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 137;
        this.match("||");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 104;
        this.match('&');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mBAND_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 134;
        this.match("&=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 138;
        this.match("&&");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSEMI(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 62;
        this.match(';');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 165;
        int _cnt370 = 0;
        block6: while (true) {
            switch (this.LA(1)) {
                case ' ': {
                    this.match(' ');
                    break;
                }
                case '\t': {
                    this.match('\t');
                    break;
                }
                case '\f': {
                    this.match('\f');
                    break;
                }
                case '\n': 
                case '\r': {
                    if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                        this.match("\r\n");
                    } else if (this.LA(1) == '\r') {
                        this.match('\r');
                    } else if (this.LA(1) == '\n') {
                        this.match('\n');
                    } else {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    if (this.inputState.guessing != 0) break;
                    this.newline();
                    break;
                }
                default: {
                    if (_cnt370 >= 1) break block6;
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            ++_cnt370;
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

    public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 166;
        this.match("//");
        while (_tokenSet_0.member(this.LA(1))) {
            this.match(_tokenSet_0);
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

    public final void mML_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 167;
        this.match("/*");
        while (true) {
            if (this.LA(1) == '\r' && this.LA(2) == '\n' && this.LA(3) >= '\u0003' && this.LA(3) <= '\uffff' && this.LA(4) >= '\u0003' && this.LA(4) <= '\uffff') {
                this.match('\r');
                this.match('\n');
                if (this.inputState.guessing != 0) continue;
                this.newline();
                continue;
            }
            if (this.LA(1) == '*' && this.LA(2) >= '\u0003' && this.LA(2) <= '\uffff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\uffff' && this.LA(2) != '/') {
                this.match('*');
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\uffff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\uffff') {
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
            if (!_tokenSet_1.member(this.LA(1))) break;
            this.match(_tokenSet_1);
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

    public final void mCHAR_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 160;
        this.match('\'');
        if (this.LA(1) == '\\') {
            this.mESC(false);
        } else if (_tokenSet_2.member(this.LA(1))) {
            this.match(_tokenSet_2);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.match('\'');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mESC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 168;
        this.match('\\');
        switch (this.LA(1)) {
            case 'n': {
                this.match('n');
                break;
            }
            case 'r': {
                this.match('r');
                break;
            }
            case 't': {
                this.match('t');
                break;
            }
            case 'b': {
                this.match('b');
                break;
            }
            case 'f': {
                this.match('f');
                break;
            }
            case '\"': {
                this.match('\"');
                break;
            }
            case '\'': {
                this.match('\'');
                break;
            }
            case '\\': {
                this.match('\\');
                break;
            }
            case 'u': {
                int _cnt389 = 0;
                while (true) {
                    if (this.LA(1) != 'u') {
                        if (_cnt389 >= 1) break;
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    this.match('u');
                    ++_cnt389;
                }
                this.mHEX_DIGIT(false);
                this.mHEX_DIGIT(false);
                this.mHEX_DIGIT(false);
                this.mHEX_DIGIT(false);
                break;
            }
            case '0': 
            case '1': 
            case '2': 
            case '3': {
                this.matchRange('0', '3');
                if (this.LA(1) >= '0' && this.LA(1) <= '7' && _tokenSet_3.member(this.LA(2))) {
                    this.matchRange('0', '7');
                    if (this.LA(1) >= '0' && this.LA(1) <= '7' && _tokenSet_3.member(this.LA(2))) {
                        this.matchRange('0', '7');
                        break;
                    }
                    if (_tokenSet_3.member(this.LA(1))) break;
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                if (_tokenSet_3.member(this.LA(1))) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case '4': 
            case '5': 
            case '6': 
            case '7': {
                this.matchRange('4', '7');
                if (this.LA(1) >= '0' && this.LA(1) <= '7' && _tokenSet_3.member(this.LA(2))) {
                    this.matchRange('0', '7');
                    break;
                }
                if (_tokenSet_3.member(this.LA(1))) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
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

    public final void mSTRING_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 161;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
                continue;
            }
            if (!_tokenSet_4.member(this.LA(1))) break;
            this.match(_tokenSet_4);
        }
        this.match('\"');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 169;
        switch (this.LA(1)) {
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
            case 'A': 
            case 'B': 
            case 'C': 
            case 'D': 
            case 'E': 
            case 'F': {
                this.matchRange('A', 'F');
                break;
            }
            case 'a': 
            case 'b': 
            case 'c': 
            case 'd': 
            case 'e': 
            case 'f': {
                this.matchRange('a', 'f');
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

    protected final void mVOCAB(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 170;
        this.matchRange('\u0003', '\u00ff');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 67;
        switch (this.LA(1)) {
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
            case 'A': 
            case 'B': 
            case 'C': 
            case 'D': 
            case 'E': 
            case 'F': 
            case 'G': 
            case 'H': 
            case 'I': 
            case 'J': 
            case 'K': 
            case 'L': 
            case 'M': 
            case 'N': 
            case 'O': 
            case 'P': 
            case 'Q': 
            case 'R': 
            case 'S': 
            case 'T': 
            case 'U': 
            case 'V': 
            case 'W': 
            case 'X': 
            case 'Y': 
            case 'Z': {
                this.matchRange('A', 'Z');
                break;
            }
            case '_': {
                this.match('_');
                break;
            }
            case '$': {
                this.match('$');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        block13: while (true) {
            switch (this.LA(1)) {
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
                    continue block13;
                }
                case 'A': 
                case 'B': 
                case 'C': 
                case 'D': 
                case 'E': 
                case 'F': 
                case 'G': 
                case 'H': 
                case 'I': 
                case 'J': 
                case 'K': 
                case 'L': 
                case 'M': 
                case 'N': 
                case 'O': 
                case 'P': 
                case 'Q': 
                case 'R': 
                case 'S': 
                case 'T': 
                case 'U': 
                case 'V': 
                case 'W': 
                case 'X': 
                case 'Y': 
                case 'Z': {
                    this.matchRange('A', 'Z');
                    continue block13;
                }
                case '_': {
                    this.match('_');
                    continue block13;
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
                    continue block13;
                }
                case '$': {
                    this.match('$');
                    continue block13;
                }
            }
            break;
        }
        if (this.inputState.guessing == 0) {
            if (this.assertEnabled && "assert".equals(new String(this.text.getBuffer(), _begin, this.text.length() - _begin))) {
                _ttype = 120;
            }
            if (this.enumEnabled && "enum".equals(new String(this.text.getBuffer(), _begin, this.text.length() - _begin))) {
                _ttype = 103;
            }
        }
        _ttype = this.testLiteralsTable(_ttype);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 159;
        Token f1 = null;
        Token f2 = null;
        Token f3 = null;
        Token f4 = null;
        boolean isDecimal = false;
        Token t = null;
        block1 : switch (this.LA(1)) {
            case '.': {
                this.match('.');
                if (this.inputState.guessing == 0) {
                    _ttype = 68;
                }
                switch (this.LA(1)) {
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
                        int _cnt404 = 0;
                        while (true) {
                            if (this.LA(1) < '0' || this.LA(1) > '9') {
                                if (_cnt404 >= 1) break;
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                            this.matchRange('0', '9');
                            ++_cnt404;
                        }
                        if (this.LA(1) == 'E' || this.LA(1) == 'e') {
                            this.mEXPONENT(false);
                        }
                        if (this.LA(1) == 'D' || this.LA(1) == 'F' || this.LA(1) == 'd' || this.LA(1) == 'f') {
                            this.mFLOAT_SUFFIX(true);
                            f1 = this._returnToken;
                            if (this.inputState.guessing == 0) {
                                t = f1;
                            }
                        }
                        if (this.inputState.guessing != 0) break block1;
                        if (t != null && t.getText().toUpperCase().indexOf(70) >= 0) {
                            _ttype = 162;
                            break;
                        }
                        _ttype = 164;
                        break;
                    }
                    case '.': {
                        this.match("..");
                        if (this.inputState.guessing != 0) break block1;
                        _ttype = 109;
                        break;
                    }
                }
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
                block9 : switch (this.LA(1)) {
                    case '0': {
                        this.match('0');
                        if (this.inputState.guessing == 0) {
                            isDecimal = true;
                        }
                        if (this.LA(1) == 'X' || this.LA(1) == 'x') {
                            switch (this.LA(1)) {
                                case 'x': {
                                    this.match('x');
                                    break;
                                }
                                case 'X': {
                                    this.match('X');
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                }
                            }
                            int _cnt412 = 0;
                            while (true) {
                                if (!_tokenSet_5.member(this.LA(1))) {
                                    if (_cnt412 >= 1) break block9;
                                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                }
                                this.mHEX_DIGIT(false);
                                ++_cnt412;
                            }
                        }
                        boolean synPredMatched417 = false;
                        if (this.LA(1) >= '0' && this.LA(1) <= '9') {
                            int _m417 = this.mark();
                            synPredMatched417 = true;
                            ++this.inputState.guessing;
                            try {
                                int _cnt415 = 0;
                                while (true) {
                                    if (this.LA(1) < '0' || this.LA(1) > '9') {
                                        if (_cnt415 >= 1) break;
                                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                    }
                                    this.matchRange('0', '9');
                                    ++_cnt415;
                                }
                                switch (this.LA(1)) {
                                    case '.': {
                                        this.match('.');
                                        break;
                                    }
                                    case 'E': 
                                    case 'e': {
                                        this.mEXPONENT(false);
                                        break;
                                    }
                                    case 'D': 
                                    case 'F': 
                                    case 'd': 
                                    case 'f': {
                                        this.mFLOAT_SUFFIX(false);
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                    }
                                }
                            }
                            catch (RecognitionException pe) {
                                synPredMatched417 = false;
                            }
                            this.rewind(_m417);
                            --this.inputState.guessing;
                        }
                        if (synPredMatched417) {
                            int _cnt419 = 0;
                            while (true) {
                                if (this.LA(1) < '0' || this.LA(1) > '9') {
                                    if (_cnt419 >= 1) break block9;
                                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                }
                                this.matchRange('0', '9');
                                ++_cnt419;
                            }
                        }
                        if (this.LA(1) < '0' || this.LA(1) > '7') break;
                        int _cnt421 = 0;
                        while (true) {
                            if (this.LA(1) < '0' || this.LA(1) > '7') {
                                if (_cnt421 >= 1) break block9;
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                            this.matchRange('0', '7');
                            ++_cnt421;
                        }
                    }
                    case '1': 
                    case '2': 
                    case '3': 
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': 
                    case '8': 
                    case '9': {
                        this.matchRange('1', '9');
                        while (this.LA(1) >= '0' && this.LA(1) <= '9') {
                            this.matchRange('0', '9');
                        }
                        if (this.inputState.guessing != 0) break;
                        isDecimal = true;
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                if (this.LA(1) == 'L' || this.LA(1) == 'l') {
                    switch (this.LA(1)) {
                        case 'l': {
                            this.match('l');
                            break;
                        }
                        case 'L': {
                            this.match('L');
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    if (this.inputState.guessing != 0) break;
                    _ttype = 163;
                    break;
                }
                if (this.LA(1) != '.' && this.LA(1) != 'D' && this.LA(1) != 'E' && this.LA(1) != 'F' && this.LA(1) != 'd' && this.LA(1) != 'e' && this.LA(1) != 'f' || !isDecimal) break;
                switch (this.LA(1)) {
                    case '.': {
                        this.match('.');
                        while (this.LA(1) >= '0' && this.LA(1) <= '9') {
                            this.matchRange('0', '9');
                        }
                        if (this.LA(1) == 'E' || this.LA(1) == 'e') {
                            this.mEXPONENT(false);
                        }
                        if (this.LA(1) != 'D' && this.LA(1) != 'F' && this.LA(1) != 'd' && this.LA(1) != 'f') break;
                        this.mFLOAT_SUFFIX(true);
                        f2 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f2;
                        break;
                    }
                    case 'E': 
                    case 'e': {
                        this.mEXPONENT(false);
                        if (this.LA(1) != 'D' && this.LA(1) != 'F' && this.LA(1) != 'd' && this.LA(1) != 'f') break;
                        this.mFLOAT_SUFFIX(true);
                        f3 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f3;
                        break;
                    }
                    case 'D': 
                    case 'F': 
                    case 'd': 
                    case 'f': {
                        this.mFLOAT_SUFFIX(true);
                        f4 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f4;
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                if (this.inputState.guessing != 0) break;
                if (t != null && t.getText().toUpperCase().indexOf(70) >= 0) {
                    _ttype = 162;
                    break;
                }
                _ttype = 164;
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

    protected final void mEXPONENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 171;
        switch (this.LA(1)) {
            case 'e': {
                this.match('e');
                break;
            }
            case 'E': {
                this.match('E');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        switch (this.LA(1)) {
            case '+': {
                this.match('+');
                break;
            }
            case '-': {
                this.match('-');
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
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        int _cnt438 = 0;
        while (true) {
            if (this.LA(1) < '0' || this.LA(1) > '9') {
                if (_cnt438 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.matchRange('0', '9');
            ++_cnt438;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mFLOAT_SUFFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 172;
        switch (this.LA(1)) {
            case 'f': {
                this.match('f');
                break;
            }
            case 'F': {
                this.match('F');
                break;
            }
            case 'd': {
                this.match('d');
                break;
            }
            case 'D': {
                this.match('D');
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

    public final void mAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 95;
        this.match('@');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[2048];
        data[0] = -9224L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[2048];
        data[0] = -4398046520328L;
        for (int i = 1; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[2048];
        data[0] = -549755823112L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[2048];
        data[0] = -9224L;
        for (int i = 1; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[2048];
        data[0] = -17179878408L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[1025];
        data[0] = 0x3FF000000000000L;
        data[1] = 0x7E0000007EL;
        return data;
    }
}

