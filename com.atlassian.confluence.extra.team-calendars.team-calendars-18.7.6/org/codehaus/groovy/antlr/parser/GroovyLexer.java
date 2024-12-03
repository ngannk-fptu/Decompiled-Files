/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.parser;

import groovyjarjarantlr.ANTLRHashString;
import groovyjarjarantlr.ByteBuffer;
import groovyjarjarantlr.CharBuffer;
import groovyjarjarantlr.CharScanner;
import groovyjarjarantlr.CharStreamException;
import groovyjarjarantlr.CharStreamIOException;
import groovyjarjarantlr.InputBuffer;
import groovyjarjarantlr.LexerSharedInputState;
import groovyjarjarantlr.MismatchedCharException;
import groovyjarjarantlr.NoViableAltForCharException;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.SemanticException;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.TokenStreamIOException;
import groovyjarjarantlr.TokenStreamRecognitionException;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import org.codehaus.groovy.antlr.GroovySourceToken;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

public class GroovyLexer
extends CharScanner
implements GroovyTokenTypes,
TokenStream {
    private boolean assertEnabled = true;
    private boolean enumEnabled = true;
    private boolean whitespaceIncluded = false;
    protected int parenLevel;
    protected int suppressNewline;
    protected static final int SCS_TYPE = 3;
    protected static final int SCS_VAL = 4;
    protected static final int SCS_LIT = 8;
    protected static final int SCS_LIMIT = 16;
    protected static final int SCS_SQ_TYPE = 0;
    protected static final int SCS_TQ_TYPE = 1;
    protected static final int SCS_RE_TYPE = 2;
    protected static final int SCS_DRE_TYPE = 3;
    protected int stringCtorState;
    protected ArrayList parenLevelStack;
    protected int lastSigTokenType;
    public static boolean tracing = false;
    private static HashMap ttypes;
    protected GroovyRecognizer parser;
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    public static final BitSet _tokenSet_3;
    public static final BitSet _tokenSet_4;
    public static final BitSet _tokenSet_5;
    public static final BitSet _tokenSet_6;
    public static final BitSet _tokenSet_7;
    public static final BitSet _tokenSet_8;
    public static final BitSet _tokenSet_9;
    public static final BitSet _tokenSet_10;
    public static final BitSet _tokenSet_11;
    public static final BitSet _tokenSet_12;
    public static final BitSet _tokenSet_13;

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

    public void setWhitespaceIncluded(boolean z) {
        this.whitespaceIncluded = z;
    }

    public boolean isWhitespaceIncluded() {
        return this.whitespaceIncluded;
    }

    @Override
    public void setTokenObjectClass(String name) {
    }

    @Override
    protected Token makeToken(int t) {
        GroovySourceToken tok = new GroovySourceToken(t);
        tok.setColumn(this.inputState.getTokenStartColumn());
        tok.setLine(this.inputState.getTokenStartLine());
        tok.setColumnLast(this.inputState.getColumn());
        tok.setLineLast(this.inputState.getLine());
        return tok;
    }

    protected void pushParenLevel() {
        this.parenLevelStack.add(this.parenLevel * 16 + this.stringCtorState);
        this.parenLevel = 0;
        this.stringCtorState = 0;
    }

    protected void popParenLevel() {
        int npl = this.parenLevelStack.size();
        if (npl == 0) {
            return;
        }
        int i = (Integer)this.parenLevelStack.remove(--npl);
        this.parenLevel = i / 16;
        this.stringCtorState = i % 16;
    }

    protected void restartStringCtor(boolean expectLiteral) {
        if (this.stringCtorState != 0) {
            this.stringCtorState = (expectLiteral ? 8 : 4) + (this.stringCtorState & 3);
        }
    }

    protected boolean allowRegexpLiteral() {
        return !GroovyLexer.isExpressionEndingToken(this.lastSigTokenType);
    }

    protected static boolean isExpressionEndingToken(int ttype) {
        switch (ttype) {
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 86: 
            case 87: 
            case 88: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 127: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 198: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                return true;
            }
        }
        return false;
    }

    protected void newlineCheck(boolean check) throws RecognitionException {
        if (check && this.suppressNewline > 0) {
            this.require(this.suppressNewline == 0, "end of line reached within a simple string 'x' or \"x\" or /x/", "for multi-line literals, use triple quotes '''x''' or \"\"\"x\"\"\" or /x/ or $/x/$");
            this.suppressNewline = 0;
        }
        this.newline();
    }

    protected boolean atValidDollarEscape() throws CharStreamException {
        char lc;
        int k = 1;
        if ((lc = this.LA(k++)) != '$') {
            return false;
        }
        return (lc = this.LA(k++)) == '{' || lc != '$' && Character.isJavaIdentifierStart(lc);
    }

    protected boolean atDollarDollarEscape() throws CharStreamException {
        return this.LA(1) == '$' && this.LA(2) == '$';
    }

    protected boolean atMultiCommentStart() throws CharStreamException {
        return this.LA(1) == '/' && this.LA(2) == '*';
    }

    protected boolean atDollarSlashEscape() throws CharStreamException {
        return this.LA(1) == '$' && this.LA(2) == '/';
    }

    public TokenStream plumb() {
        return new TokenStream(){

            @Override
            public Token nextToken() throws TokenStreamException {
                if (GroovyLexer.this.stringCtorState >= 8) {
                    int quoteType = GroovyLexer.this.stringCtorState & 3;
                    GroovyLexer.this.stringCtorState = 0;
                    GroovyLexer.this.resetText();
                    try {
                        switch (quoteType) {
                            case 0: {
                                GroovyLexer.this.mSTRING_CTOR_END(true, false, false);
                                break;
                            }
                            case 1: {
                                GroovyLexer.this.mSTRING_CTOR_END(true, false, true);
                                break;
                            }
                            case 2: {
                                GroovyLexer.this.mREGEXP_CTOR_END(true, false);
                                break;
                            }
                            case 3: {
                                GroovyLexer.this.mDOLLAR_REGEXP_CTOR_END(true, false);
                                break;
                            }
                            default: {
                                throw new AssertionError(false);
                            }
                        }
                        GroovyLexer.this.lastSigTokenType = GroovyLexer.this._returnToken.getType();
                        return GroovyLexer.this._returnToken;
                    }
                    catch (RecognitionException e) {
                        throw new TokenStreamRecognitionException(e);
                    }
                    catch (CharStreamException cse) {
                        if (cse instanceof CharStreamIOException) {
                            throw new TokenStreamIOException(((CharStreamIOException)cse).io);
                        }
                        throw new TokenStreamException(cse.getMessage());
                    }
                }
                Token token = GroovyLexer.this.nextToken();
                int lasttype = token.getType();
                if (GroovyLexer.this.whitespaceIncluded) {
                    switch (lasttype) {
                        case 207: 
                        case 208: 
                        case 209: 
                        case 210: {
                            lasttype = GroovyLexer.this.lastSigTokenType;
                        }
                    }
                }
                GroovyLexer.this.lastSigTokenType = lasttype;
                return token;
            }
        };
    }

    @Override
    public void traceIn(String rname) throws CharStreamException {
        if (!tracing) {
            return;
        }
        super.traceIn(rname);
    }

    @Override
    public void traceOut(String rname) throws CharStreamException {
        if (!tracing) {
            return;
        }
        if (this._returnToken != null) {
            rname = rname + GroovyLexer.tokenStringOf(this._returnToken);
        }
        super.traceOut(rname);
    }

    private static String tokenStringOf(Token t) {
        Integer tt;
        Object ttn;
        if (ttypes == null) {
            HashMap<Object, String> map = new HashMap<Object, String>();
            Field[] fields = GroovyTokenTypes.class.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                if (fields[i].getType() != Integer.TYPE) continue;
                try {
                    map.put(fields[i].get(null), fields[i].getName());
                    continue;
                }
                catch (IllegalAccessException illegalAccessException) {
                    // empty catch block
                }
            }
            ttypes = map;
        }
        if ((ttn = ttypes.get(tt = Integer.valueOf(t.getType()))) == null) {
            ttn = "<" + tt + ">";
        }
        return "[" + ttn + ",\"" + t.getText() + "\"]";
    }

    private void require(boolean z, String problem, String solution) throws SemanticException {
        if (!z && this.parser != null) {
            this.parser.requireFailed(problem, solution);
        }
        if (!z) {
            int lineNum = this.inputState.getLine();
            int colNum = this.inputState.getColumn();
            throw new SemanticException(problem + ";\n   solution: " + solution, this.getFilename(), lineNum, colNum);
        }
    }

    public GroovyLexer(InputStream in) {
        this(new ByteBuffer(in));
    }

    public GroovyLexer(Reader in) {
        this(new CharBuffer(in));
    }

    public GroovyLexer(InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }

    public GroovyLexer(LexerSharedInputState state) {
        super(state);
        this.setTabSize(1);
        this.parenLevel = 0;
        this.suppressNewline = 0;
        this.stringCtorState = 0;
        this.parenLevelStack = new ArrayList();
        this.lastSigTokenType = 1;
        this.caseSensitiveLiterals = true;
        this.setCaseSensitive(true);
        this.literals = new Hashtable();
        this.literals.put(new ANTLRHashString("byte", this), new Integer(106));
        this.literals.put(new ANTLRHashString("public", this), new Integer(116));
        this.literals.put(new ANTLRHashString("trait", this), new Integer(95));
        this.literals.put(new ANTLRHashString("case", this), new Integer(150));
        this.literals.put(new ANTLRHashString("short", this), new Integer(108));
        this.literals.put(new ANTLRHashString("break", this), new Integer(144));
        this.literals.put(new ANTLRHashString("while", this), new Integer(139));
        this.literals.put(new ANTLRHashString("new", this), new Integer(159));
        this.literals.put(new ANTLRHashString("instanceof", this), new Integer(158));
        this.literals.put(new ANTLRHashString("implements", this), new Integer(131));
        this.literals.put(new ANTLRHashString("synchronized", this), new Integer(121));
        this.literals.put(new ANTLRHashString("const", this), new Integer(41));
        this.literals.put(new ANTLRHashString("float", this), new Integer(110));
        this.literals.put(new ANTLRHashString("package", this), new Integer(81));
        this.literals.put(new ANTLRHashString("return", this), new Integer(143));
        this.literals.put(new ANTLRHashString("throw", this), new Integer(146));
        this.literals.put(new ANTLRHashString("null", this), new Integer(160));
        this.literals.put(new ANTLRHashString("def", this), new Integer(84));
        this.literals.put(new ANTLRHashString("threadsafe", this), new Integer(120));
        this.literals.put(new ANTLRHashString("protected", this), new Integer(117));
        this.literals.put(new ANTLRHashString("class", this), new Integer(92));
        this.literals.put(new ANTLRHashString("throws", this), new Integer(130));
        this.literals.put(new ANTLRHashString("do", this), new Integer(42));
        this.literals.put(new ANTLRHashString("strictfp", this), new Integer(43));
        this.literals.put(new ANTLRHashString("super", this), new Integer(99));
        this.literals.put(new ANTLRHashString("transient", this), new Integer(118));
        this.literals.put(new ANTLRHashString("native", this), new Integer(119));
        this.literals.put(new ANTLRHashString("interface", this), new Integer(93));
        this.literals.put(new ANTLRHashString("final", this), new Integer(38));
        this.literals.put(new ANTLRHashString("if", this), new Integer(137));
        this.literals.put(new ANTLRHashString("double", this), new Integer(112));
        this.literals.put(new ANTLRHashString("volatile", this), new Integer(122));
        this.literals.put(new ANTLRHashString("as", this), new Integer(114));
        this.literals.put(new ANTLRHashString("assert", this), new Integer(147));
        this.literals.put(new ANTLRHashString("catch", this), new Integer(153));
        this.literals.put(new ANTLRHashString("try", this), new Integer(151));
        this.literals.put(new ANTLRHashString("goto", this), new Integer(40));
        this.literals.put(new ANTLRHashString("enum", this), new Integer(94));
        this.literals.put(new ANTLRHashString("int", this), new Integer(109));
        this.literals.put(new ANTLRHashString("for", this), new Integer(141));
        this.literals.put(new ANTLRHashString("extends", this), new Integer(98));
        this.literals.put(new ANTLRHashString("boolean", this), new Integer(105));
        this.literals.put(new ANTLRHashString("char", this), new Integer(107));
        this.literals.put(new ANTLRHashString("private", this), new Integer(115));
        this.literals.put(new ANTLRHashString("default", this), new Integer(129));
        this.literals.put(new ANTLRHashString("false", this), new Integer(157));
        this.literals.put(new ANTLRHashString("this", this), new Integer(132));
        this.literals.put(new ANTLRHashString("static", this), new Integer(83));
        this.literals.put(new ANTLRHashString("abstract", this), new Integer(39));
        this.literals.put(new ANTLRHashString("continue", this), new Integer(145));
        this.literals.put(new ANTLRHashString("finally", this), new Integer(152));
        this.literals.put(new ANTLRHashString("else", this), new Integer(138));
        this.literals.put(new ANTLRHashString("import", this), new Integer(82));
        this.literals.put(new ANTLRHashString("in", this), new Integer(142));
        this.literals.put(new ANTLRHashString("void", this), new Integer(104));
        this.literals.put(new ANTLRHashString("switch", this), new Integer(140));
        this.literals.put(new ANTLRHashString("true", this), new Integer(161));
        this.literals.put(new ANTLRHashString("long", this), new Integer(111));
    }

    /*
     * Exception decompiling
     */
    @Override
    public Token nextToken() throws TokenStreamException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [21[DOLOOP]], but top level block is 1[TRYBLOCK]
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
        int _ttype = 97;
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
        int _ttype = 91;
        this.match('(');
        if (this.inputState.guessing == 0) {
            ++this.parenLevel;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 123;
        this.match(')');
        if (this.inputState.guessing == 0) {
            --this.parenLevel;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 85;
        this.match('[');
        if (this.inputState.guessing == 0) {
            ++this.parenLevel;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 86;
        this.match(']');
        if (this.inputState.guessing == 0) {
            --this.parenLevel;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 126;
        this.match('{');
        if (this.inputState.guessing == 0) {
            this.pushParenLevel();
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 127;
        this.match('}');
        if (this.inputState.guessing == 0) {
            this.popParenLevel();
            if (this.stringCtorState != 0) {
                this.restartStringCtor(true);
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 136;
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
        int _ttype = 101;
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
        int _ttype = 90;
        this.match('.');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 124;
        this.match('=');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCOMPARE_TO(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 184;
        this.match("<=>");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 181;
        this.match("==");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mIDENTICAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 182;
        this.match("===");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mLNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 196;
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
        int _ttype = 195;
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
        int _ttype = 180;
        this.match("!=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNOT_IDENTICAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 183;
        this.match("!==");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 191;
        this.match('/');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mDIV_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 165;
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
        int _ttype = 148;
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
        int _ttype = 162;
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
        int _ttype = 190;
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
        int _ttype = 149;
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
        int _ttype = 163;
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
        int _ttype = 193;
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
        int _ttype = 113;
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
        int _ttype = 164;
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
        int _ttype = 192;
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
        int _ttype = 166;
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
        int _ttype = 102;
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
        int _ttype = 167;
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
        int _ttype = 103;
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
        int _ttype = 168;
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
        int _ttype = 186;
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
        int _ttype = 100;
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
        int _ttype = 187;
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
        int _ttype = 169;
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
        int _ttype = 185;
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
        int _ttype = 89;
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
        int _ttype = 177;
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
        int _ttype = 171;
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
        int _ttype = 134;
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
        int _ttype = 172;
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
        int _ttype = 175;
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
        int _ttype = 125;
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
        int _ttype = 170;
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
        int _ttype = 176;
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
        int _ttype = 128;
        this.match(';');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mDOLLAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 206;
        this.match('$');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRANGE_INCLUSIVE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 188;
        this.match("..");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mRANGE_EXCLUSIVE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 189;
        this.match("..<");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mTRIPLE_DOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 133;
        this.match("...");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSPREAD_DOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 154;
        this.match("*.");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mOPTIONAL_DOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 155;
        this.match("?.");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mELVIS_OPERATOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 174;
        this.match("?:");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mMEMBER_POINTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 156;
        this.match(".&");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mREGEX_FIND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 178;
        this.match("=~");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mREGEX_MATCH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 179;
        this.match("==~");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSTAR_STAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 194;
        this.match("**");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSTAR_STAR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 173;
        this.match("**=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mCLOSABLE_BLOCK_OP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 135;
        this.match("->");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 207;
        int _cnt659 = 0;
        while (true) {
            if (this.LA(1) == '\\' && (this.LA(2) == '\n' || this.LA(2) == '\r')) {
                this.match('\\');
                this.mONE_NL(false, false);
            } else if (this.LA(1) == ' ') {
                this.match(' ');
            } else if (this.LA(1) == '\t') {
                this.match('\t');
            } else if (this.LA(1) == '\f') {
                this.match('\f');
            } else {
                if (_cnt659 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            ++_cnt659;
        }
        if (this.inputState.guessing == 0 && !this.whitespaceIncluded) {
            _ttype = -1;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mONE_NL(boolean _createToken, boolean check) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 208;
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            int _saveIndex = this.text.length();
            this.match("\r\n");
            this.text.setLength(_saveIndex);
        } else if (this.LA(1) == '\r') {
            int _saveIndex = this.text.length();
            this.match('\r');
            this.text.setLength(_saveIndex);
        } else if (this.LA(1) == '\n') {
            int _saveIndex = this.text.length();
            this.match('\n');
            this.text.setLength(_saveIndex);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (this.inputState.guessing == 0) {
            this.newlineCheck(check);
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mNLS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 205;
        this.mONE_NL(false, true);
        if (!(this.LA(1) != '\t' && this.LA(1) != '\n' && this.LA(1) != '\f' && this.LA(1) != '\r' && this.LA(1) != ' ' && this.LA(1) != '/' && this.LA(1) != '\\' || this.whitespaceIncluded)) {
            int _cnt665 = 0;
            block4: while (true) {
                switch (this.LA(1)) {
                    case '\n': 
                    case '\r': {
                        this.mONE_NL(false, true);
                        break;
                    }
                    case '\t': 
                    case '\f': 
                    case ' ': 
                    case '\\': {
                        this.mWS(false);
                        break;
                    }
                    default: {
                        if (this.LA(1) == '/' && this.LA(2) == '/') {
                            this.mSL_COMMENT(false);
                            break;
                        }
                        if (this.LA(1) == '/' && this.LA(2) == '*') {
                            this.mML_COMMENT(false);
                            break;
                        }
                        if (_cnt665 >= 1) break block4;
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                ++_cnt665;
            }
        }
        if (this.inputState.guessing == 0 && !this.whitespaceIncluded) {
            if (this.parenLevel != 0) {
                _ttype = -1;
            } else {
                this.text.setLength(_begin);
                this.text.append("<newline>");
            }
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
        int _ttype = 209;
        this.match("//");
        while (_tokenSet_1.member(this.LA(1))) {
            this.match(_tokenSet_1);
        }
        if (this.inputState.guessing == 0 && !this.whitespaceIncluded) {
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
        int _ttype = 210;
        if (!this.atMultiCommentStart()) {
            throw new SemanticException(" atMultiCommentStart() ");
        }
        this.match("/*");
        while (true) {
            boolean synPredMatched677 = false;
            if (this.LA(1) == '*' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
                int _m677 = this.mark();
                synPredMatched677 = true;
                ++this.inputState.guessing;
                try {
                    this.match('*');
                    this.matchNot('/');
                }
                catch (RecognitionException pe) {
                    synPredMatched677 = false;
                }
                this.rewind(_m677);
                --this.inputState.guessing;
            }
            if (synPredMatched677) {
                this.match('*');
                continue;
            }
            if (this.LA(1) == '\n' || this.LA(1) == '\r') {
                this.mONE_NL(false, true);
                continue;
            }
            if (!_tokenSet_2.member(this.LA(1))) break;
            this.match(_tokenSet_2);
        }
        this.match("*/");
        if (this.inputState.guessing == 0 && !this.whitespaceIncluded) {
            _ttype = -1;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    public final void mSH_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 80;
        if (this.getLine() != 1 || this.getColumn() != 1) {
            throw new SemanticException("getLine() == 1 && getColumn() == 1");
        }
        this.match("#!");
        while (_tokenSet_1.member(this.LA(1))) {
            this.match(_tokenSet_1);
        }
        if (this.inputState.guessing == 0 && !this.whitespaceIncluded) {
            _ttype = -1;
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
        int _ttype = 88;
        int tt = 0;
        boolean synPredMatched682 = false;
        if (this.LA(1) == '\'' && this.LA(2) == '\'' && this.LA(3) == '\'' && this.LA(4) >= '\u0000' && this.LA(4) <= '\ufffe') {
            int _m682 = this.mark();
            synPredMatched682 = true;
            ++this.inputState.guessing;
            try {
                this.match("'''");
            }
            catch (RecognitionException pe) {
                synPredMatched682 = false;
            }
            this.rewind(_m682);
            --this.inputState.guessing;
        }
        if (synPredMatched682) {
            int _saveIndex = this.text.length();
            this.match("'''");
            this.text.setLength(_saveIndex);
            block17: while (true) {
                switch (this.LA(1)) {
                    case '\\': {
                        this.mESC(false);
                        continue block17;
                    }
                    case '\"': {
                        this.match('\"');
                        continue block17;
                    }
                    case '$': {
                        this.match('$');
                        continue block17;
                    }
                    case '\n': 
                    case '\r': {
                        this.mSTRING_NL(false, true);
                        continue block17;
                    }
                }
                boolean synPredMatched686 = false;
                if (this.LA(1) == '\'' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe' && this.LA(4) >= '\u0000' && this.LA(4) <= '\ufffe') {
                    int _m686;
                    block39: {
                        _m686 = this.mark();
                        synPredMatched686 = true;
                        ++this.inputState.guessing;
                        try {
                            this.match('\'');
                            if (_tokenSet_3.member(this.LA(1))) {
                                this.matchNot('\'');
                                break block39;
                            }
                            if (this.LA(1) == '\'') {
                                this.match('\'');
                                this.matchNot('\'');
                                break block39;
                            }
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        catch (RecognitionException pe) {
                            synPredMatched686 = false;
                        }
                    }
                    this.rewind(_m686);
                    --this.inputState.guessing;
                }
                if (synPredMatched686) {
                    this.match('\'');
                    continue;
                }
                if (!_tokenSet_4.member(this.LA(1))) break;
                this.mSTRING_CH(false);
            }
            _saveIndex = this.text.length();
            this.match("'''");
            this.text.setLength(_saveIndex);
        } else {
            boolean synPredMatched691 = false;
            if (this.LA(1) == '\"' && this.LA(2) == '\"' && this.LA(3) == '\"' && this.LA(4) >= '\u0000' && this.LA(4) <= '\ufffe') {
                int _m691 = this.mark();
                synPredMatched691 = true;
                ++this.inputState.guessing;
                try {
                    this.match("\"\"\"");
                }
                catch (RecognitionException pe) {
                    synPredMatched691 = false;
                }
                this.rewind(_m691);
                --this.inputState.guessing;
            }
            if (synPredMatched691) {
                int _saveIndex = this.text.length();
                this.match("\"\"\"");
                this.text.setLength(_saveIndex);
                tt = this.mSTRING_CTOR_END(false, true, true);
                if (this.inputState.guessing == 0) {
                    _ttype = tt;
                }
            } else if (this.LA(1) == '\'' && _tokenSet_1.member(this.LA(2))) {
                int _saveIndex = this.text.length();
                this.match('\'');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing == 0) {
                    ++this.suppressNewline;
                }
                block18: while (true) {
                    switch (this.LA(1)) {
                        case '\\': {
                            this.mESC(false);
                            continue block18;
                        }
                        case '\"': {
                            this.match('\"');
                            continue block18;
                        }
                        case '$': {
                            this.match('$');
                            continue block18;
                        }
                    }
                    if (!_tokenSet_4.member(this.LA(1))) break;
                    this.mSTRING_CH(false);
                }
                if (this.inputState.guessing == 0) {
                    --this.suppressNewline;
                }
                _saveIndex = this.text.length();
                this.match('\'');
                this.text.setLength(_saveIndex);
            } else if (this.LA(1) == '\"' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe') {
                int _saveIndex = this.text.length();
                this.match('\"');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing == 0) {
                    ++this.suppressNewline;
                }
                tt = this.mSTRING_CTOR_END(false, true, false);
                if (this.inputState.guessing == 0) {
                    _ttype = tt;
                }
            } else {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mSTRING_CH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 211;
        this.match(_tokenSet_4);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mESC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        int _ttype;
        int _begin;
        Token _token;
        block38: {
            block37: {
                _token = null;
                _begin = this.text.length();
                _ttype = 220;
                if (this.LA(1) != '\\' || this.LA(2) != '\"' && this.LA(2) != '$' && this.LA(2) != '\'' && this.LA(2) != '0' && this.LA(2) != '1' && this.LA(2) != '2' && this.LA(2) != '3' && this.LA(2) != '4' && this.LA(2) != '5' && this.LA(2) != '6' && this.LA(2) != '7' && this.LA(2) != '\\' && this.LA(2) != 'b' && this.LA(2) != 'f' && this.LA(2) != 'n' && this.LA(2) != 'r' && this.LA(2) != 't' && this.LA(2) != 'u') break block37;
                int _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                switch (this.LA(1)) {
                    case 'n': {
                        this.match('n');
                        if (this.inputState.guessing == 0) {
                            this.text.setLength(_begin);
                            this.text.append("\n");
                        }
                        break block38;
                    }
                    case 'r': {
                        this.match('r');
                        if (this.inputState.guessing == 0) {
                            this.text.setLength(_begin);
                            this.text.append("\r");
                        }
                        break block38;
                    }
                    case 't': {
                        this.match('t');
                        if (this.inputState.guessing == 0) {
                            this.text.setLength(_begin);
                            this.text.append("\t");
                        }
                        break block38;
                    }
                    case 'b': {
                        this.match('b');
                        if (this.inputState.guessing == 0) {
                            this.text.setLength(_begin);
                            this.text.append("\b");
                        }
                        break block38;
                    }
                    case 'f': {
                        this.match('f');
                        if (this.inputState.guessing == 0) {
                            this.text.setLength(_begin);
                            this.text.append("\f");
                        }
                        break block38;
                    }
                    case '\"': {
                        this.match('\"');
                        break block38;
                    }
                    case '\'': {
                        this.match('\'');
                        break block38;
                    }
                    case '\\': {
                        this.match('\\');
                        break block38;
                    }
                    case '$': {
                        this.match('$');
                        break block38;
                    }
                    case 'u': {
                        int _cnt736 = 0;
                        while (true) {
                            if (this.LA(1) != 'u') {
                                if (_cnt736 >= 1) break;
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                            this.match('u');
                            ++_cnt736;
                        }
                        if (this.inputState.guessing == 0) {
                            this.text.setLength(_begin);
                            this.text.append("");
                        }
                        this.mHEX_DIGIT(false);
                        this.mHEX_DIGIT(false);
                        this.mHEX_DIGIT(false);
                        this.mHEX_DIGIT(false);
                        if (this.inputState.guessing == 0) {
                            char ch = (char)Integer.parseInt(new String(this.text.getBuffer(), _begin, this.text.length() - _begin), 16);
                            this.text.setLength(_begin);
                            this.text.append(ch);
                        }
                        break block38;
                    }
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': {
                        this.matchRange('0', '3');
                        if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe') {
                            this.matchRange('0', '7');
                            if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe') {
                                this.matchRange('0', '7');
                            } else if (this.LA(1) < '\u0000' || this.LA(1) > '\ufffe') {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        } else if (this.LA(1) < '\u0000' || this.LA(1) > '\ufffe') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        if (this.inputState.guessing == 0) {
                            char ch = (char)Integer.parseInt(new String(this.text.getBuffer(), _begin, this.text.length() - _begin), 8);
                            this.text.setLength(_begin);
                            this.text.append(ch);
                        }
                        break block38;
                    }
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': {
                        this.matchRange('4', '7');
                        if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe') {
                            this.matchRange('0', '7');
                        } else if (this.LA(1) < '\u0000' || this.LA(1) > '\ufffe') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        if (this.inputState.guessing == 0) {
                            char ch = (char)Integer.parseInt(new String(this.text.getBuffer(), _begin, this.text.length() - _begin), 8);
                            this.text.setLength(_begin);
                            this.text.append(ch);
                        }
                        break block38;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
            }
            if (this.LA(1) == '\\' && (this.LA(2) == '\n' || this.LA(2) == '\r')) {
                int _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                _saveIndex = this.text.length();
                this.mONE_NL(false, false);
                this.text.setLength(_saveIndex);
            } else {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mSTRING_NL(boolean _createToken, boolean allowNewline) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 221;
        if (this.inputState.guessing == 0 && !allowNewline) {
            throw new MismatchedCharException('\n', '\n', true, (CharScanner)this);
        }
        this.mONE_NL(false, false);
        if (this.inputState.guessing == 0) {
            this.text.setLength(_begin);
            this.text.append('\n');
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final int mSTRING_CTOR_END(boolean _createToken, boolean fromStart, boolean tripleQuote) throws RecognitionException, CharStreamException, TokenStreamException {
        int tt = 198;
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 198;
        boolean dollarOK = false;
        block11: while (true) {
            switch (this.LA(1)) {
                case '\\': {
                    this.mESC(false);
                    continue block11;
                }
                case '\'': {
                    this.match('\'');
                    continue block11;
                }
                case '\n': 
                case '\r': {
                    this.mSTRING_NL(false, tripleQuote);
                    continue block11;
                }
            }
            boolean synPredMatched696 = false;
            if (this.LA(1) == '\"' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && tripleQuote) {
                int _m696;
                block24: {
                    _m696 = this.mark();
                    synPredMatched696 = true;
                    ++this.inputState.guessing;
                    try {
                        this.match('\"');
                        if (_tokenSet_5.member(this.LA(1))) {
                            this.matchNot('\"');
                            break block24;
                        }
                        if (this.LA(1) == '\"') {
                            this.match('\"');
                            this.matchNot('\"');
                            break block24;
                        }
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    catch (RecognitionException pe) {
                        synPredMatched696 = false;
                    }
                }
                this.rewind(_m696);
                --this.inputState.guessing;
            }
            if (synPredMatched696) {
                this.match('\"');
                continue;
            }
            if (!_tokenSet_4.member(this.LA(1))) break;
            this.mSTRING_CH(false);
        }
        switch (this.LA(1)) {
            case '\"': {
                if (this.LA(1) == '\"' && this.LA(2) == '\"' && tripleQuote) {
                    int _saveIndex = this.text.length();
                    this.match("\"\"\"");
                    this.text.setLength(_saveIndex);
                } else if (this.LA(1) == '\"' && !tripleQuote) {
                    int _saveIndex = this.text.length();
                    this.match("\"");
                    this.text.setLength(_saveIndex);
                } else {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                if (this.inputState.guessing != 0) break;
                if (fromStart) {
                    tt = 88;
                }
                if (tripleQuote) break;
                --this.suppressNewline;
                break;
            }
            case '$': {
                if (this.inputState.guessing == 0) {
                    dollarOK = this.atValidDollarEscape();
                }
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing != 0) break;
                this.require(dollarOK, "illegal string body character after dollar sign", "either escape a literal dollar sign \"\\$5\" or bracket the value expression \"${5}\"");
                tt = fromStart ? 197 : 49;
                this.stringCtorState = 4 + (tripleQuote ? 1 : 0);
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (this.inputState.guessing == 0) {
            _ttype = tt;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
        return tt;
    }

    public final void mREGEXP_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 212;
        int tt = 0;
        if (this.atMultiCommentStart()) {
            throw new SemanticException(" !atMultiCommentStart() ");
        }
        if (this.LA(1) == '/' && _tokenSet_6.member(this.LA(2)) && this.allowRegexpLiteral()) {
            int _saveIndex = this.text.length();
            this.match('/');
            this.text.setLength(_saveIndex);
            if (this.inputState.guessing == 0) {
                ++this.suppressNewline;
            }
            if (this.LA(1) == '$' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && !this.atValidDollarEscape()) {
                this.match('$');
                tt = this.mREGEXP_CTOR_END(false, true);
            } else if (_tokenSet_7.member(this.LA(1))) {
                this.mREGEXP_SYMBOL(false);
                tt = this.mREGEXP_CTOR_END(false, true);
            } else if (this.LA(1) == '$') {
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing == 0) {
                    tt = 197;
                    this.stringCtorState = 6;
                }
            } else {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            if (this.inputState.guessing == 0) {
                _ttype = tt;
            }
        } else if (this.LA(1) == '/' && this.LA(2) == '=') {
            this.mDIV_ASSIGN(false);
            if (this.inputState.guessing == 0) {
                _ttype = 165;
            }
        } else {
            boolean synPredMatched706 = false;
            if (this.LA(1) == '/') {
                int _m706 = this.mark();
                synPredMatched706 = true;
                ++this.inputState.guessing;
                try {
                    this.match('/');
                    this.matchNot('=');
                }
                catch (RecognitionException pe) {
                    synPredMatched706 = false;
                }
                this.rewind(_m706);
                --this.inputState.guessing;
            }
            if (synPredMatched706) {
                this.mDIV(false);
                if (this.inputState.guessing == 0) {
                    _ttype = 191;
                }
            } else {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mREGEXP_SYMBOL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 218;
        if (this.LA(1) == '\\' && this.LA(2) == '/' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
            this.match('\\');
            this.match('/');
            if (this.inputState.guessing == 0) {
                this.text.setLength(_begin);
                this.text.append('/');
            }
        } else if (this.LA(1) == '\\' && (this.LA(2) == '\n' || this.LA(2) == '\r') && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
            int _saveIndex = this.text.length();
            this.match('\\');
            this.text.setLength(_saveIndex);
            _saveIndex = this.text.length();
            this.mONE_NL(false, false);
            this.text.setLength(_saveIndex);
        } else if (this.LA(1) == '\\' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && this.LA(2) != '/' && this.LA(2) != '\n' && this.LA(2) != '\r') {
            this.match('\\');
        } else if (_tokenSet_8.member(this.LA(1))) {
            this.match(_tokenSet_8);
        } else if (this.LA(1) == '\n' || this.LA(1) == '\r') {
            this.mSTRING_NL(false, true);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final int mREGEXP_CTOR_END(boolean _createToken, boolean fromStart) throws RecognitionException, CharStreamException, TokenStreamException {
        int tt = 198;
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 214;
        while (true) {
            if (this.LA(1) == '$' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && !this.atValidDollarEscape()) {
                this.match('$');
                continue;
            }
            if (!_tokenSet_7.member(this.LA(1))) break;
            this.mREGEXP_SYMBOL(false);
        }
        switch (this.LA(1)) {
            case '/': {
                int _saveIndex = this.text.length();
                this.match('/');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing != 0) break;
                if (fromStart) {
                    tt = 88;
                }
                --this.suppressNewline;
                break;
            }
            case '$': {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing != 0) break;
                tt = fromStart ? 197 : 49;
                this.stringCtorState = 6;
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (this.inputState.guessing == 0) {
            _ttype = tt;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
        return tt;
    }

    public final void mDOLLAR_REGEXP_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 213;
        int tt = 0;
        if (!this.allowRegexpLiteral()) {
            throw new SemanticException("allowRegexpLiteral()");
        }
        int _saveIndex = this.text.length();
        this.match("$/");
        this.text.setLength(_saveIndex);
        boolean synPredMatched710 = false;
        if (this.LA(1) == '$' && this.LA(2) == '/' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
            int _m710 = this.mark();
            synPredMatched710 = true;
            ++this.inputState.guessing;
            try {
                this.match('$');
                this.match('/');
            }
            catch (RecognitionException pe) {
                synPredMatched710 = false;
            }
            this.rewind(_m710);
            --this.inputState.guessing;
        }
        if (synPredMatched710) {
            this.mESCAPED_SLASH(false);
            tt = this.mDOLLAR_REGEXP_CTOR_END(false, true);
        } else {
            boolean synPredMatched712 = false;
            if (this.LA(1) == '$' && this.LA(2) == '$' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
                int _m712 = this.mark();
                synPredMatched712 = true;
                ++this.inputState.guessing;
                try {
                    this.match('$');
                    this.match('$');
                }
                catch (RecognitionException pe) {
                    synPredMatched712 = false;
                }
                this.rewind(_m712);
                --this.inputState.guessing;
            }
            if (synPredMatched712) {
                this.mESCAPED_DOLLAR(false);
                tt = this.mDOLLAR_REGEXP_CTOR_END(false, true);
            } else if (!(this.LA(1) != '$' || this.LA(2) < '\u0000' || this.LA(2) > '\ufffe' || this.atValidDollarEscape() || this.atDollarSlashEscape() || this.atDollarDollarEscape())) {
                this.match('$');
                tt = this.mDOLLAR_REGEXP_CTOR_END(false, true);
            } else if (_tokenSet_9.member(this.LA(1))) {
                this.mDOLLAR_REGEXP_SYMBOL(false);
                tt = this.mDOLLAR_REGEXP_CTOR_END(false, true);
            } else if (this.LA(1) == '$') {
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing == 0) {
                    tt = 197;
                    this.stringCtorState = 7;
                }
            } else {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (this.inputState.guessing == 0) {
            _ttype = tt;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mDOLLAR_REGEXP_SYMBOL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 219;
        switch (this.LA(1)) {
            case '/': {
                this.match('/');
                break;
            }
            case '\n': 
            case '\r': {
                this.mSTRING_NL(false, true);
                break;
            }
            default: {
                if (this.LA(1) == '\\' && (this.LA(2) == '\n' || this.LA(2) == '\r') && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
                    int _saveIndex = this.text.length();
                    this.match('\\');
                    this.text.setLength(_saveIndex);
                    _saveIndex = this.text.length();
                    this.mONE_NL(false, false);
                    this.text.setLength(_saveIndex);
                    break;
                }
                if (this.LA(1) == '\\' && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && this.LA(2) != '\n' && this.LA(2) != '\r') {
                    this.match('\\');
                    break;
                }
                if (_tokenSet_8.member(this.LA(1))) {
                    this.match(_tokenSet_8);
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

    protected final int mDOLLAR_REGEXP_CTOR_END(boolean _createToken, boolean fromStart) throws RecognitionException, CharStreamException, TokenStreamException {
        int tt = 198;
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 215;
        while (true) {
            boolean synPredMatched720 = false;
            if (this.LA(1) == '$' && this.LA(2) == '/' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
                int _m720 = this.mark();
                synPredMatched720 = true;
                ++this.inputState.guessing;
                try {
                    this.match('$');
                    this.match('/');
                }
                catch (RecognitionException pe) {
                    synPredMatched720 = false;
                }
                this.rewind(_m720);
                --this.inputState.guessing;
            }
            if (synPredMatched720) {
                this.mESCAPED_SLASH(false);
                continue;
            }
            boolean synPredMatched722 = false;
            if (this.LA(1) == '$' && this.LA(2) == '$' && this.LA(3) >= '\u0000' && this.LA(3) <= '\ufffe') {
                int _m722 = this.mark();
                synPredMatched722 = true;
                ++this.inputState.guessing;
                try {
                    this.match('$');
                    this.match('$');
                }
                catch (RecognitionException pe) {
                    synPredMatched722 = false;
                }
                this.rewind(_m722);
                --this.inputState.guessing;
            }
            if (synPredMatched722) {
                this.mESCAPED_DOLLAR(false);
                continue;
            }
            if (_tokenSet_9.member(this.LA(1)) && this.LA(2) >= '\u0000' && this.LA(2) <= '\ufffe' && (this.LA(1) != '/' || this.LA(2) != '$')) {
                this.mDOLLAR_REGEXP_SYMBOL(false);
                continue;
            }
            if (this.LA(1) != '$' || this.LA(2) < '\u0000' || this.LA(2) > '\ufffe' || this.atValidDollarEscape() || this.atDollarSlashEscape() || this.atDollarDollarEscape()) break;
            this.match('$');
        }
        switch (this.LA(1)) {
            case '/': {
                int _saveIndex = this.text.length();
                this.match("/$");
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing != 0 || !fromStart) break;
                tt = 88;
                break;
            }
            case '$': {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                if (this.inputState.guessing != 0) break;
                tt = fromStart ? 197 : 49;
                this.stringCtorState = 7;
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (this.inputState.guessing == 0) {
            _ttype = tt;
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
        return tt;
    }

    protected final void mESCAPED_SLASH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 216;
        this.match('$');
        this.match('/');
        if (this.inputState.guessing == 0) {
            this.text.setLength(_begin);
            this.text.append('/');
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mESCAPED_DOLLAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 217;
        this.match('$');
        this.match('$');
        if (this.inputState.guessing == 0) {
            this.text.setLength(_begin);
            this.text.append('$');
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
        int _ttype = 222;
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
        int _ttype = 223;
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
        int _ttype = 87;
        if (_tokenSet_0.member(this.LA(1)) && this.stringCtorState == 0) {
            if (this.LA(1) == '$') {
                this.mDOLLAR(false);
            } else if (_tokenSet_10.member(this.LA(1))) {
                this.mLETTER(false);
            } else {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            block4: while (true) {
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
                        this.mDIGIT(false);
                        continue block4;
                    }
                    case '$': {
                        this.mDOLLAR(false);
                        continue block4;
                    }
                }
                if (_tokenSet_10.member(this.LA(1))) {
                    this.mLETTER(false);
                    continue;
                }
                break;
            }
        } else if (_tokenSet_10.member(this.LA(1))) {
            this.mLETTER(false);
            while (true) {
                if (_tokenSet_10.member(this.LA(1))) {
                    this.mLETTER(false);
                    continue;
                }
                if (this.LA(1) >= '0' && this.LA(1) <= '9') {
                    this.mDIGIT(false);
                    continue;
                }
                break;
            }
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (this.inputState.guessing == 0) {
            int ttype;
            if (this.stringCtorState != 0) {
                if (this.LA(1) == '.' && this.LA(2) != '$' && Character.isJavaIdentifierStart(this.LA(2))) {
                    this.restartStringCtor(false);
                } else {
                    this.restartStringCtor(true);
                }
            }
            if (!((ttype = this.testLiteralsTable(87)) != 114 && ttype != 84 && ttype != 142 && ttype != 95 || this.LA(1) != '.' && this.lastSigTokenType != 90 && this.lastSigTokenType != 81)) {
                ttype = 87;
            }
            if (ttype == 81 && (this.LA(1) == '.' || this.lastSigTokenType == 90 || this.lastSigTokenType == 82 || this.LA(1) == ')' && this.lastSigTokenType == 91)) {
                ttype = 87;
            }
            if (ttype == 83 && this.LA(1) == '.') {
                ttype = 87;
            }
            _ttype = ttype;
            if (this.assertEnabled && "assert".equals(new String(this.text.getBuffer(), _begin, this.text.length() - _begin))) {
                _ttype = 147;
            }
            if (this.enumEnabled && "enum".equals(new String(this.text.getBuffer(), _begin, this.text.length() - _begin))) {
                _ttype = 94;
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mLETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 224;
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
            case '\u00c0': 
            case '\u00c1': 
            case '\u00c2': 
            case '\u00c3': 
            case '\u00c4': 
            case '\u00c5': 
            case '\u00c6': 
            case '\u00c7': 
            case '\u00c8': 
            case '\u00c9': 
            case '\u00ca': 
            case '\u00cb': 
            case '\u00cc': 
            case '\u00cd': 
            case '\u00ce': 
            case '\u00cf': 
            case '\u00d0': 
            case '\u00d1': 
            case '\u00d2': 
            case '\u00d3': 
            case '\u00d4': 
            case '\u00d5': 
            case '\u00d6': {
                this.matchRange('\u00c0', '\u00d6');
                break;
            }
            case '\u00d8': 
            case '\u00d9': 
            case '\u00da': 
            case '\u00db': 
            case '\u00dc': 
            case '\u00dd': 
            case '\u00de': 
            case '\u00df': 
            case '\u00e0': 
            case '\u00e1': 
            case '\u00e2': 
            case '\u00e3': 
            case '\u00e4': 
            case '\u00e5': 
            case '\u00e6': 
            case '\u00e7': 
            case '\u00e8': 
            case '\u00e9': 
            case '\u00ea': 
            case '\u00eb': 
            case '\u00ec': 
            case '\u00ed': 
            case '\u00ee': 
            case '\u00ef': 
            case '\u00f0': 
            case '\u00f1': 
            case '\u00f2': 
            case '\u00f3': 
            case '\u00f4': 
            case '\u00f5': 
            case '\u00f6': {
                this.matchRange('\u00d8', '\u00f6');
                break;
            }
            case '\u00f8': 
            case '\u00f9': 
            case '\u00fa': 
            case '\u00fb': 
            case '\u00fc': 
            case '\u00fd': 
            case '\u00fe': 
            case '\u00ff': {
                this.matchRange('\u00f8', '\u00ff');
                break;
            }
            case '_': {
                this.match('_');
                break;
            }
            default: {
                if (this.LA(1) >= '\u0100' && this.LA(1) <= '\ufffe') {
                    this.matchRange('\u0100', '\ufffe');
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

    protected final void mDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 225;
        this.matchRange('0', '9');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mDIGITS_WITH_UNDERSCORE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 226;
        this.mDIGIT(false);
        if (this.LA(1) == '0' || this.LA(1) == '1' || this.LA(1) == '2' || this.LA(1) == '3' || this.LA(1) == '4' || this.LA(1) == '5' || this.LA(1) == '6' || this.LA(1) == '7' || this.LA(1) == '8' || this.LA(1) == '9' || this.LA(1) == '_') {
            this.mDIGITS_WITH_UNDERSCORE_OPT(false);
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mDIGITS_WITH_UNDERSCORE_OPT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 227;
        while (true) {
            if (this.LA(1) >= '0' && this.LA(1) <= '9' && (this.LA(2) == '0' || this.LA(2) == '1' || this.LA(2) == '2' || this.LA(2) == '3' || this.LA(2) == '4' || this.LA(2) == '5' || this.LA(2) == '6' || this.LA(2) == '7' || this.LA(2) == '8' || this.LA(2) == '9' || this.LA(2) == '_')) {
                this.mDIGIT(false);
                continue;
            }
            if (this.LA(1) != '_') break;
            this.match('_');
        }
        this.mDIGIT(false);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 199;
        Token e = null;
        Token f2 = null;
        Token g2 = null;
        Token f3 = null;
        Token g3 = null;
        Token f4 = null;
        boolean isDecimal = false;
        Token t = null;
        block2 : switch (this.LA(1)) {
            case '0': {
                this.match('0');
                if (this.inputState.guessing == 0) {
                    isDecimal = true;
                }
                switch (this.LA(1)) {
                    case 'X': 
                    case 'x': {
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
                        if (this.inputState.guessing == 0) {
                            isDecimal = false;
                        }
                        this.mHEX_DIGIT(false);
                        if (!_tokenSet_11.member(this.LA(1))) break block2;
                        while (true) {
                            if (_tokenSet_12.member(this.LA(1)) && _tokenSet_11.member(this.LA(2))) {
                                this.mHEX_DIGIT(false);
                                continue;
                            }
                            if (this.LA(1) != '_') break;
                            this.match('_');
                        }
                        this.mHEX_DIGIT(false);
                        break;
                    }
                    case 'B': 
                    case 'b': {
                        switch (this.LA(1)) {
                            case 'b': {
                                this.match('b');
                                break;
                            }
                            case 'B': {
                                this.match('B');
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                        switch (this.LA(1)) {
                            case '0': {
                                this.match('0');
                                break;
                            }
                            case '1': {
                                this.match('1');
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                        if (this.LA(1) == '0' || this.LA(1) == '1' || this.LA(1) == '_') {
                            while (true) {
                                if (this.LA(1) == '0' && (this.LA(2) == '0' || this.LA(2) == '1' || this.LA(2) == '_')) {
                                    this.match('0');
                                    continue;
                                }
                                if (this.LA(1) == '1' && (this.LA(2) == '0' || this.LA(2) == '1' || this.LA(2) == '_')) {
                                    this.match('1');
                                    continue;
                                }
                                if (this.LA(1) != '_') break;
                                this.match('_');
                            }
                            switch (this.LA(1)) {
                                case '0': {
                                    this.match('0');
                                    break;
                                }
                                case '1': {
                                    this.match('1');
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                }
                            }
                        }
                        if (this.inputState.guessing != 0) break block2;
                        isDecimal = false;
                        break;
                    }
                    default: {
                        boolean synPredMatched773 = false;
                        if (this.LA(1) >= '0' && this.LA(1) <= '9') {
                            int _m773 = this.mark();
                            synPredMatched773 = true;
                            ++this.inputState.guessing;
                            try {
                                this.mDIGITS_WITH_UNDERSCORE(false);
                                switch (this.LA(1)) {
                                    case '.': {
                                        this.match('.');
                                        this.mDIGITS_WITH_UNDERSCORE(false);
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
                                synPredMatched773 = false;
                            }
                            this.rewind(_m773);
                            --this.inputState.guessing;
                        }
                        if (synPredMatched773) {
                            this.mDIGITS_WITH_UNDERSCORE(false);
                            break;
                        }
                        if (this.LA(1) < '0' || this.LA(1) > '7') break block2;
                        this.matchRange('0', '7');
                        if (this.LA(1) == '0' || this.LA(1) == '1' || this.LA(1) == '2' || this.LA(1) == '3' || this.LA(1) == '4' || this.LA(1) == '5' || this.LA(1) == '6' || this.LA(1) == '7' || this.LA(1) == '_') {
                            while (true) {
                                if (this.LA(1) >= '0' && this.LA(1) <= '7' && (this.LA(2) == '0' || this.LA(2) == '1' || this.LA(2) == '2' || this.LA(2) == '3' || this.LA(2) == '4' || this.LA(2) == '5' || this.LA(2) == '6' || this.LA(2) == '7' || this.LA(2) == '_')) {
                                    this.matchRange('0', '7');
                                    continue;
                                }
                                if (this.LA(1) != '_') break;
                                this.match('_');
                            }
                            this.matchRange('0', '7');
                        }
                        if (this.inputState.guessing != 0) break block2;
                        isDecimal = false;
                        break;
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
                if (this.LA(1) == '0' || this.LA(1) == '1' || this.LA(1) == '2' || this.LA(1) == '3' || this.LA(1) == '4' || this.LA(1) == '5' || this.LA(1) == '6' || this.LA(1) == '7' || this.LA(1) == '8' || this.LA(1) == '9' || this.LA(1) == '_') {
                    this.mDIGITS_WITH_UNDERSCORE_OPT(false);
                }
                if (this.inputState.guessing != 0) break;
                isDecimal = true;
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        switch (this.LA(1)) {
            case 'L': 
            case 'l': {
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
                _ttype = 201;
                break;
            }
            case 'I': 
            case 'i': {
                switch (this.LA(1)) {
                    case 'i': {
                        this.match('i');
                        break;
                    }
                    case 'I': {
                        this.match('I');
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                if (this.inputState.guessing != 0) break;
                _ttype = 199;
                break;
            }
            case 'G': 
            case 'g': {
                this.mBIG_SUFFIX(false);
                if (this.inputState.guessing != 0) break;
                _ttype = 203;
                break;
            }
            default: {
                String txt;
                boolean synPredMatched786 = false;
                if ((this.LA(1) == '.' || this.LA(1) == 'D' || this.LA(1) == 'E' || this.LA(1) == 'F' || this.LA(1) == 'd' || this.LA(1) == 'e' || this.LA(1) == 'f') && isDecimal) {
                    int _m786 = this.mark();
                    synPredMatched786 = true;
                    ++this.inputState.guessing;
                    try {
                        if (_tokenSet_13.member(this.LA(1))) {
                            this.matchNot('.');
                        } else {
                            if (this.LA(1) != '.') throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            this.match('.');
                            this.matchRange('0', '9');
                        }
                    }
                    catch (RecognitionException pe) {
                        synPredMatched786 = false;
                    }
                    this.rewind(_m786);
                    --this.inputState.guessing;
                }
                if (!synPredMatched786) break;
                block44 : switch (this.LA(1)) {
                    case '.': {
                        this.match('.');
                        this.mDIGITS_WITH_UNDERSCORE(false);
                        if (this.LA(1) == 'E' || this.LA(1) == 'e') {
                            this.mEXPONENT(true);
                            e = this._returnToken;
                        }
                        switch (this.LA(1)) {
                            case 'D': 
                            case 'F': 
                            case 'd': 
                            case 'f': {
                                this.mFLOAT_SUFFIX(true);
                                f2 = this._returnToken;
                                if (this.inputState.guessing != 0) break block44;
                                t = f2;
                                break;
                            }
                            case 'G': 
                            case 'g': {
                                this.mBIG_SUFFIX(true);
                                g2 = this._returnToken;
                                if (this.inputState.guessing != 0) break block44;
                                t = g2;
                                break;
                            }
                        }
                        break;
                    }
                    case 'E': 
                    case 'e': {
                        this.mEXPONENT(false);
                        switch (this.LA(1)) {
                            case 'D': 
                            case 'F': 
                            case 'd': 
                            case 'f': {
                                this.mFLOAT_SUFFIX(true);
                                f3 = this._returnToken;
                                if (this.inputState.guessing != 0) break block44;
                                t = f3;
                                break;
                            }
                            case 'G': 
                            case 'g': {
                                this.mBIG_SUFFIX(true);
                                g3 = this._returnToken;
                                if (this.inputState.guessing != 0) break block44;
                                t = g3;
                                break;
                            }
                        }
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
                String string = txt = t == null ? "" : t.getText().toUpperCase();
                _ttype = txt.indexOf(70) >= 0 ? 200 : (txt.indexOf(71) >= 0 ? 204 : 202);
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
        int _ttype = 228;
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
            case '9': 
            case '_': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        while (true) {
            if (this.LA(1) >= '0' && this.LA(1) <= '9' && (this.LA(2) == '0' || this.LA(2) == '1' || this.LA(2) == '2' || this.LA(2) == '3' || this.LA(2) == '4' || this.LA(2) == '5' || this.LA(2) == '6' || this.LA(2) == '7' || this.LA(2) == '8' || this.LA(2) == '9' || this.LA(2) == '_')) {
                this.matchRange('0', '9');
                continue;
            }
            if (this.LA(1) != '_') break;
            this.match('_');
        }
        this.matchRange('0', '9');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    protected final void mFLOAT_SUFFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 229;
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

    protected final void mBIG_SUFFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        int _begin = this.text.length();
        int _ttype = 230;
        switch (this.LA(1)) {
            case 'g': {
                this.match('g');
                break;
            }
            case 'G': {
                this.match('G');
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
        int _ttype = 96;
        this.match('@');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[2560];
        data[0] = 0x1000000000L;
        data[1] = 576460745995190270L;
        data[3] = -36028797027352577L;
        for (int i = 4; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[2048];
        data[0] = -9217L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[2048];
        data[0] = -4398046520321L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[2048];
        data[0] = -549755813889L;
        for (int i = 1; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[2048];
        data[0] = -635655169025L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[2048];
        data[0] = -17179869185L;
        for (int i = 1; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    private static final long[] mk_tokenSet_6() {
        long[] data = new long[2048];
        data[0] = -140737488355329L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_7() {
        long[] data = new long[2048];
        data[0] = -140806207832065L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_8() {
        long[] data = new long[2048];
        data[0] = -140806207841281L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_9() {
        long[] data = new long[2048];
        data[0] = -68719476737L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_10() {
        long[] data = new long[2560];
        data[1] = 576460745995190270L;
        data[3] = -36028797027352577L;
        for (int i = 4; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }

    private static final long[] mk_tokenSet_11() {
        long[] data = new long[1025];
        data[0] = 0x3FF000000000000L;
        data[1] = 543313363070L;
        return data;
    }

    private static final long[] mk_tokenSet_12() {
        long[] data = new long[1025];
        data[0] = 0x3FF000000000000L;
        data[1] = 0x7E0000007EL;
        return data;
    }

    private static final long[] mk_tokenSet_13() {
        long[] data = new long[2048];
        data[0] = -70368744177665L;
        for (int i = 1; i <= 1023; ++i) {
            data[i] = -1L;
        }
        return data;
    }

    static {
        _tokenSet_0 = new BitSet(GroovyLexer.mk_tokenSet_0());
        _tokenSet_1 = new BitSet(GroovyLexer.mk_tokenSet_1());
        _tokenSet_2 = new BitSet(GroovyLexer.mk_tokenSet_2());
        _tokenSet_3 = new BitSet(GroovyLexer.mk_tokenSet_3());
        _tokenSet_4 = new BitSet(GroovyLexer.mk_tokenSet_4());
        _tokenSet_5 = new BitSet(GroovyLexer.mk_tokenSet_5());
        _tokenSet_6 = new BitSet(GroovyLexer.mk_tokenSet_6());
        _tokenSet_7 = new BitSet(GroovyLexer.mk_tokenSet_7());
        _tokenSet_8 = new BitSet(GroovyLexer.mk_tokenSet_8());
        _tokenSet_9 = new BitSet(GroovyLexer.mk_tokenSet_9());
        _tokenSet_10 = new BitSet(GroovyLexer.mk_tokenSet_10());
        _tokenSet_11 = new BitSet(GroovyLexer.mk_tokenSet_11());
        _tokenSet_12 = new BitSet(GroovyLexer.mk_tokenSet_12());
        _tokenSet_13 = new BitSet(GroovyLexer.mk_tokenSet_13());
    }
}

