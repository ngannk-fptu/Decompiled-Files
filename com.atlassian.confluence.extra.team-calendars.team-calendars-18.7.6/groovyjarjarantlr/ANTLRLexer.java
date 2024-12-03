/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ANTLRHashString;
import groovyjarjarantlr.ANTLRTokenTypes;
import groovyjarjarantlr.ByteBuffer;
import groovyjarjarantlr.CharBuffer;
import groovyjarjarantlr.CharScanner;
import groovyjarjarantlr.CharStreamException;
import groovyjarjarantlr.CommonToken;
import groovyjarjarantlr.InputBuffer;
import groovyjarjarantlr.LexerSharedInputState;
import groovyjarjarantlr.NoViableAltForCharException;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.StringUtils;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

public class ANTLRLexer
extends CharScanner
implements ANTLRTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(ANTLRLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(ANTLRLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(ANTLRLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(ANTLRLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(ANTLRLexer.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(ANTLRLexer.mk_tokenSet_5());

    public static int escapeCharValue(String string) {
        if (string.charAt(1) != '\\') {
            return 0;
        }
        switch (string.charAt(2)) {
            case 'b': {
                return 8;
            }
            case 'r': {
                return 13;
            }
            case 't': {
                return 9;
            }
            case 'n': {
                return 10;
            }
            case 'f': {
                return 12;
            }
            case '\"': {
                return 34;
            }
            case '\'': {
                return 39;
            }
            case '\\': {
                return 92;
            }
            case 'u': {
                if (string.length() != 8) {
                    return 0;
                }
                return Character.digit(string.charAt(3), 16) * 16 * 16 * 16 + Character.digit(string.charAt(4), 16) * 16 * 16 + Character.digit(string.charAt(5), 16) * 16 + Character.digit(string.charAt(6), 16);
            }
            case '0': 
            case '1': 
            case '2': 
            case '3': {
                if (string.length() > 5 && Character.isDigit(string.charAt(4))) {
                    return (string.charAt(2) - 48) * 8 * 8 + (string.charAt(3) - 48) * 8 + (string.charAt(4) - 48);
                }
                if (string.length() > 4 && Character.isDigit(string.charAt(3))) {
                    return (string.charAt(2) - 48) * 8 + (string.charAt(3) - 48);
                }
                return string.charAt(2) - 48;
            }
            case '4': 
            case '5': 
            case '6': 
            case '7': {
                if (string.length() > 4 && Character.isDigit(string.charAt(3))) {
                    return (string.charAt(2) - 48) * 8 + (string.charAt(3) - 48);
                }
                return string.charAt(2) - 48;
            }
        }
        return 0;
    }

    public static int tokenTypeForCharLiteral(String string) {
        if (string.length() > 3) {
            return ANTLRLexer.escapeCharValue(string);
        }
        return string.charAt(1);
    }

    public ANTLRLexer(InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }

    public ANTLRLexer(Reader reader) {
        this(new CharBuffer(reader));
    }

    public ANTLRLexer(InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }

    public ANTLRLexer(LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.caseSensitiveLiterals = true;
        this.setCaseSensitive(true);
        this.literals = new Hashtable();
        this.literals.put(new ANTLRHashString("public", this), new Integer(31));
        this.literals.put(new ANTLRHashString("class", this), new Integer(10));
        this.literals.put(new ANTLRHashString("header", this), new Integer(5));
        this.literals.put(new ANTLRHashString("throws", this), new Integer(37));
        this.literals.put(new ANTLRHashString("lexclass", this), new Integer(9));
        this.literals.put(new ANTLRHashString("catch", this), new Integer(40));
        this.literals.put(new ANTLRHashString("private", this), new Integer(32));
        this.literals.put(new ANTLRHashString("options", this), new Integer(51));
        this.literals.put(new ANTLRHashString("extends", this), new Integer(11));
        this.literals.put(new ANTLRHashString("protected", this), new Integer(30));
        this.literals.put(new ANTLRHashString("TreeParser", this), new Integer(13));
        this.literals.put(new ANTLRHashString("Parser", this), new Integer(29));
        this.literals.put(new ANTLRHashString("Lexer", this), new Integer(12));
        this.literals.put(new ANTLRHashString("returns", this), new Integer(35));
        this.literals.put(new ANTLRHashString("charVocabulary", this), new Integer(18));
        this.literals.put(new ANTLRHashString("tokens", this), new Integer(4));
        this.literals.put(new ANTLRHashString("exception", this), new Integer(39));
    }

    /*
     * Exception decompiling
     */
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [31[DOLOOP]], but top level block is 1[TRYBLOCK]
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

    public final void mWS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 52;
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
        n2 = -1;
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCOMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 53;
        Token token2 = null;
        if (this.LA(1) == '/' && this.LA(2) == '/') {
            this.mSL_COMMENT(false);
        } else if (this.LA(1) == '/' && this.LA(2) == '*') {
            this.mML_COMMENT(true);
            token2 = this._returnToken;
            n2 = token2.getType();
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (n2 != 8) {
            n2 = -1;
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mSL_COMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 54;
        this.match("//");
        while (_tokenSet_0.member(this.LA(1))) {
            this.match(_tokenSet_0);
        }
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
        this.newline();
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mML_COMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 55;
        this.match("/*");
        if (this.LA(1) == '*' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(2) != '/') {
            this.match('*');
            n2 = 8;
        } else if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        while (this.LA(1) != '*' || this.LA(2) != '/') {
            if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                this.match('\r');
                this.match('\n');
                this.newline();
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match('\r');
                this.newline();
                continue;
            }
            if (_tokenSet_0.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match(_tokenSet_0);
                continue;
            }
            if (this.LA(1) != '\n') break;
            this.match('\n');
            this.newline();
        }
        this.match("*/");
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mOPEN_ELEMENT_OPTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 25;
        this.match('<');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCLOSE_ELEMENT_OPTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 26;
        this.match('>');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCOMMA(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 38;
        this.match(',');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mQUESTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 45;
        this.match('?');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mTREE_BEGIN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 44;
        this.match("#(");
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mLPAREN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 27;
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
        int n2 = 28;
        this.match(')');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCOLON(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 36;
        this.match(':');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mSTAR(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 46;
        this.match('*');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mPLUS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 47;
        this.match('+');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mASSIGN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 15;
        this.match('=');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mIMPLIES(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 48;
        this.match("=>");
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mSEMI(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 16;
        this.match(';');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCARET(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 49;
        this.match('^');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mBANG(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 33;
        this.match('!');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mOR(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 21;
        this.match('|');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mWILDCARD(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 50;
        this.match('.');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mRANGE(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 22;
        this.match("..");
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mNOT_OP(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 42;
        this.match('~');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mRCURLY(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 17;
        this.match('}');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mCHAR_LITERAL(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 19;
        this.match('\'');
        if (this.LA(1) == '\\') {
            this.mESC(false);
        } else if (_tokenSet_1.member(this.LA(1))) {
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

    protected final void mESC(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 56;
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
                if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.matchRange('0', '7');
                    if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                        this.matchRange('0', '7');
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
                if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.matchRange('0', '7');
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

    public final void mSTRING_LITERAL(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 6;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
                continue;
            }
            if (!_tokenSet_2.member(this.LA(1))) break;
            this.matchNot('\"');
        }
        this.match('\"');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mXDIGIT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 58;
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

    protected final void mDIGIT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 57;
        this.matchRange('0', '9');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mINT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 20;
        int n3 = 0;
        while (true) {
            if (this.LA(1) < '0' || this.LA(1) > '9') {
                if (n3 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.matchRange('0', '9');
            ++n3;
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
        int n2 = 34;
        this.mNESTED_ARG_ACTION(false);
        this.setText(StringUtils.stripFrontBack(this.getText(), "[", "]"));
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mNESTED_ARG_ACTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 59;
        this.match('[');
        block6: while (true) {
            switch (this.LA(1)) {
                case '[': {
                    this.mNESTED_ARG_ACTION(false);
                    continue block6;
                }
                case '\n': {
                    this.match('\n');
                    this.newline();
                    continue block6;
                }
                case '\'': {
                    this.mCHAR_LITERAL(false);
                    continue block6;
                }
                case '\"': {
                    this.mSTRING_LITERAL(false);
                    continue block6;
                }
            }
            if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                this.match('\r');
                this.match('\n');
                this.newline();
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match('\r');
                this.newline();
                continue;
            }
            if (!_tokenSet_3.member(this.LA(1))) break;
            this.matchNot(']');
        }
        this.match(']');
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
        int n3 = this.getLine();
        int n4 = this.getColumn();
        this.mNESTED_ACTION(false);
        if (this.LA(1) == '?') {
            this.match('?');
            n2 = 43;
        }
        if (n2 == 7) {
            this.setText(StringUtils.stripFrontBack(this.getText(), "{", "}"));
        } else {
            this.setText(StringUtils.stripFrontBack(this.getText(), "{", "}?"));
        }
        CommonToken commonToken = new CommonToken(n2, new String(this.text.getBuffer(), n, this.text.length() - n));
        commonToken.setLine(n3);
        commonToken.setColumn(n4);
        token = commonToken;
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mNESTED_ACTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 60;
        this.match('{');
        while (this.LA(1) != '}') {
            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match('\r');
                    this.match('\n');
                    this.newline();
                    continue;
                }
                if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.match('\r');
                    this.newline();
                    continue;
                }
                if (this.LA(1) == '\n') {
                    this.match('\n');
                    this.newline();
                    continue;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            if (this.LA(1) == '{' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mNESTED_ACTION(false);
                continue;
            }
            if (this.LA(1) == '\'' && _tokenSet_4.member(this.LA(2))) {
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

    public final void mTOKEN_REF(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 24;
        this.matchRange('A', 'Z');
        block6: while (true) {
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
                    continue block6;
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
                    continue block6;
                }
                case '_': {
                    this.match('_');
                    continue block6;
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
                    continue block6;
                }
            }
            break;
        }
        n2 = this.testLiteralsTable(n2);
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mRULE_REF(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 41;
        int n3 = 0;
        n2 = n3 = this.mINTERNAL_RULE_REF(false);
        if (n3 == 51) {
            this.mWS_LOOP(false);
            if (this.LA(1) == '{') {
                this.match('{');
                n2 = 14;
            }
        } else if (n3 == 4) {
            this.mWS_LOOP(false);
            if (this.LA(1) == '{') {
                this.match('{');
                n2 = 23;
            }
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final int mINTERNAL_RULE_REF(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 62;
        int n3 = 41;
        this.matchRange('a', 'z');
        block6: while (true) {
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
                    continue block6;
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
                    continue block6;
                }
                case '_': {
                    this.match('_');
                    continue block6;
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
                    continue block6;
                }
            }
            break;
        }
        n3 = this.testLiteralsTable(n3);
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
        return n3;
    }

    protected final void mWS_LOOP(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 61;
        block4: while (true) {
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.mWS(false);
                    continue block4;
                }
                case '/': {
                    this.mCOMMENT(false);
                    continue block4;
                }
            }
            break;
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mWS_OPT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 63;
        if (_tokenSet_5.member(this.LA(1))) {
            this.mWS(false);
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[8];
        lArray[0] = -9224L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[8];
        lArray[0] = -549755813896L;
        lArray[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_2() {
        long[] lArray = new long[8];
        lArray[0] = -17179869192L;
        lArray[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_3() {
        long[] lArray = new long[8];
        lArray[0] = -566935692296L;
        lArray[1] = -671088641L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_4() {
        long[] lArray = new long[8];
        lArray[0] = -549755813896L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_5() {
        long[] lArray = new long[]{4294977024L, 0L, 0L, 0L, 0L};
        return lArray;
    }
}

