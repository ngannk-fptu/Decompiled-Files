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
package org.hibernate.sql.ordering.antlr;

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
import org.hibernate.sql.ordering.antlr.OrderByTemplateTokenTypes;

public class GeneratedOrderByLexer
extends CharScanner
implements OrderByTemplateTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(GeneratedOrderByLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(GeneratedOrderByLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(GeneratedOrderByLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(GeneratedOrderByLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(GeneratedOrderByLexer.mk_tokenSet_4());

    public GeneratedOrderByLexer(InputStream in) {
        this((InputBuffer)new ByteBuffer(in));
    }

    public GeneratedOrderByLexer(Reader in) {
        this((InputBuffer)new CharBuffer(in));
    }

    public GeneratedOrderByLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public GeneratedOrderByLexer(LexerSharedInputState state) {
        super(state);
        this.caseSensitiveLiterals = false;
        this.setCaseSensitive(false);
        this.literals = new Hashtable();
        this.literals.put(new ANTLRHashString("asc", (CharScanner)this), new Integer(14));
        this.literals.put(new ANTLRHashString("desc", (CharScanner)this), new Integer(15));
        this.literals.put(new ANTLRHashString("descending", (CharScanner)this), new Integer(30));
        this.literals.put(new ANTLRHashString("nulls", (CharScanner)this), new Integer(16));
        this.literals.put(new ANTLRHashString("collate", (CharScanner)this), new Integer(13));
        this.literals.put(new ANTLRHashString("ascending", (CharScanner)this), new Integer(29));
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

    public final void mOPEN_PAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 22;
        this.match('(');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCLOSE_PAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 23;
        this.match(')');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 19;
        this.match(',');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mHARD_QUOTE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 20;
        this.match('`');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 21;
        this.mID_START_LETTER(false);
        while (_tokenSet_1.member((int)this.LA(1))) {
            this.mID_LETTER(false);
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
        int _ttype = 31;
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
        int _ttype = 32;
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
        int _ttype = 28;
        this.match('\'');
        while (true) {
            boolean synPredMatched47 = false;
            if (this.LA(1) == '\'' && this.LA(2) == '\'') {
                int _m47 = this.mark();
                synPredMatched47 = true;
                ++this.inputState.guessing;
                try {
                    this.mESCqs(false);
                }
                catch (RecognitionException pe) {
                    synPredMatched47 = false;
                }
                this.rewind(_m47);
                --this.inputState.guessing;
            }
            if (synPredMatched47) {
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
        int _ttype = 33;
        this.match('\'');
        this.match('\'');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 26;
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
                    _ttype = 10;
                }
                if (this.LA(1) < '0' || this.LA(1) > '9') break;
                int _cnt53 = 0;
                while (true) {
                    if (this.LA(1) < '0' || this.LA(1) > '9') {
                        if (_cnt53 >= 1) break;
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    this.matchRange('0', '9');
                    ++_cnt53;
                }
                if (this.LA(1) == 'e') {
                    this.mEXPONENT(false);
                }
                if (this.LA(1) == 'd' || this.LA(1) == 'f') {
                    this.mFLOAT_SUFFIX(true);
                    f1 = this._returnToken;
                    if (this.inputState.guessing == 0) {
                        t = f1;
                    }
                }
                if (this.inputState.guessing != 0) break;
                if (t != null && t.getText().toUpperCase().indexOf(70) >= 0) {
                    _ttype = 25;
                    break;
                }
                _ttype = 24;
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
                                int _cnt60 = 0;
                                while (true) {
                                    if (!_tokenSet_3.member((int)this.LA(1))) {
                                        if (_cnt60 >= 1) break block8;
                                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                    }
                                    this.mHEX_DIGIT(false);
                                    ++_cnt60;
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
                                int _cnt62 = 0;
                                while (true) {
                                    if (this.LA(1) < '0' || this.LA(1) > '7') {
                                        if (_cnt62 >= 1) break block8;
                                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                    }
                                    this.matchRange('0', '7');
                                    ++_cnt62;
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
                if (this.LA(1) == 'l') {
                    this.match('l');
                    if (this.inputState.guessing != 0) break;
                    _ttype = 27;
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
                        if (this.LA(1) != 'd' && this.LA(1) != 'f') break;
                        this.mFLOAT_SUFFIX(true);
                        f2 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f2;
                        break;
                    }
                    case 'e': {
                        this.mEXPONENT(false);
                        if (this.LA(1) != 'd' && this.LA(1) != 'f') break;
                        this.mFLOAT_SUFFIX(true);
                        f3 = this._returnToken;
                        if (this.inputState.guessing != 0) break;
                        t = f3;
                        break;
                    }
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
                    _ttype = 25;
                    break;
                }
                _ttype = 24;
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
        int _ttype = 35;
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
        int _cnt80 = 0;
        while (true) {
            if (this.LA(1) < '0' || this.LA(1) > '9') {
                if (_cnt80 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.matchRange('0', '9');
            ++_cnt80;
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
        int _ttype = 36;
        switch (this.LA(1)) {
            case 'f': {
                this.match('f');
                break;
            }
            case 'd': {
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
        int _ttype = 34;
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

    public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 37;
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
        data[1] = 0x7000000000L;
        return data;
    }
}

