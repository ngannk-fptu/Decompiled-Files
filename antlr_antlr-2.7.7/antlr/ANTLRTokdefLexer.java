/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRTokdefParserTokenTypes;
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

public class ANTLRTokdefLexer
extends CharScanner
implements ANTLRTokdefParserTokenTypes,
TokenStream {
    public static final BitSet _tokenSet_0 = new BitSet(ANTLRTokdefLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(ANTLRTokdefLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(ANTLRTokdefLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(ANTLRTokdefLexer.mk_tokenSet_3());

    public ANTLRTokdefLexer(InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }

    public ANTLRTokdefLexer(Reader reader) {
        this(new CharBuffer(reader));
    }

    public ANTLRTokdefLexer(InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }

    public ANTLRTokdefLexer(LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
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

    public final void mWS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 10;
        switch (this.LA(1)) {
            case ' ': {
                this.match(' ');
                break;
            }
            case '\t': {
                this.match('\t');
                break;
            }
            case '\r': {
                this.match('\r');
                if (this.LA(1) == '\n') {
                    this.match('\n');
                }
                this.newline();
                break;
            }
            case '\n': {
                this.match('\n');
                this.newline();
                break;
            }
            default: {
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

    public final void mSL_COMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 11;
        this.match("//");
        while (_tokenSet_0.member(this.LA(1))) {
            this.match(_tokenSet_0);
        }
        switch (this.LA(1)) {
            case '\n': {
                this.match('\n');
                break;
            }
            case '\r': {
                this.match('\r');
                if (this.LA(1) != '\n') break;
                this.match('\n');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        n2 = -1;
        this.newline();
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mML_COMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 12;
        this.match("/*");
        while (true) {
            if (this.LA(1) == '*' && _tokenSet_1.member(this.LA(2))) {
                this.match('*');
                this.matchNot('/');
                continue;
            }
            if (this.LA(1) == '\n') {
                this.match('\n');
                this.newline();
                continue;
            }
            if (!_tokenSet_2.member(this.LA(1))) break;
            this.matchNot('*');
        }
        this.match("*/");
        n2 = -1;
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mLPAREN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 7;
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
        int n2 = 8;
        this.match(')');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mASSIGN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 6;
        this.match('=');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    public final void mSTRING(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 5;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
                continue;
            }
            if (!_tokenSet_3.member(this.LA(1))) break;
            this.matchNot('\"');
        }
        this.match('\"');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mESC(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 13;
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
        int n2 = 14;
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
        int n2 = 15;
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

    public final void mID(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 4;
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
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        block10: while (true) {
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
                    continue block10;
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
                    continue block10;
                }
                case '_': {
                    this.match('_');
                    continue block10;
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
                    continue block10;
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

    public final void mINT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 9;
        int n3 = 0;
        while (true) {
            if (this.LA(1) < '0' || this.LA(1) > '9') {
                if (n3 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mDIGIT(false);
            ++n3;
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
        lArray[0] = -140737488355336L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_2() {
        long[] lArray = new long[8];
        lArray[0] = -4398046512136L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_3() {
        long[] lArray = new long[8];
        lArray[0] = -17179869192L;
        lArray[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }
}

