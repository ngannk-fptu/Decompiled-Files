/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.actions.python;

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
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.actions.python.CodeLexerTokenTypes;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;

public class CodeLexer
extends CharScanner
implements CodeLexerTokenTypes,
TokenStream {
    protected int lineOffset = 0;
    private Tool antlrTool;
    public static final BitSet _tokenSet_0 = new BitSet(CodeLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(CodeLexer.mk_tokenSet_1());

    public CodeLexer(String string, String string2, int n, Tool tool) {
        this(new StringReader(string));
        this.setLine(n);
        this.setFilename(string2);
        this.antlrTool = tool;
    }

    public void setLineOffset(int n) {
        this.setLine(n);
    }

    public void reportError(RecognitionException recognitionException) {
        this.antlrTool.error("Syntax error in action: " + recognitionException, this.getFilename(), this.getLine(), this.getColumn());
    }

    public void reportError(String string) {
        this.antlrTool.error(string, this.getFilename(), this.getLine(), this.getColumn());
    }

    public void reportWarning(String string) {
        if (this.getFilename() == null) {
            this.antlrTool.warning(string);
        } else {
            this.antlrTool.warning(string, this.getFilename(), this.getLine(), this.getColumn());
        }
    }

    public CodeLexer(InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }

    public CodeLexer(Reader reader) {
        this(new CharBuffer(reader));
    }

    public CodeLexer(InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }

    public CodeLexer(LexerSharedInputState lexerSharedInputState) {
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
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[DOLOOP]], but top level block is 1[TRYBLOCK]
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

    public final void mACTION(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 4;
        while (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') {
            this.mSTUFF(false);
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mSTUFF(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 5;
        if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
            this.mCOMMENT(false);
        } else if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match("\r\n");
            this.newline();
        } else if (this.LA(1) == '/' && _tokenSet_0.member(this.LA(2))) {
            this.match('/');
            this.match(_tokenSet_0);
        } else if (this.LA(1) == '\r') {
            this.match('\r');
            this.newline();
        } else if (this.LA(1) == '\n') {
            this.match('\n');
            this.newline();
        } else if (_tokenSet_1.member(this.LA(1))) {
            this.match(_tokenSet_1);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mCOMMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 6;
        if (this.LA(1) == '/' && this.LA(2) == '/') {
            this.mSL_COMMENT(false);
        } else if (this.LA(1) == '/' && this.LA(2) == '*') {
            this.mML_COMMENT(false);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
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
        int n2 = 7;
        int n3 = this.text.length();
        this.match("//");
        this.text.setLength(n3);
        this.text.append("#");
        while (this.LA(1) != '\n' && this.LA(1) != '\r' && this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.matchNot('\uffff');
        }
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match("\r\n");
        } else if (this.LA(1) == '\n') {
            this.match('\n');
        } else if (this.LA(1) == '\r') {
            this.match('\r');
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
        int n2 = 9;
        boolean bl2 = false;
        int n3 = this.text.length();
        this.match("/*");
        this.text.setLength(n3);
        this.text.append("#");
        while (this.LA(1) != '*' || this.LA(2) != '/') {
            if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                this.match('\r');
                this.match('\n');
                n3 = this.text.length();
                this.mIGNWS(false);
                this.text.setLength(n3);
                this.newline();
                this.text.append("# ");
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match('\r');
                n3 = this.text.length();
                this.mIGNWS(false);
                this.text.setLength(n3);
                this.newline();
                this.text.append("# ");
                continue;
            }
            if (this.LA(1) == '\n' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match('\n');
                n3 = this.text.length();
                this.mIGNWS(false);
                this.text.setLength(n3);
                this.newline();
                this.text.append("# ");
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.text.append("\n");
        n3 = this.text.length();
        this.match("*/");
        this.text.setLength(n3);
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mIGNWS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 8;
        while (true) {
            if (this.LA(1) == ' ' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match(' ');
                continue;
            }
            if (this.LA(1) != '\t' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') break;
            this.match('\t');
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[8];
        lArray[0] = -145135534866440L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[8];
        lArray[0] = -140737488364552L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }
}

