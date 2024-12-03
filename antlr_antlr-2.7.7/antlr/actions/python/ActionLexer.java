/*
 * Decompiled with CFR 0.152.
 */
package antlr.actions.python;

import antlr.ActionTransInfo;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharScanner;
import antlr.CharStreamException;
import antlr.CodeGenerator;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.RuleBlock;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.Tool;
import antlr.actions.python.ActionLexerTokenTypes;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.Vector;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;

public class ActionLexer
extends CharScanner
implements ActionLexerTokenTypes,
TokenStream {
    protected RuleBlock currentRule;
    protected CodeGenerator generator;
    protected int lineOffset = 0;
    private Tool antlrTool;
    ActionTransInfo transInfo;
    public static final BitSet _tokenSet_0 = new BitSet(ActionLexer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(ActionLexer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(ActionLexer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(ActionLexer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(ActionLexer.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(ActionLexer.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(ActionLexer.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(ActionLexer.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(ActionLexer.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(ActionLexer.mk_tokenSet_9());
    public static final BitSet _tokenSet_10 = new BitSet(ActionLexer.mk_tokenSet_10());
    public static final BitSet _tokenSet_11 = new BitSet(ActionLexer.mk_tokenSet_11());
    public static final BitSet _tokenSet_12 = new BitSet(ActionLexer.mk_tokenSet_12());
    public static final BitSet _tokenSet_13 = new BitSet(ActionLexer.mk_tokenSet_13());
    public static final BitSet _tokenSet_14 = new BitSet(ActionLexer.mk_tokenSet_14());
    public static final BitSet _tokenSet_15 = new BitSet(ActionLexer.mk_tokenSet_15());
    public static final BitSet _tokenSet_16 = new BitSet(ActionLexer.mk_tokenSet_16());
    public static final BitSet _tokenSet_17 = new BitSet(ActionLexer.mk_tokenSet_17());
    public static final BitSet _tokenSet_18 = new BitSet(ActionLexer.mk_tokenSet_18());
    public static final BitSet _tokenSet_19 = new BitSet(ActionLexer.mk_tokenSet_19());
    public static final BitSet _tokenSet_20 = new BitSet(ActionLexer.mk_tokenSet_20());
    public static final BitSet _tokenSet_21 = new BitSet(ActionLexer.mk_tokenSet_21());
    public static final BitSet _tokenSet_22 = new BitSet(ActionLexer.mk_tokenSet_22());
    public static final BitSet _tokenSet_23 = new BitSet(ActionLexer.mk_tokenSet_23());
    public static final BitSet _tokenSet_24 = new BitSet(ActionLexer.mk_tokenSet_24());
    public static final BitSet _tokenSet_25 = new BitSet(ActionLexer.mk_tokenSet_25());

    public ActionLexer(String string, RuleBlock ruleBlock, CodeGenerator codeGenerator, ActionTransInfo actionTransInfo) {
        this(new StringReader(string));
        this.currentRule = ruleBlock;
        this.generator = codeGenerator;
        this.transInfo = actionTransInfo;
    }

    public void setLineOffset(int n) {
        this.setLine(n);
    }

    public void setTool(Tool tool) {
        this.antlrTool = tool;
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

    public ActionLexer(InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }

    public ActionLexer(Reader reader) {
        this(new CharBuffer(reader));
    }

    public ActionLexer(InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }

    public ActionLexer(LexerSharedInputState lexerSharedInputState) {
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
        int n3 = 0;
        block4: while (true) {
            switch (this.LA(1)) {
                case '#': {
                    this.mAST_ITEM(false);
                    break;
                }
                case '$': {
                    this.mTEXT_ITEM(false);
                    break;
                }
                default: {
                    if (_tokenSet_0.member(this.LA(1))) {
                        this.mSTUFF(false);
                        break;
                    }
                    if (n3 >= 1) break block4;
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            ++n3;
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
        switch (this.LA(1)) {
            case '\"': {
                this.mSTRING(false);
                break;
            }
            case '\'': {
                this.mCHAR(false);
                break;
            }
            case '\n': {
                this.match('\n');
                this.newline();
                break;
            }
            default: {
                if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
                    this.mCOMMENT(false);
                    break;
                }
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match("\r\n");
                    this.newline();
                    break;
                }
                if (this.LA(1) == '/' && _tokenSet_1.member(this.LA(2))) {
                    this.match('/');
                    this.match(_tokenSet_1);
                    break;
                }
                if (this.LA(1) == '\r') {
                    this.match('\r');
                    this.newline();
                    break;
                }
                if (_tokenSet_2.member(this.LA(1))) {
                    this.match(_tokenSet_2);
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

    protected final void mAST_ITEM(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 6;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        if (this.LA(1) == '#' && this.LA(2) == '(') {
            int n3 = this.text.length();
            this.match('#');
            this.text.setLength(n3);
            this.mTREE(true);
            token2 = this._returnToken;
        } else if (this.LA(1) == '#' && _tokenSet_3.member(this.LA(2))) {
            int n4 = this.text.length();
            this.match('#');
            this.text.setLength(n4);
            this.mID(true);
            token3 = this._returnToken;
            String string = token3.getText();
            String string2 = this.generator.mapTreeId(string, this.transInfo);
            if (string2 != null) {
                this.text.setLength(n);
                this.text.append(string2);
            }
            if (_tokenSet_4.member(this.LA(1))) {
                this.mWS(false);
            }
            if (this.LA(1) == '=') {
                this.mVAR_ASSIGN(false);
            }
        } else if (this.LA(1) == '#' && this.LA(2) == '[') {
            int n5 = this.text.length();
            this.match('#');
            this.text.setLength(n5);
            this.mAST_CONSTRUCTOR(true);
            token4 = this._returnToken;
        } else if (this.LA(1) == '#' && this.LA(2) == '#') {
            this.match("##");
            String string = this.currentRule.getRuleName() + "_AST";
            this.text.setLength(n);
            this.text.append(string);
            if (this.transInfo != null) {
                this.transInfo.refRuleRoot = string;
            }
            if (_tokenSet_4.member(this.LA(1))) {
                this.mWS(false);
            }
            if (this.LA(1) == '=') {
                this.mVAR_ASSIGN(false);
            }
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected final void mTEXT_ITEM(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 7;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Token token6 = null;
        Token token7 = null;
        if (this.LA(1) == '$' && this.LA(2) == 's' && this.LA(3) == 'e') {
            this.match("$set");
            if (this.LA(1) == 'T' && this.LA(2) == 'e') {
                this.match("Text");
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                token3 = this._returnToken;
                this.match(')');
                String string = "self.text.setLength(_begin) ; self.text.append(" + token3.getText() + ")";
                this.text.setLength(n);
                this.text.append(string);
            } else if (this.LA(1) == 'T' && this.LA(2) == 'o') {
                this.match("Token");
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                token4 = this._returnToken;
                this.match(')');
                String string = "_token = " + token4.getText();
                this.text.setLength(n);
                this.text.append(string);
            } else {
                if (this.LA(1) != 'T' || this.LA(2) != 'y') throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                this.match("Type");
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                token5 = this._returnToken;
                this.match(')');
                String string = "_ttype = " + token5.getText();
                this.text.setLength(n);
                this.text.append(string);
            }
        } else if (this.LA(1) == '$' && this.LA(2) == 'F' && this.LA(3) == 'O') {
            String string;
            this.match("$FOLLOW");
            if (_tokenSet_5.member(this.LA(1)) && _tokenSet_6.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                token6 = this._returnToken;
                this.match(')');
            }
            String string2 = this.currentRule.getRuleName();
            if (token6 != null) {
                string2 = token6.getText();
            }
            if ((string = this.generator.getFOLLOWBitSet(string2, 1)) == null) {
                this.reportError("$FOLLOW(" + string2 + ")" + ": unknown rule or bad lookahead computation");
            } else {
                this.text.setLength(n);
                this.text.append(string);
            }
        } else if (this.LA(1) == '$' && this.LA(2) == 'F' && this.LA(3) == 'I') {
            String string;
            this.match("$FIRST");
            if (_tokenSet_5.member(this.LA(1)) && _tokenSet_6.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                token7 = this._returnToken;
                this.match(')');
            }
            String string3 = this.currentRule.getRuleName();
            if (token7 != null) {
                string3 = token7.getText();
            }
            if ((string = this.generator.getFIRSTBitSet(string3, 1)) == null) {
                this.reportError("$FIRST(" + string3 + ")" + ": unknown rule or bad lookahead computation");
            } else {
                this.text.setLength(n);
                this.text.append(string);
            }
        } else if (this.LA(1) == '$' && this.LA(2) == 's' && this.LA(3) == 'k') {
            this.match("$skip");
            this.text.setLength(n);
            this.text.append("_ttype = SKIP");
        } else if (this.LA(1) == '$' && this.LA(2) == 'a') {
            this.match("$append");
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '(': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            this.match('(');
            this.mTEXT_ARG(true);
            token2 = this._returnToken;
            this.match(')');
            String string = "self.text.append(" + token2.getText() + ")";
            this.text.setLength(n);
            this.text.append(string);
        } else if (this.LA(1) == '$' && this.LA(2) == 'g') {
            this.match("$getText");
            this.text.setLength(n);
            this.text.append("self.text.getString(_begin)");
        } else {
            if (this.LA(1) != '$' || this.LA(2) != 'n') throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            if (this.LA(1) == '$' && this.LA(2) == 'n' && this.LA(3) == 'l') {
                this.match("$nl");
            } else {
                if (this.LA(1) != '$' || this.LA(2) != 'n' || this.LA(3) != 'e') throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                this.match("$newline");
            }
            this.text.setLength(n);
            this.text.append("self.newline()");
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
        int n2 = 19;
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

    protected final void mSTRING(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 24;
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

    protected final void mCHAR(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 23;
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

    protected final void mTREE(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 8;
        Token token2 = null;
        Token token3 = null;
        StringBuffer stringBuffer = new StringBuffer();
        boolean bl2 = false;
        Vector vector = new Vector(10);
        int n3 = this.text.length();
        this.match('(');
        this.text.setLength(n3);
        switch (this.LA(1)) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                n3 = this.text.length();
                this.mWS(false);
                this.text.setLength(n3);
                break;
            }
            case '\"': 
            case '#': 
            case '(': 
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
            case 'Z': 
            case '[': 
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
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        n3 = this.text.length();
        this.mTREE_ELEMENT(true);
        this.text.setLength(n3);
        token2 = this._returnToken;
        vector.appendElement(token2.getText());
        switch (this.LA(1)) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                n3 = this.text.length();
                this.mWS(false);
                this.text.setLength(n3);
                break;
            }
            case ')': 
            case ',': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        block16: while (this.LA(1) == ',') {
            n3 = this.text.length();
            this.match(',');
            this.text.setLength(n3);
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    n3 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(n3);
                    break;
                }
                case '\"': 
                case '#': 
                case '(': 
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
                case 'Z': 
                case '[': 
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
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            n3 = this.text.length();
            this.mTREE_ELEMENT(true);
            this.text.setLength(n3);
            token3 = this._returnToken;
            vector.appendElement(token3.getText());
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    n3 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(n3);
                    continue block16;
                }
                case ')': 
                case ',': {
                    continue block16;
                }
            }
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.text.setLength(n);
        this.text.append(this.generator.getASTCreateString(vector));
        n3 = this.text.length();
        this.match(')');
        this.text.setLength(n3);
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mID(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 17;
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
        block11: while (_tokenSet_9.member(this.LA(1))) {
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
                case '_': {
                    this.match('_');
                    continue block11;
                }
            }
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mWS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 29;
        int n3 = 0;
        while (true) {
            if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                this.match('\r');
                this.match('\n');
                this.newline();
            } else if (this.LA(1) == ' ') {
                this.match(' ');
            } else if (this.LA(1) == '\t') {
                this.match('\t');
            } else if (this.LA(1) == '\r') {
                this.match('\r');
                this.newline();
            } else if (this.LA(1) == '\n') {
                this.match('\n');
                this.newline();
            } else {
                if (n3 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            ++n3;
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mVAR_ASSIGN(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 18;
        this.match('=');
        if (this.LA(1) != '=' && this.transInfo != null && this.transInfo.refRuleRoot != null) {
            this.transInfo.assignToRoot = true;
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mAST_CONSTRUCTOR(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        int n;
        Token token;
        Token token2;
        Token token3;
        int n2;
        int n3;
        Token token4;
        block33: {
            block32: {
                token4 = null;
                n3 = this.text.length();
                n2 = 10;
                token3 = null;
                token2 = null;
                token = null;
                n = this.text.length();
                this.match('[');
                this.text.setLength(n);
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
                    case '\"': 
                    case '#': 
                    case '(': 
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
                    case 'Z': 
                    case '[': 
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
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                n = this.text.length();
                this.mAST_CTOR_ELEMENT(true);
                this.text.setLength(n);
                token3 = this._returnToken;
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
                    case ',': 
                    case ']': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                if (this.LA(1) != ',' || !_tokenSet_10.member(this.LA(2)) || this.LA(3) < '\u0003' || this.LA(3) > '\u00ff') break block32;
                n = this.text.length();
                this.match(',');
                this.text.setLength(n);
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
                    case '\"': 
                    case '#': 
                    case '(': 
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
                    case 'Z': 
                    case '[': 
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
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                n = this.text.length();
                this.mAST_CTOR_ELEMENT(true);
                this.text.setLength(n);
                token2 = this._returnToken;
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        n = this.text.length();
                        this.mWS(false);
                        this.text.setLength(n);
                        break block33;
                    }
                    case ',': 
                    case ']': {
                        break block33;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
            }
            if (this.LA(1) != ',' && this.LA(1) != ']') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        block16 : switch (this.LA(1)) {
            case ',': {
                n = this.text.length();
                this.match(',');
                this.text.setLength(n);
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
                    case '\"': 
                    case '#': 
                    case '(': 
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
                    case 'Z': 
                    case '[': 
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
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                n = this.text.length();
                this.mAST_CTOR_ELEMENT(true);
                this.text.setLength(n);
                token = this._returnToken;
                switch (this.LA(1)) {
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        n = this.text.length();
                        this.mWS(false);
                        this.text.setLength(n);
                        break block16;
                    }
                    case ']': {
                        break block16;
                    }
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case ']': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        n = this.text.length();
        this.match(']');
        this.text.setLength(n);
        String string = token3.getText();
        if (token2 != null) {
            string = string + "," + token2.getText();
        }
        if (token != null) {
            string = string + "," + token.getText();
        }
        this.text.setLength(n3);
        this.text.append(this.generator.getASTCreateString(null, string));
        if (bl && token4 == null && n2 != -1) {
            token4 = this.makeToken(n2);
            token4.setText(new String(this.text.getBuffer(), n3, this.text.length() - n3));
        }
        this._returnToken = token4;
    }

    protected final void mTEXT_ARG(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 13;
        switch (this.LA(1)) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                this.mWS(false);
                break;
            }
            case '\"': 
            case '$': 
            case '\'': 
            case '+': 
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
            case 'Z': 
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
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        int n3 = 0;
        while (true) {
            if (_tokenSet_11.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.mTEXT_ARG_ELEMENT(false);
                if (_tokenSet_4.member(this.LA(1)) && _tokenSet_12.member(this.LA(2))) {
                    this.mWS(false);
                } else if (!_tokenSet_12.member(this.LA(1))) {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            } else {
                if (n3 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            ++n3;
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mTREE_ELEMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 9;
        Token token2 = null;
        switch (this.LA(1)) {
            case '(': {
                this.mTREE(false);
                break;
            }
            case '[': {
                this.mAST_CONSTRUCTOR(false);
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
            case 'Z': 
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
                this.mID_ELEMENT(false);
                break;
            }
            case '\"': {
                this.mSTRING(false);
                break;
            }
            default: {
                if (this.LA(1) == '#' && this.LA(2) == '(') {
                    int n3 = this.text.length();
                    this.match('#');
                    this.text.setLength(n3);
                    this.mTREE(false);
                    break;
                }
                if (this.LA(1) == '#' && this.LA(2) == '[') {
                    int n4 = this.text.length();
                    this.match('#');
                    this.text.setLength(n4);
                    this.mAST_CONSTRUCTOR(false);
                    break;
                }
                if (this.LA(1) == '#' && _tokenSet_3.member(this.LA(2))) {
                    int n5 = this.text.length();
                    this.match('#');
                    this.text.setLength(n5);
                    boolean bl2 = this.mID_ELEMENT(true);
                    token2 = this._returnToken;
                    if (bl2) break;
                    String string = this.generator.mapTreeId(token2.getText(), null);
                    this.text.setLength(n);
                    this.text.append(string);
                    break;
                }
                if (this.LA(1) == '#' && this.LA(2) == '#') {
                    this.match("##");
                    String string = this.currentRule.getRuleName() + "_AST";
                    this.text.setLength(n);
                    this.text.append(string);
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

    protected final boolean mID_ELEMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        int n;
        boolean bl2 = false;
        Token token = null;
        int n2 = this.text.length();
        int n3 = 12;
        Token token2 = null;
        this.mID(true);
        token2 = this._returnToken;
        if (_tokenSet_4.member(this.LA(1)) && _tokenSet_13.member(this.LA(2))) {
            n = this.text.length();
            this.mWS(false);
            this.text.setLength(n);
        } else if (!_tokenSet_13.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        block0 : switch (this.LA(1)) {
            case '(': {
                this.match('(');
                if (_tokenSet_4.member(this.LA(1)) && _tokenSet_14.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    n = this.text.length();
                    this.mWS(false);
                    this.text.setLength(n);
                } else if (!_tokenSet_14.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                switch (this.LA(1)) {
                    case '\"': 
                    case '#': 
                    case '\'': 
                    case '(': 
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
                    case 'Z': 
                    case '[': 
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
                        this.mARG(false);
                        while (this.LA(1) == ',') {
                            this.match(',');
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
                                case '\"': 
                                case '#': 
                                case '\'': 
                                case '(': 
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
                                case 'Z': 
                                case '[': 
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
                                    break;
                                }
                                default: {
                                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                }
                            }
                            this.mARG(false);
                        }
                        break;
                    }
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': 
                    case ')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
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
                    case ')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match(')');
                break;
            }
            case '[': {
                int n4 = 0;
                while (true) {
                    if (this.LA(1) == '[') {
                        this.match('[');
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
                            case '\"': 
                            case '#': 
                            case '\'': 
                            case '(': 
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
                            case 'Z': 
                            case '[': 
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
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                        this.mARG(false);
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
                            case ']': {
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                    } else {
                        if (n4 >= 1) break block0;
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    this.match(']');
                    ++n4;
                }
            }
            case '.': {
                this.match('.');
                this.mID_ELEMENT(false);
                break;
            }
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': 
            case ')': 
            case '*': 
            case '+': 
            case ',': 
            case '-': 
            case '/': 
            case '=': 
            case ']': {
                bl2 = true;
                String string = this.generator.mapTreeId(token2.getText(), this.transInfo);
                this.text.setLength(n2);
                this.text.append(string);
                if (_tokenSet_15.member(this.LA(1)) && _tokenSet_16.member(this.LA(2)) && this.transInfo != null && this.transInfo.refRuleRoot != null) {
                    switch (this.LA(1)) {
                        case '\t': 
                        case '\n': 
                        case '\r': 
                        case ' ': {
                            this.mWS(false);
                            break;
                        }
                        case '=': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.mVAR_ASSIGN(false);
                    break;
                }
                if (_tokenSet_17.member(this.LA(1))) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (bl && token == null && n3 != -1) {
            token = this.makeToken(n3);
            token.setText(new String(this.text.getBuffer(), n2, this.text.length() - n2));
        }
        this._returnToken = token;
        return bl2;
    }

    protected final void mAST_CTOR_ELEMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 11;
        if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
            this.mSTRING(false);
        } else if (_tokenSet_18.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.mTREE_ELEMENT(false);
        } else if (this.LA(1) >= '0' && this.LA(1) <= '9') {
            this.mINT(false);
        } else {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mINT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 27;
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

    protected final void mARG(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 16;
        switch (this.LA(1)) {
            case '\'': {
                this.mCHAR(false);
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
                this.mINT_OR_FLOAT(false);
                break;
            }
            default: {
                if (_tokenSet_18.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.mTREE_ELEMENT(false);
                    break;
                }
                if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.mSTRING(false);
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        while (_tokenSet_19.member(this.LA(1)) && _tokenSet_20.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '*': 
                case '+': 
                case '-': 
                case '/': {
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
                case '*': {
                    this.match('*');
                    break;
                }
                case '/': {
                    this.match('/');
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            switch (this.LA(1)) {
                case '\t': 
                case '\n': 
                case '\r': 
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '\"': 
                case '#': 
                case '\'': 
                case '(': 
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
                case 'Z': 
                case '[': 
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
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            this.mARG(false);
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mTEXT_ARG_ELEMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 14;
        switch (this.LA(1)) {
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
            case 'Z': 
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
                this.mTEXT_ARG_ID_ELEMENT(false);
                break;
            }
            case '\"': {
                this.mSTRING(false);
                break;
            }
            case '\'': {
                this.mCHAR(false);
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
                this.mINT_OR_FLOAT(false);
                break;
            }
            case '$': {
                this.mTEXT_ITEM(false);
                break;
            }
            case '+': {
                this.match('+');
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

    protected final void mTEXT_ARG_ID_ELEMENT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        int n;
        Token token = null;
        int n2 = this.text.length();
        int n3 = 15;
        Token token2 = null;
        this.mID(true);
        token2 = this._returnToken;
        if (_tokenSet_4.member(this.LA(1)) && _tokenSet_21.member(this.LA(2))) {
            n = this.text.length();
            this.mWS(false);
            this.text.setLength(n);
        } else if (!_tokenSet_21.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        block0 : switch (this.LA(1)) {
            case '(': {
                this.match('(');
                if (_tokenSet_4.member(this.LA(1)) && _tokenSet_22.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    n = this.text.length();
                    this.mWS(false);
                    this.text.setLength(n);
                } else if (!_tokenSet_22.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                while (_tokenSet_23.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.mTEXT_ARG(false);
                    while (this.LA(1) == ',') {
                        this.match(',');
                        this.mTEXT_ARG(false);
                    }
                }
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
                    case ')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match(')');
                break;
            }
            case '[': {
                int n4 = 0;
                while (true) {
                    if (this.LA(1) == '[') {
                        this.match('[');
                        if (_tokenSet_4.member(this.LA(1)) && _tokenSet_23.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                            n = this.text.length();
                            this.mWS(false);
                            this.text.setLength(n);
                        } else if (!_tokenSet_23.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff' || this.LA(3) < '\u0003' || this.LA(3) > '\u00ff') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        this.mTEXT_ARG(false);
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
                            case ']': {
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                    } else {
                        if (n4 >= 1) break block0;
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    this.match(']');
                    ++n4;
                }
            }
            case '.': {
                this.match('.');
                this.mTEXT_ARG_ID_ELEMENT(false);
                break;
            }
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': 
            case '\"': 
            case '$': 
            case '\'': 
            case ')': 
            case '+': 
            case ',': 
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
            case 'Z': 
            case ']': 
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
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (bl && token == null && n3 != -1) {
            token = this.makeToken(n3);
            token.setText(new String(this.text.getBuffer(), n2, this.text.length() - n2));
        }
        this._returnToken = token;
    }

    protected final void mINT_OR_FLOAT(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 28;
        int n3 = 0;
        while (true) {
            if (this.LA(1) < '0' || this.LA(1) > '9' || !_tokenSet_24.member(this.LA(2))) {
                if (n3 >= 1) break;
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mDIGIT(false);
            ++n3;
        }
        if (this.LA(1) == 'L' && _tokenSet_25.member(this.LA(2))) {
            this.match('L');
        } else if (this.LA(1) == 'l' && _tokenSet_25.member(this.LA(2))) {
            this.match('l');
        } else if (this.LA(1) == '.') {
            this.match('.');
            while (this.LA(1) >= '0' && this.LA(1) <= '9' && _tokenSet_25.member(this.LA(2))) {
                this.mDIGIT(false);
            }
        } else if (!_tokenSet_25.member(this.LA(1))) {
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
        int n2 = 20;
        this.match("//");
        this.text.setLength(n);
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
        int n;
        Token token = null;
        int n2 = this.text.length();
        int n3 = 22;
        this.match("/*");
        this.text.setLength(n2);
        this.text.append("#");
        while (this.LA(1) != '*' || this.LA(2) != '/') {
            if (this.LA(1) == '\r' && this.LA(2) == '\n' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                this.match('\r');
                this.match('\n');
                n = this.text.length();
                this.mIGNWS(false);
                this.text.setLength(n);
                this.newline();
                this.text.append("# ");
                continue;
            }
            if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                this.match('\r');
                n = this.text.length();
                this.mIGNWS(false);
                this.text.setLength(n);
                this.newline();
                this.text.append("# ");
                continue;
            }
            if (this.LA(1) == '\n' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                this.match('\n');
                n = this.text.length();
                this.mIGNWS(false);
                this.text.setLength(n);
                this.newline();
                this.text.append("# ");
                continue;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff' || this.LA(3) < '\u0003' || this.LA(3) > '\u00ff') break;
            this.matchNot('\uffff');
        }
        this.text.append("\n");
        n = this.text.length();
        this.match("*/");
        this.text.setLength(n);
        if (bl && token == null && n3 != -1) {
            token = this.makeToken(n3);
            token.setText(new String(this.text.getBuffer(), n2, this.text.length() - n2));
        }
        this._returnToken = token;
    }

    protected final void mIGNWS(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 21;
        while (true) {
            if (this.LA(1) == ' ' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                this.match(' ');
                continue;
            }
            if (this.LA(1) != '\t' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff' || this.LA(3) < '\u0003' || this.LA(3) > '\u00ff') break;
            this.match('\t');
        }
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    protected final void mESC(boolean bl) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        int n = this.text.length();
        int n2 = 25;
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
        int n2 = 26;
        this.matchRange('0', '9');
        if (bl && token == null && n2 != -1) {
            token = this.makeToken(n2);
            token.setText(new String(this.text.getBuffer(), n, this.text.length() - n));
        }
        this._returnToken = token;
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[8];
        lArray[0] = -103079215112L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[8];
        lArray[0] = -145135534866440L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_2() {
        long[] lArray = new long[8];
        lArray[0] = -141407503262728L;
        for (int i = 1; i <= 3; ++i) {
            lArray[i] = -1L;
        }
        return lArray;
    }

    private static final long[] mk_tokenSet_3() {
        long[] lArray = new long[]{0L, 576460745995190270L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_4() {
        long[] lArray = new long[]{4294977024L, 0L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_5() {
        long[] lArray = new long[]{1103806604800L, 0L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_6() {
        long[] lArray = new long[]{287959436729787904L, 576460745995190270L, 0L, 0L, 0L};
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
        long[] lArray = new long[]{0x3FF000000000000L, 576460745995190270L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_10() {
        long[] lArray = new long[]{287950056521213440L, 576460746129407998L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_11() {
        long[] lArray = new long[]{287958332923183104L, 576460745995190270L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_12() {
        long[] lArray = new long[]{287978128427460096L, 576460746532061182L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_13() {
        long[] lArray = new long[]{2306123388973753856L, 0x28000000L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_14() {
        long[] lArray = new long[]{287952805300282880L, 576460746129407998L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_15() {
        long[] lArray = new long[]{2305843013508670976L, 0L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_16() {
        long[] lArray = new long[]{2306051920717948416L, 0x20000000L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_17() {
        long[] lArray = new long[]{208911504254464L, 0x20000000L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_18() {
        long[] lArray = new long[]{0x10C00000000L, 576460746129407998L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_19() {
        long[] lArray = new long[]{189120294954496L, 0L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_20() {
        long[] lArray = new long[]{288139722277004800L, 576460746129407998L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_21() {
        long[] lArray = new long[]{288049596683265536L, 576460746666278910L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_22() {
        long[] lArray = new long[]{287960536241415680L, 576460745995190270L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_23() {
        long[] lArray = new long[]{287958337218160128L, 576460745995190270L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_24() {
        long[] lArray = new long[]{288228817078593024L, 576460746532061182L, 0L, 0L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_25() {
        long[] lArray = new long[]{288158448334415360L, 576460746532061182L, 0L, 0L, 0L};
        return lArray;
    }
}

