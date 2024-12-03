/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ANTLRHashString
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
package org.hibernate.hql.internal.antlr;

import antlr.ANTLRHashString;
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
import org.hibernate.hql.internal.antlr.HqlTokenTypes;

public class HqlBaseLexer
extends CharScanner
implements HqlTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(HqlBaseLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(HqlBaseLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(HqlBaseLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(HqlBaseLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(HqlBaseLexer.mk_tokenSet_4());

    protected void setPossibleID(boolean possibleID) {
    }

    public HqlBaseLexer(InputStream in) {
        this((InputBuffer)new ByteBuffer(in));
    }

    public HqlBaseLexer(Reader in) {
        this((InputBuffer)new CharBuffer(in));
    }

    public HqlBaseLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public HqlBaseLexer(LexerSharedInputState state) {
        super(state);
        this.caseSensitiveLiterals = false;
        this.setCaseSensitive(false);
        this.literals = new Hashtable();
        this.literals.put(new ANTLRHashString("between", (CharScanner)this), new Integer(10));
        this.literals.put(new ANTLRHashString("case", (CharScanner)this), new Integer(57));
        this.literals.put(new ANTLRHashString("delete", (CharScanner)this), new Integer(13));
        this.literals.put(new ANTLRHashString("new", (CharScanner)this), new Integer(38));
        this.literals.put(new ANTLRHashString("end", (CharScanner)this), new Integer(58));
        this.literals.put(new ANTLRHashString("object", (CharScanner)this), new Integer(68));
        this.literals.put(new ANTLRHashString("insert", (CharScanner)this), new Integer(30));
        this.literals.put(new ANTLRHashString("distinct", (CharScanner)this), new Integer(16));
        this.literals.put(new ANTLRHashString("where", (CharScanner)this), new Integer(53));
        this.literals.put(new ANTLRHashString("trailing", (CharScanner)this), new Integer(70));
        this.literals.put(new ANTLRHashString("then", (CharScanner)this), new Integer(60));
        this.literals.put(new ANTLRHashString("select", (CharScanner)this), new Integer(46));
        this.literals.put(new ANTLRHashString("and", (CharScanner)this), new Integer(6));
        this.literals.put(new ANTLRHashString("outer", (CharScanner)this), new Integer(43));
        this.literals.put(new ANTLRHashString("not", (CharScanner)this), new Integer(39));
        this.literals.put(new ANTLRHashString("fetch", (CharScanner)this), new Integer(21));
        this.literals.put(new ANTLRHashString("from", (CharScanner)this), new Integer(23));
        this.literals.put(new ANTLRHashString("null", (CharScanner)this), new Integer(40));
        this.literals.put(new ANTLRHashString("count", (CharScanner)this), new Integer(12));
        this.literals.put(new ANTLRHashString("like", (CharScanner)this), new Integer(35));
        this.literals.put(new ANTLRHashString("when", (CharScanner)this), new Integer(61));
        this.literals.put(new ANTLRHashString("class", (CharScanner)this), new Integer(11));
        this.literals.put(new ANTLRHashString("inner", (CharScanner)this), new Integer(29));
        this.literals.put(new ANTLRHashString("leading", (CharScanner)this), new Integer(66));
        this.literals.put(new ANTLRHashString("with", (CharScanner)this), new Integer(63));
        this.literals.put(new ANTLRHashString("set", (CharScanner)this), new Integer(47));
        this.literals.put(new ANTLRHashString("escape", (CharScanner)this), new Integer(18));
        this.literals.put(new ANTLRHashString("join", (CharScanner)this), new Integer(33));
        this.literals.put(new ANTLRHashString("elements", (CharScanner)this), new Integer(17));
        this.literals.put(new ANTLRHashString("of", (CharScanner)this), new Integer(69));
        this.literals.put(new ANTLRHashString("is", (CharScanner)this), new Integer(32));
        this.literals.put(new ANTLRHashString("member", (CharScanner)this), new Integer(67));
        this.literals.put(new ANTLRHashString("or", (CharScanner)this), new Integer(41));
        this.literals.put(new ANTLRHashString("any", (CharScanner)this), new Integer(5));
        this.literals.put(new ANTLRHashString("full", (CharScanner)this), new Integer(24));
        this.literals.put(new ANTLRHashString("min", (CharScanner)this), new Integer(37));
        this.literals.put(new ANTLRHashString("as", (CharScanner)this), new Integer(7));
        this.literals.put(new ANTLRHashString("by", (CharScanner)this), new Integer(112));
        this.literals.put(new ANTLRHashString("nulls", (CharScanner)this), new Integer(54));
        this.literals.put(new ANTLRHashString("all", (CharScanner)this), new Integer(4));
        this.literals.put(new ANTLRHashString("order", (CharScanner)this), new Integer(42));
        this.literals.put(new ANTLRHashString("both", (CharScanner)this), new Integer(64));
        this.literals.put(new ANTLRHashString("some", (CharScanner)this), new Integer(48));
        this.literals.put(new ANTLRHashString("properties", (CharScanner)this), new Integer(44));
        this.literals.put(new ANTLRHashString("fk", (CharScanner)this), new Integer(129));
        this.literals.put(new ANTLRHashString("ascending", (CharScanner)this), new Integer(113));
        this.literals.put(new ANTLRHashString("descending", (CharScanner)this), new Integer(114));
        this.literals.put(new ANTLRHashString("false", (CharScanner)this), new Integer(20));
        this.literals.put(new ANTLRHashString("exists", (CharScanner)this), new Integer(19));
        this.literals.put(new ANTLRHashString("asc", (CharScanner)this), new Integer(8));
        this.literals.put(new ANTLRHashString("left", (CharScanner)this), new Integer(34));
        this.literals.put(new ANTLRHashString("desc", (CharScanner)this), new Integer(14));
        this.literals.put(new ANTLRHashString("max", (CharScanner)this), new Integer(36));
        this.literals.put(new ANTLRHashString("empty", (CharScanner)this), new Integer(65));
        this.literals.put(new ANTLRHashString("sum", (CharScanner)this), new Integer(49));
        this.literals.put(new ANTLRHashString("on", (CharScanner)this), new Integer(62));
        this.literals.put(new ANTLRHashString("into", (CharScanner)this), new Integer(31));
        this.literals.put(new ANTLRHashString("else", (CharScanner)this), new Integer(59));
        this.literals.put(new ANTLRHashString("right", (CharScanner)this), new Integer(45));
        this.literals.put(new ANTLRHashString("versioned", (CharScanner)this), new Integer(52));
        this.literals.put(new ANTLRHashString("in", (CharScanner)this), new Integer(27));
        this.literals.put(new ANTLRHashString("avg", (CharScanner)this), new Integer(9));
        this.literals.put(new ANTLRHashString("update", (CharScanner)this), new Integer(51));
        this.literals.put(new ANTLRHashString("true", (CharScanner)this), new Integer(50));
        this.literals.put(new ANTLRHashString("group", (CharScanner)this), new Integer(25));
        this.literals.put(new ANTLRHashString("having", (CharScanner)this), new Integer(26));
        this.literals.put(new ANTLRHashString("indices", (CharScanner)this), new Integer(28));
    }

    /*
     * Exception decompiling
     */
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [24[DOLOOP]], but top level block is 1[TRYBLOCK]
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

    public final void mEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 108;
        this.match('=');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 117;
        this.match('<');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 118;
        this.match('>');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSQL_NE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 116;
        this.match("<>");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 115;
        switch (this.LA(1)) {
            case '!': {
                this.match("!=");
                break;
            }
            case '^': {
                this.match("^=");
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

    public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 119;
        this.match("<=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 120;
        this.match(">=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 107;
        this.match(',');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mOPEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 109;
        this.match('(');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCLOSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 110;
        this.match(')');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mOPEN_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 127;
        this.match('[');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCLOSE_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 128;
        this.match(']');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCONCAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 121;
        this.match("||");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 122;
        this.match('+');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 123;
        this.match('-');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 124;
        this.match('*');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 125;
        this.match('/');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 126;
        this.match('%');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 131;
        this.match(':');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mPARAM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 132;
        this.match('?');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 111;
        this.mID_START_LETTER(false);
        while (_tokenSet_1.member((int)this.LA(1))) {
            this.mID_LETTER(false);
        }
        if (this.inputState.guessing == 0) {
            this.setPossibleID(true);
        }
        _ttype = this.testLiteralsTable(_ttype);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mID_START_LETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 134;
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
                if (this.LA(1) >= '\u0080' && this.LA(1) <= '\ufffe') {
                    this.matchRange('\u0080', '\ufffe');
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mID_LETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 135;
        if (_tokenSet_0.member((int)this.LA(1))) {
            this.mID_START_LETTER(false);
        } else if (this.LA(1) >= '0' && this.LA(1) <= '9') {
            this.matchRange('0', '9');
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mQUOTED_STRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 130;
        this.match('\'');
        while (true) {
            boolean synPredMatched331 = false;
            if (this.LA(1) == '\'' && this.LA(2) == '\'') {
                int _m331 = this.mark();
                synPredMatched331 = true;
                ++this.inputState.guessing;
                try {
                    this.mESCqs(false);
                }
                catch (RecognitionException pe) {
                    synPredMatched331 = false;
                }
                this.rewind(_m331);
                --this.inputState.guessing;
            }
            if (synPredMatched331) {
                this.mESCqs(false);
                continue;
            }
            if (!_tokenSet_2.member((int)this.LA(1))) break;
            this.matchNot('\'');
        }
        this.match('\'');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mESCqs(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 136;
        this.match('\'');
        this.match('\'');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 137;
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
                if (this.inputState.guessing != 0) break;
                this.newline();
                break;
            }
            default: {
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match('\r');
                    this.match('\n');
                    if (this.inputState.guessing != 0) break;
                    this.newline();
                    break;
                }
                if (this.LA(1) == '\r') {
                    this.match('\r');
                    if (this.inputState.guessing != 0) break;
                    this.newline();
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
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

    public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 133;
        Token f1 = null;
        Token f2 = null;
        Token f3 = null;
        Token f4 = null;
        boolean isDecimal = false;
        Token t = null;
        switch (this.LA(1)) {
            case '.': {
                this.match('.');
                if (this.inputState.guessing == 0) {
                    _ttype = 15;
                }
                if (this.LA(1) < '0' || this.LA(1) > '9') break;
                int _cnt339 = 0;
                while (true) {
                    if (this.LA(1) < '0' || this.LA(1) > '9') {
                        if (_cnt339 >= 1) break;
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    this.matchRange('0', '9');
                    ++_cnt339;
                }
                if (this.LA(1) == 'e') {
                    this.mEXPONENT(false);
                }
                if (this.LA(1) == 'b' || this.LA(1) == 'd' || this.LA(1) == 'f') {
                    this.mFLOAT_SUFFIX(true);
                    f1 = this._returnToken;
                    if (this.inputState.guessing == 0) {
                        t = f1;
                    }
                }
                if (this.inputState.guessing != 0) break;
                if (t != null && t.getText().toUpperCase().indexOf("BD") >= 0) {
                    _ttype = 105;
                    break;
                }
                if (t != null && t.getText().toUpperCase().indexOf(70) >= 0) {
                    _ttype = 102;
                    break;
                }
                _ttype = 101;
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
                switch (this.LA(1)) {
                    case '0': {
                        this.match('0');
                        if (this.inputState.guessing == 0) {
                            isDecimal = true;
                        }
                        block8 : switch (this.LA(1)) {
                            case 'x': {
                                this.match('x');
                                int _cnt346 = 0;
                                while (true) {
                                    if (!_tokenSet_3.member((int)this.LA(1))) {
                                        if (_cnt346 >= 1) break block8;
                                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                    }
                                    this.mHEX_DIGIT(false);
                                    ++_cnt346;
                                }
                            }
                            case '0': 
                            case '1': 
                            case '2': 
                            case '3': 
                            case '4': 
                            case '5': 
                            case '6': 
                            case '7': {
                                int _cnt348 = 0;
                                while (true) {
                                    if (this.LA(1) < '0' || this.LA(1) > '7') {
                                        if (_cnt348 >= 1) break block8;
                                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                    }
                                    this.matchRange('0', '7');
                                    ++_cnt348;
                                }
                            }
                        }
                        break;
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
                if (this.LA(1) == 'b' && this.LA(2) == 'i') {
                    this.match('b');
                    this.match('i');
                    if (this.inputState.guessing != 0) break;
                    _ttype = 104;
                    break;
                }
                if (this.LA(1) == 'l') {
                    this.match('l');
                    if (this.inputState.guessing != 0) break;
                    _ttype = 103;
                    break;
                }
                if (!_tokenSet_4.member((int)this.LA(1)) || !isDecimal) break;
                switch (this.LA(1)) {
                    case '.': {
                        this.match('.');
                        while (this.LA(1) >= '0' && this.LA(1) <= '9') {
                            this.matchRange('0', '9');
                        }
                        if (this.LA(1) == 'e') {
                            this.mEXPONENT(false);
                        }
                        if (this.LA(1) != 'b' && this.LA(1) != 'd' && this.LA(1) != 'f') break;
                        this.mFLOAT_SUFFIX(true);
                        f2 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f2;
                        break;
                    }
                    case 'e': {
                        this.mEXPONENT(false);
                        if (this.LA(1) != 'b' && this.LA(1) != 'd' && this.LA(1) != 'f') break;
                        this.mFLOAT_SUFFIX(true);
                        f3 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f3;
                        break;
                    }
                    case 'b': 
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
                if (t != null && t.getText().toUpperCase().indexOf("BD") >= 0) {
                    _ttype = 105;
                    break;
                }
                if (t != null && t.getText().toUpperCase().indexOf(70) >= 0) {
                    _ttype = 102;
                    break;
                }
                _ttype = 101;
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
        int _ttype = 139;
        this.match('e');
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
        int _cnt367 = 0;
        while (true) {
            if (this.LA(1) < '0' || this.LA(1) > '9') {
                if (_cnt367 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.matchRange('0', '9');
            ++_cnt367;
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
        int _ttype = 140;
        switch (this.LA(1)) {
            case 'f': {
                this.match('f');
                break;
            }
            case 'd': {
                this.match('d');
                break;
            }
            case 'b': {
                this.match('b');
                this.match('d');
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

    protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 138;
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

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[3072];
        data[0] = 0x1000000000L;
        data[1] = 576460745860972544L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[3072];
        data[0] = 287948969894477824L;
        data[1] = 576460745860972544L;
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
        long[] data = new long[1025];
        data[0] = 0x3FF000000000000L;
        data[1] = 0x7E00000000L;
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[1025];
        data[0] = 0x400000000000L;
        data[1] = 0x7400000000L;
        return data;
    }
}

