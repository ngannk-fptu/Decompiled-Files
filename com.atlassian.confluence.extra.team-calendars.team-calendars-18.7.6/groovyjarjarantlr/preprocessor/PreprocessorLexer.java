/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.preprocessor;

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
import groovyjarjarantlr.preprocessor.PreprocessorTokenTypes;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

public class PreprocessorLexer
extends CharScanner
implements PreprocessorTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(PreprocessorLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(PreprocessorLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(PreprocessorLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(PreprocessorLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(PreprocessorLexer.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(PreprocessorLexer.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(PreprocessorLexer.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(PreprocessorLexer.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(PreprocessorLexer.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(PreprocessorLexer.mk_tokenSet_9());
    public static final BitSet _tokenSet_10 = new BitSet(PreprocessorLexer.mk_tokenSet_10());

    public PreprocessorLexer(InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }

    public PreprocessorLexer(Reader reader) {
        this(new CharBuffer(reader));
    }

    public PreprocessorLexer(InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }

    public PreprocessorLexer(LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.caseSensitiveLiterals = true;
        this.setCaseSensitive(true);
        this.literals = new Hashtable();
        this.literals.put(new ANTLRHashString("public", this), new Integer(18));
        this.literals.put(new ANTLRHashString("class", this), new Integer(8));
        this.literals.put(new ANTLRHashString("throws", this), new Integer(23));
        this.literals.put(new ANTLRHashString("catch", this), new Integer(26));
        this.literals.put(new ANTLRHashString("private", this), new Integer(17));
        this.literals.put(new ANTLRHashString("extends", this), new Integer(10));
        this.literals.put(new ANTLRHashString("protected", this), new Integer(16));
        this.literals.put(new ANTLRHashString("returns", this), new Integer(21));
        this.literals.put(new ANTLRHashString("tokens", this), new Integer(4));
        this.literals.put(new ANTLRHashString("exception", this), new Integer(25));
    }

    /*
     * Exception decompiling
     */
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [20[DOLOOP]], but top level block is 1[TRYBLOCK]
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

    public final void mRULE_BLOCK(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        int n;
        Token token = null;
        int n2 = this.text.length();
        int n3 = 22;
        this.match(':');
        if (_tokenSet_1.member(this.LA(1)) && _tokenSet_2.member(this.LA(2))) {
            n = this.text.length();
            this.mWS(false);
            this.text.setLength(n);
        } else if (!_tokenSet_2.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.mALT(false);
        switch (this.LA(1)) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                n = this.text.length();
                this.mWS(false);
                this.text.setLength(n);
                break;
            }
            case ';': 
            case '|': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        block8: while (this.LA(1) == '|') {
            this.match('|');
            if (_tokenSet_1.member(this.LA(1)) && _tokenSet_2.member(this.LA(2))) {
                n = this.text.length();
                this.mWS(false);
                this.text.setLength(n);
            } else if (!_tokenSet_2.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mALT(false);
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    n = this.text.length();
                    this.mWS(false);
                    this.text.setLength(n);
                    continue block8;
                }
                case ';': 
                case '|': {
                    continue block8;
                }
            }
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.match(';');
        if (bl && token == null && n3 != -1) {
            token = this.makeToken(n3);
            token.setText(new String(this.text.getBuffer(), n2, this.text.length() - n2));
        }
        this._returnToken = token;
    }

    public final void mWS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 33;
        int n3 = 0;
        while (true) {
            if (this.LA(1) == ' ') {
                this.match(' ');
            } else if (this.LA(1) == '\t') {
                this.match('\t');
            } else if (this.LA(1) == '\n' || this.LA(1) == '\r') {
                this.mNEWLINE(false);
            } else {
                if (n3 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            ++n3;
        }
        n2 = -1;
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mALT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 27;
        while (_tokenSet_3.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.mELEMENT(false);
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mSUBRULE_BLOCK(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 6;
        this.match('(');
        if (_tokenSet_1.member(this.LA(1)) && _tokenSet_0.member(this.LA(2))) {
            this.mWS(false);
        } else if (!_tokenSet_0.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.mALT(false);
        while (_tokenSet_4.member(this.LA(1)) && _tokenSet_0.member(this.LA(2))) {
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '|': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            this.match('|');
            if (_tokenSet_1.member(this.LA(1)) && _tokenSet_0.member(this.LA(2))) {
                this.mWS(false);
            } else if (!_tokenSet_0.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mALT(false);
        }
        switch (this.LA(1)) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                this.mWS(false);
                break;
            }
            case ')': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        this.match(')');
        if (this.LA(1) == '=' && this.LA(2) == '>') {
            this.match("=>");
        } else if (this.LA(1) == '*') {
            this.match('*');
        } else if (this.LA(1) == '+') {
            this.match('+');
        } else if (this.LA(1) == '?') {
            this.match('?');
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mELEMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 28;
        switch (this.LA(1)) {
            case '/': {
                this.mCOMMENT(false);
                break;
            }
            case '{': {
                this.mACTION(false);
                break;
            }
            case '\"': {
                this.mSTRING_LITERAL(false);
                break;
            }
            case '\'': {
                this.mCHAR_LITERAL(false);
                break;
            }
            case '(': {
                this.mSUBRULE_BLOCK(false);
                break;
            }
            case '\n': 
            case '\r': {
                this.mNEWLINE(false);
                break;
            }
            default: {
                if (_tokenSet_5.member(this.LA(1))) {
                    this.match(_tokenSet_5);
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCOMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 35;
        if (this.LA(1) == '/' && this.LA(2) == '/') {
            this.mSL_COMMENT(false);
        } else if (this.LA(1) == '/' && this.LA(2) == '*') {
            this.mML_COMMENT(false);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        n2 = -1;
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mACTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 7;
        this.match('{');
        while (this.LA(1) != '}') {
            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mNEWLINE(false);
                continue;
            }
            if (this.LA(1) == '{' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mACTION(false);
                continue;
            }
            if (this.LA(1) == '\'' && _tokenSet_6.member(this.LA(2))) {
                this.mCHAR_LITERAL(false);
                continue;
            }
            if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
                this.mCOMMENT(false);
                continue;
            }
            if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mSTRING_LITERAL(false);
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.match('}');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mSTRING_LITERAL(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 39;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
                continue;
            }
            if (!_tokenSet_7.member(this.LA(1))) break;
            this.matchNot('\"');
        }
        this.match('\"');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCHAR_LITERAL(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 38;
        this.match('\'');
        if (this.LA(1) == '\\') {
            this.mESC(false);
        } else if (_tokenSet_8.member(this.LA(1))) {
            this.matchNot('\'');
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.match('\'');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mNEWLINE(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 34;
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match('\r');
            this.match('\n');
            this.newline();
        } else if (this.LA(1) == '\r') {
            this.match('\r');
            this.newline();
        } else if (this.LA(1) == '\n') {
            this.match('\n');
            this.newline();
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mBANG(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 19;
        this.match('!');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mSEMI(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 11;
        this.match(';');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCOMMA(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 24;
        this.match(',');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mRCURLY(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 15;
        this.match('}');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mLPAREN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 29;
        this.match('(');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mRPAREN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 30;
        this.match(')');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mID_OR_KEYWORD(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 31;
        Token token2 = null;
        this.mID(true);
        token2 = this._returnToken;
        n2 = token2.getType();
        if (_tokenSet_9.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && token2.getText().equals("header")) {
            if (_tokenSet_1.member(this.LA(1)) && _tokenSet_9.member(this.LA(2))) {
                this.mWS(false);
            } else if (!_tokenSet_9.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            switch (this.LA(1)) {
                case '\"': {
                    this.mSTRING_LITERAL(false);
                    break;
                }
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': 
                case '/': 
                case '{': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            block16: while (true) {
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        continue block16;
                    }
                    case '/': {
                        this.mCOMMENT(false);
                        continue block16;
                    }
                }
                break;
            }
            this.mACTION(false);
            n2 = 5;
        } else if (_tokenSet_10.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && token2.getText().equals("tokens")) {
            block17: while (true) {
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        continue block17;
                    }
                    case '/': {
                        this.mCOMMENT(false);
                        continue block17;
                    }
                }
                break;
            }
            this.mCURLY_BLOCK_SCARF(false);
            n2 = 12;
        } else if (_tokenSet_10.member(this.LA(1)) && token2.getText().equals("options")) {
            block18: while (true) {
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        continue block18;
                    }
                    case '/': {
                        this.mCOMMENT(false);
                        continue block18;
                    }
                }
                break;
            }
            this.match('{');
            n2 = 13;
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mID(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 9;
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
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        block11: while (true) {
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
                    continue block11;
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
                    continue block11;
                }
                case '_': {
                    this.match('_');
                    continue block11;
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
                    continue block11;
                }
            }
            break;
        }
        n2 = this.testLiteralsTable(new String(this.text.getBuffer(), n, this.text.length() - n), n2);
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mCURLY_BLOCK_SCARF(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 32;
        this.match('{');
        while (this.LA(1) != '}') {
            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mNEWLINE(false);
                continue;
            }
            if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mSTRING_LITERAL(false);
                continue;
            }
            if (this.LA(1) == '\'' && _tokenSet_6.member(this.LA(2))) {
                this.mCHAR_LITERAL(false);
                continue;
            }
            if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
                this.mCOMMENT(false);
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.match('}');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mASSIGN_RHS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 14;
        int n3 = this.text.length();
        this.match('=');
        this.text.setLength(n3);
        while (this.LA(1) != ';') {
            if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mSTRING_LITERAL(false);
                continue;
            }
            if (this.LA(1) == '\'' && _tokenSet_6.member(this.LA(2))) {
                this.mCHAR_LITERAL(false);
                continue;
            }
            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mNEWLINE(false);
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.match(';');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mSL_COMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 36;
        this.match("//");
        while (this.LA(1) != '\n' && this.LA(1) != '\r' && this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.matchNot('\uffff');
        }
        this.mNEWLINE(false);
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mML_COMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 37;
        this.match("/*");
        while (this.LA(1) != '*' || this.LA(2) != '/') {
            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mNEWLINE(false);
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.match("*/");
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mESC(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 40;
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
            case 'w': {
                this.match('w');
                break;
            }
            case 'a': {
                this.match('a');
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
            case '0': 
            case '1': 
            case '2': 
            case '3': {
                this.matchRange('0', '3');
                if (this.LA(1) >= '0' && this.LA(1) <= '9' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mDIGIT(false);
                    if (this.LA(1) >= '0' && this.LA(1) <= '9' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                        this.mDIGIT(false);
                        break;
                    }
                    if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') break;
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case '4': 
            case '5': 
            case '6': 
            case '7': {
                this.matchRange('4', '7');
                if (this.LA(1) >= '0' && this.LA(1) <= '9' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mDIGIT(false);
                    break;
                }
                if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case 'u': {
                this.match('u');
                this.mXDIGIT(false);
                this.mXDIGIT(false);
                this.mXDIGIT(false);
                this.mXDIGIT(false);
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mDIGIT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 41;
        this.matchRange('0', '9');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mXDIGIT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 42;
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
            case 'A': 
            case 'B': 
            case 'C': 
            case 'D': 
            case 'E': 
            case 'F': {
                this.matchRange('A', 'F');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mARG_ACTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 20;
        this.match('[');
        while (this.LA(1) != ']') {
            if (this.LA(1) == '[' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mARG_ACTION(false);
                continue;
            }
            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mNEWLINE(false);
                continue;
            }
            if (this.LA(1) == '\'' && _tokenSet_6.member(this.LA(2))) {
                this.mCHAR_LITERAL(false);
                continue;
            }
            if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mSTRING_LITERAL(false);
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.match(']');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[8];
        lArray[0] = -576460752303423496L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[]{4294977024L, 0L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_2() {
        long[] lArray = new long[8];
        lArray[0] = -2199023255560L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_3() {
        long[] lArray = new long[8];
        lArray[0] = -576462951326679048L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_4() {
        long[] lArray = new long[]{4294977024L, 0x1000000000000000L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_5() {
        long[] lArray = new long[8];
        lArray[0] = -576605355262354440L;
        lArray[1] = -576460752303423489L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_6() {
        long[] lArray = new long[8];
        lArray[0] = -549755813896L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_7() {
        long[] lArray = new long[8];
        lArray[0] = -17179869192L;
        lArray[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_8() {
        long[] lArray = new long[8];
        lArray[0] = -549755813896L;
        lArray[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_9() {
        long[] lArray = new long[]{140758963201536L, 0x800000000000000L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_10() {
        long[] lArray = new long[]{140741783332352L, 0x800000000000000L, 0L, 0L, 0L};
        return lArray;
    }
}

