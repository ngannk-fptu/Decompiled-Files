/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ANTLRGrammarParseBehavior;
import groovyjarjarantlr.ANTLRLexer;
import groovyjarjarantlr.ANTLRTokenTypes;
import groovyjarjarantlr.CommonToken;
import groovyjarjarantlr.LLkParser;
import groovyjarjarantlr.NoViableAltException;
import groovyjarjarantlr.ParserSharedInputState;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.StringUtils;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenBuffer;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.collections.impl.BitSet;

public class ANTLRParser
extends LLkParser
implements ANTLRTokenTypes {
    private static final boolean DEBUG_PARSER = false;
    ANTLRGrammarParseBehavior behavior;
    Tool antlrTool;
    protected int blockNesting = -1;
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"tokens\"", "\"header\"", "STRING_LITERAL", "ACTION", "DOC_COMMENT", "\"lexclass\"", "\"class\"", "\"extends\"", "\"Lexer\"", "\"TreeParser\"", "OPTIONS", "ASSIGN", "SEMI", "RCURLY", "\"charVocabulary\"", "CHAR_LITERAL", "INT", "OR", "RANGE", "TOKENS", "TOKEN_REF", "OPEN_ELEMENT_OPTION", "CLOSE_ELEMENT_OPTION", "LPAREN", "RPAREN", "\"Parser\"", "\"protected\"", "\"public\"", "\"private\"", "BANG", "ARG_ACTION", "\"returns\"", "COLON", "\"throws\"", "COMMA", "\"exception\"", "\"catch\"", "RULE_REF", "NOT_OP", "SEMPRED", "TREE_BEGIN", "QUESTION", "STAR", "PLUS", "IMPLIES", "CARET", "WILDCARD", "\"options\"", "WS", "COMMENT", "SL_COMMENT", "ML_COMMENT", "ESC", "DIGIT", "XDIGIT", "NESTED_ARG_ACTION", "NESTED_ACTION", "WS_LOOP", "INTERNAL_RULE_REF", "WS_OPT"};
    public static final BitSet _tokenSet_0 = new BitSet(ANTLRParser.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(ANTLRParser.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(ANTLRParser.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(ANTLRParser.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(ANTLRParser.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(ANTLRParser.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(ANTLRParser.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(ANTLRParser.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(ANTLRParser.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(ANTLRParser.mk_tokenSet_9());
    public static final BitSet _tokenSet_10 = new BitSet(ANTLRParser.mk_tokenSet_10());
    public static final BitSet _tokenSet_11 = new BitSet(ANTLRParser.mk_tokenSet_11());

    public ANTLRParser(TokenBuffer tokenBuffer, ANTLRGrammarParseBehavior aNTLRGrammarParseBehavior, Tool tool) {
        super(tokenBuffer, 1);
        this.tokenNames = _tokenNames;
        this.behavior = aNTLRGrammarParseBehavior;
        this.antlrTool = tool;
    }

    public void reportError(String string) {
        this.antlrTool.error(string, this.getFilename(), -1, -1);
    }

    public void reportError(RecognitionException recognitionException) {
        this.reportError(recognitionException, recognitionException.getErrorMessage());
    }

    public void reportError(RecognitionException recognitionException, String string) {
        this.antlrTool.error(string, recognitionException.getFilename(), recognitionException.getLine(), recognitionException.getColumn());
    }

    public void reportWarning(String string) {
        this.antlrTool.warning(string, this.getFilename(), -1, -1);
    }

    private boolean lastInRule() throws TokenStreamException {
        return this.blockNesting == 0 && (this.LA(1) == 16 || this.LA(1) == 39 || this.LA(1) == 21);
    }

    private void checkForMissingEndRule(Token token) {
        if (token.getColumn() == 1) {
            this.antlrTool.warning("did you forget to terminate previous rule?", this.getFilename(), token.getLine(), token.getColumn());
        }
    }

    protected ANTLRParser(TokenBuffer tokenBuffer, int n) {
        super(tokenBuffer, n);
        this.tokenNames = _tokenNames;
    }

    public ANTLRParser(TokenBuffer tokenBuffer) {
        this(tokenBuffer, 2);
    }

    protected ANTLRParser(TokenStream tokenStream, int n) {
        super(tokenStream, n);
        this.tokenNames = _tokenNames;
    }

    public ANTLRParser(TokenStream tokenStream) {
        this(tokenStream, 2);
    }

    public ANTLRParser(ParserSharedInputState parserSharedInputState) {
        super(parserSharedInputState, 2);
        this.tokenNames = _tokenNames;
    }

    public final void grammar() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        try {
            while (this.LA(1) == 5) {
                if (this.inputState.guessing == 0) {
                    token = null;
                }
                this.match(5);
                switch (this.LA(1)) {
                    case 6: {
                        token = this.LT(1);
                        this.match(6);
                        break;
                    }
                    case 7: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                token2 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing != 0) continue;
                this.behavior.refHeaderAction(token, token2);
            }
            switch (this.LA(1)) {
                case 14: {
                    this.fileOptionsSpec();
                    break;
                }
                case 1: 
                case 7: 
                case 8: 
                case 9: 
                case 10: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            while (this.LA(1) >= 7 && this.LA(1) <= 10) {
                this.classDef();
            }
            this.match(1);
        }
        catch (RecognitionException recognitionException) {
            if (this.inputState.guessing == 0) {
                this.reportError(recognitionException, "rule grammar trapped:\n" + recognitionException.toString());
                this.consumeUntil(1);
            }
            throw recognitionException;
        }
    }

    public final void fileOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            Token token = this.id();
            this.match(15);
            Token token2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setFileOption(token, token2, this.getInputState().filename);
            }
            this.match(16);
        }
        this.match(17);
    }

    public final void classDef() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        String string = null;
        try {
            int n;
            switch (this.LA(1)) {
                case 7: {
                    token = this.LT(1);
                    this.match(7);
                    if (this.inputState.guessing != 0) break;
                    this.behavior.refPreambleAction(token);
                    break;
                }
                case 8: 
                case 9: 
                case 10: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 8: {
                    token2 = this.LT(1);
                    this.match(8);
                    if (this.inputState.guessing != 0) break;
                    string = token2.getText();
                    break;
                }
                case 9: 
                case 10: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            boolean bl = false;
            if (!(this.LA(1) != 9 && this.LA(1) != 10 || this.LA(2) != 24 && this.LA(2) != 41)) {
                n = this.mark();
                bl = true;
                ++this.inputState.guessing;
                try {
                    switch (this.LA(1)) {
                        case 9: {
                            this.match(9);
                            break;
                        }
                        case 10: {
                            this.match(10);
                            this.id();
                            this.match(11);
                            this.match(12);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                }
                catch (RecognitionException recognitionException) {
                    bl = false;
                }
                this.rewind(n);
                --this.inputState.guessing;
            }
            if (bl) {
                this.lexerSpec(string);
            } else {
                n = 0;
                if (this.LA(1) == 10 && (this.LA(2) == 24 || this.LA(2) == 41)) {
                    int n2 = this.mark();
                    n = 1;
                    ++this.inputState.guessing;
                    try {
                        this.match(10);
                        this.id();
                        this.match(11);
                        this.match(13);
                    }
                    catch (RecognitionException recognitionException) {
                        n = 0;
                    }
                    this.rewind(n2);
                    --this.inputState.guessing;
                }
                if (n != 0) {
                    this.treeParserSpec(string);
                } else if (this.LA(1) == 10 && (this.LA(2) == 24 || this.LA(2) == 41)) {
                    this.parserSpec(string);
                } else {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.rules();
            if (this.inputState.guessing == 0) {
                this.behavior.endGrammar();
            }
        }
        catch (RecognitionException recognitionException) {
            if (this.inputState.guessing == 0) {
                if (recognitionException instanceof NoViableAltException) {
                    NoViableAltException noViableAltException = (NoViableAltException)recognitionException;
                    if (noViableAltException.token.getType() == 8) {
                        this.reportError(recognitionException, "JAVADOC comments may only prefix rules and grammars");
                    } else {
                        this.reportError(recognitionException, "rule classDef trapped:\n" + recognitionException.toString());
                    }
                } else {
                    this.reportError(recognitionException, "rule classDef trapped:\n" + recognitionException.toString());
                }
                this.behavior.abortGrammar();
                boolean bl = true;
                while (bl) {
                    this.consume();
                    switch (this.LA(1)) {
                        case 1: 
                        case 9: 
                        case 10: {
                            bl = false;
                        }
                    }
                }
            }
            throw recognitionException;
        }
    }

    public final Token id() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        switch (this.LA(1)) {
            case 24: {
                token = this.LT(1);
                this.match(24);
                if (this.inputState.guessing != 0) break;
                token3 = token;
                break;
            }
            case 41: {
                token2 = this.LT(1);
                this.match(41);
                if (this.inputState.guessing != 0) break;
                token3 = token2;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return token3;
    }

    public final void lexerSpec(String string) throws RecognitionException, TokenStreamException {
        Token token;
        Token token2 = null;
        Token token3 = null;
        String string2 = null;
        block0 : switch (this.LA(1)) {
            case 9: {
                token2 = this.LT(1);
                this.match(9);
                token = this.id();
                if (this.inputState.guessing != 0) break;
                this.antlrTool.warning("lexclass' is deprecated; use 'class X extends Lexer'", this.getFilename(), token2.getLine(), token2.getColumn());
                break;
            }
            case 10: {
                this.match(10);
                token = this.id();
                this.match(11);
                this.match(12);
                switch (this.LA(1)) {
                    case 27: {
                        string2 = this.superClass();
                        break block0;
                    }
                    case 16: {
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.startLexer(this.getFilename(), token, string2, string);
        }
        this.match(16);
        switch (this.LA(1)) {
            case 14: {
                this.lexerOptionsSpec();
                break;
            }
            case 7: 
            case 8: 
            case 23: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endOptions();
        }
        switch (this.LA(1)) {
            case 23: {
                this.tokensSpec();
                break;
            }
            case 7: 
            case 8: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                token3 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing != 0) break;
                this.behavior.refMemberAction(token3);
                break;
            }
            case 8: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void treeParserSpec(String string) throws RecognitionException, TokenStreamException {
        Token token = null;
        String string2 = null;
        this.match(10);
        Token token2 = this.id();
        this.match(11);
        this.match(13);
        switch (this.LA(1)) {
            case 27: {
                string2 = this.superClass();
                break;
            }
            case 16: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.startTreeWalker(this.getFilename(), token2, string2, string);
        }
        this.match(16);
        switch (this.LA(1)) {
            case 14: {
                this.treeParserOptionsSpec();
                break;
            }
            case 7: 
            case 8: 
            case 23: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endOptions();
        }
        switch (this.LA(1)) {
            case 23: {
                this.tokensSpec();
                break;
            }
            case 7: 
            case 8: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                token = this.LT(1);
                this.match(7);
                if (this.inputState.guessing != 0) break;
                this.behavior.refMemberAction(token);
                break;
            }
            case 8: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void parserSpec(String string) throws RecognitionException, TokenStreamException {
        Token token = null;
        String string2 = null;
        this.match(10);
        Token token2 = this.id();
        block0 : switch (this.LA(1)) {
            case 11: {
                this.match(11);
                this.match(29);
                switch (this.LA(1)) {
                    case 27: {
                        string2 = this.superClass();
                        break block0;
                    }
                    case 16: {
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 16: {
                if (this.inputState.guessing != 0) break;
                this.antlrTool.warning("use 'class X extends Parser'", this.getFilename(), token2.getLine(), token2.getColumn());
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.startParser(this.getFilename(), token2, string2, string);
        }
        this.match(16);
        switch (this.LA(1)) {
            case 14: {
                this.parserOptionsSpec();
                break;
            }
            case 7: 
            case 8: 
            case 23: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endOptions();
        }
        switch (this.LA(1)) {
            case 23: {
                this.tokensSpec();
                break;
            }
            case 7: 
            case 8: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                token = this.LT(1);
                this.match(7);
                if (this.inputState.guessing != 0) break;
                this.behavior.refMemberAction(token);
                break;
            }
            case 8: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void rules() throws RecognitionException, TokenStreamException {
        int n = 0;
        while (true) {
            if (!_tokenSet_0.member(this.LA(1)) || !_tokenSet_1.member(this.LA(2))) {
                if (n >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.rule();
            ++n;
        }
    }

    public final Token optionValue() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        switch (this.LA(1)) {
            case 24: 
            case 41: {
                token4 = this.qualifiedID();
                break;
            }
            case 6: {
                token = this.LT(1);
                this.match(6);
                if (this.inputState.guessing != 0) break;
                token4 = token;
                break;
            }
            case 19: {
                token2 = this.LT(1);
                this.match(19);
                if (this.inputState.guessing != 0) break;
                token4 = token2;
                break;
            }
            case 20: {
                token3 = this.LT(1);
                this.match(20);
                if (this.inputState.guessing != 0) break;
                token4 = token3;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return token4;
    }

    public final void parserOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            Token token = this.id();
            this.match(15);
            Token token2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setGrammarOption(token, token2);
            }
            this.match(16);
        }
        this.match(17);
    }

    public final void treeParserOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            Token token = this.id();
            this.match(15);
            Token token2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setGrammarOption(token, token2);
            }
            this.match(16);
        }
        this.match(17);
    }

    public final void lexerOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        block4: while (true) {
            switch (this.LA(1)) {
                case 18: {
                    this.match(18);
                    this.match(15);
                    BitSet bitSet = this.charSet();
                    this.match(16);
                    if (this.inputState.guessing != 0) continue block4;
                    this.behavior.setCharVocabulary(bitSet);
                    continue block4;
                }
                case 24: 
                case 41: {
                    Token token = this.id();
                    this.match(15);
                    Token token2 = this.optionValue();
                    if (this.inputState.guessing == 0) {
                        this.behavior.setGrammarOption(token, token2);
                    }
                    this.match(16);
                    continue block4;
                }
            }
            break;
        }
        this.match(17);
    }

    public final BitSet charSet() throws RecognitionException, TokenStreamException {
        BitSet bitSet = null;
        BitSet bitSet2 = null;
        bitSet = this.setBlockElement();
        while (this.LA(1) == 21) {
            this.match(21);
            bitSet2 = this.setBlockElement();
            if (this.inputState.guessing != 0) continue;
            bitSet.orInPlace(bitSet2);
        }
        return bitSet;
    }

    public final void subruleOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            Token token = this.id();
            this.match(15);
            Token token2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setSubruleOption(token, token2);
            }
            this.match(16);
        }
        this.match(17);
    }

    public final Token qualifiedID() throws RecognitionException, TokenStreamException {
        CommonToken commonToken = null;
        StringBuffer stringBuffer = new StringBuffer(30);
        Token token = this.id();
        if (this.inputState.guessing == 0) {
            stringBuffer.append(token.getText());
        }
        while (this.LA(1) == 50) {
            this.match(50);
            token = this.id();
            if (this.inputState.guessing != 0) continue;
            stringBuffer.append('.');
            stringBuffer.append(token.getText());
        }
        if (this.inputState.guessing == 0) {
            commonToken = new CommonToken(24, stringBuffer.toString());
            ((Token)commonToken).setLine(token.getLine());
        }
        return commonToken;
    }

    public final BitSet setBlockElement() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        BitSet bitSet = null;
        int n = 0;
        token = this.LT(1);
        this.match(19);
        if (this.inputState.guessing == 0) {
            n = ANTLRLexer.tokenTypeForCharLiteral(token.getText());
            bitSet = BitSet.of(n);
        }
        switch (this.LA(1)) {
            case 22: {
                this.match(22);
                token2 = this.LT(1);
                this.match(19);
                if (this.inputState.guessing != 0) break;
                int n2 = ANTLRLexer.tokenTypeForCharLiteral(token2.getText());
                if (n2 < n) {
                    this.antlrTool.error("Malformed range line ", this.getFilename(), token.getLine(), token.getColumn());
                }
                for (int i = n + 1; i <= n2; ++i) {
                    bitSet.add(i);
                }
                break;
            }
            case 16: 
            case 21: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return bitSet;
    }

    public final void tokensSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        this.match(23);
        int n = 0;
        while (true) {
            if (this.LA(1) == 6 || this.LA(1) == 24) {
                block0 : switch (this.LA(1)) {
                    case 24: {
                        if (this.inputState.guessing == 0) {
                            token2 = null;
                        }
                        token = this.LT(1);
                        this.match(24);
                        switch (this.LA(1)) {
                            case 15: {
                                this.match(15);
                                token2 = this.LT(1);
                                this.match(6);
                                break;
                            }
                            case 16: 
                            case 25: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        if (this.inputState.guessing == 0) {
                            this.behavior.defineToken(token, token2);
                        }
                        switch (this.LA(1)) {
                            case 25: {
                                this.tokensSpecOptions(token);
                                break block0;
                            }
                            case 16: {
                                break block0;
                            }
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    case 6: {
                        token3 = this.LT(1);
                        this.match(6);
                        if (this.inputState.guessing == 0) {
                            this.behavior.defineToken(null, token3);
                        }
                        switch (this.LA(1)) {
                            case 25: {
                                this.tokensSpecOptions(token3);
                                break block0;
                            }
                            case 16: {
                                break block0;
                            }
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            } else {
                if (n >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.match(16);
            ++n;
        }
        this.match(17);
    }

    public final void tokensSpecOptions(Token token) throws RecognitionException, TokenStreamException {
        Token token2 = null;
        Token token3 = null;
        this.match(25);
        token2 = this.id();
        this.match(15);
        token3 = this.optionValue();
        if (this.inputState.guessing == 0) {
            this.behavior.refTokensSpecElementOption(token, token2, token3);
        }
        while (this.LA(1) == 16) {
            this.match(16);
            token2 = this.id();
            this.match(15);
            token3 = this.optionValue();
            if (this.inputState.guessing != 0) continue;
            this.behavior.refTokensSpecElementOption(token, token2, token3);
        }
        this.match(26);
    }

    public final String superClass() throws RecognitionException, TokenStreamException {
        String string = null;
        this.match(27);
        if (this.inputState.guessing == 0) {
            string = this.LT(1).getText();
            string = StringUtils.stripFrontBack(string, "\"", "\"");
        }
        this.match(6);
        this.match(28);
        return string;
    }

    public final void rule() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Token token6 = null;
        Token token7 = null;
        String string = "public";
        String string2 = null;
        boolean bl = true;
        this.blockNesting = -1;
        switch (this.LA(1)) {
            case 8: {
                token = this.LT(1);
                this.match(8);
                if (this.inputState.guessing != 0) break;
                string2 = token.getText();
                break;
            }
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 30: {
                token2 = this.LT(1);
                this.match(30);
                if (this.inputState.guessing != 0) break;
                string = token2.getText();
                break;
            }
            case 31: {
                token3 = this.LT(1);
                this.match(31);
                if (this.inputState.guessing != 0) break;
                string = token3.getText();
                break;
            }
            case 32: {
                token4 = this.LT(1);
                this.match(32);
                if (this.inputState.guessing != 0) break;
                string = token4.getText();
                break;
            }
            case 24: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        Token token8 = this.id();
        switch (this.LA(1)) {
            case 33: {
                this.match(33);
                if (this.inputState.guessing != 0) break;
                bl = false;
                break;
            }
            case 7: 
            case 14: 
            case 34: 
            case 35: 
            case 36: 
            case 37: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.defineRuleName(token8, string, bl, string2);
        }
        switch (this.LA(1)) {
            case 34: {
                token5 = this.LT(1);
                this.match(34);
                if (this.inputState.guessing != 0) break;
                this.behavior.refArgAction(token5);
                break;
            }
            case 7: 
            case 14: 
            case 35: 
            case 36: 
            case 37: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 35: {
                this.match(35);
                token6 = this.LT(1);
                this.match(34);
                if (this.inputState.guessing != 0) break;
                this.behavior.refReturnAction(token6);
                break;
            }
            case 7: 
            case 14: 
            case 36: 
            case 37: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 37: {
                this.throwsSpec();
                break;
            }
            case 7: 
            case 14: 
            case 36: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 14: {
                this.ruleOptionsSpec();
                break;
            }
            case 7: 
            case 36: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                token7 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing != 0) break;
                this.behavior.refInitAction(token7);
                break;
            }
            case 36: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(36);
        this.block();
        this.match(16);
        switch (this.LA(1)) {
            case 39: {
                this.exceptionGroup();
                break;
            }
            case 1: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endRule(token8.getText());
        }
    }

    public final void throwsSpec() throws RecognitionException, TokenStreamException {
        String string = null;
        this.match(37);
        Token token = this.id();
        if (this.inputState.guessing == 0) {
            string = token.getText();
        }
        while (this.LA(1) == 38) {
            this.match(38);
            Token token2 = this.id();
            if (this.inputState.guessing != 0) continue;
            string = string + "," + token2.getText();
        }
        if (this.inputState.guessing == 0) {
            this.behavior.setUserExceptions(string);
        }
    }

    public final void ruleOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            Token token = this.id();
            this.match(15);
            Token token2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setRuleOption(token, token2);
            }
            this.match(16);
        }
        this.match(17);
    }

    public final void block() throws RecognitionException, TokenStreamException {
        if (this.inputState.guessing == 0) {
            ++this.blockNesting;
        }
        this.alternative();
        while (this.LA(1) == 21) {
            this.match(21);
            this.alternative();
        }
        if (this.inputState.guessing == 0) {
            --this.blockNesting;
        }
    }

    public final void exceptionGroup() throws RecognitionException, TokenStreamException {
        if (this.inputState.guessing == 0) {
            this.behavior.beginExceptionGroup();
        }
        int n = 0;
        while (true) {
            if (this.LA(1) != 39) {
                if (n >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.exceptionSpec();
            ++n;
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endExceptionGroup();
        }
    }

    public final void alternative() throws RecognitionException, TokenStreamException {
        boolean bl = true;
        switch (this.LA(1)) {
            case 33: {
                this.match(33);
                if (this.inputState.guessing != 0) break;
                bl = false;
                break;
            }
            case 6: 
            case 7: 
            case 16: 
            case 19: 
            case 21: 
            case 24: 
            case 27: 
            case 28: 
            case 39: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 50: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.beginAlt(bl);
        }
        while (_tokenSet_2.member(this.LA(1))) {
            this.element();
        }
        switch (this.LA(1)) {
            case 39: {
                this.exceptionSpecNoLabel();
                break;
            }
            case 16: 
            case 21: 
            case 28: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endAlt();
        }
    }

    public final void element() throws RecognitionException, TokenStreamException {
        this.elementNoOptionSpec();
        switch (this.LA(1)) {
            case 25: {
                this.elementOptionSpec();
                break;
            }
            case 6: 
            case 7: 
            case 16: 
            case 19: 
            case 21: 
            case 24: 
            case 27: 
            case 28: 
            case 39: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 50: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void exceptionSpecNoLabel() throws RecognitionException, TokenStreamException {
        this.match(39);
        if (this.inputState.guessing == 0) {
            this.behavior.beginExceptionSpec(null);
        }
        while (this.LA(1) == 40) {
            this.exceptionHandler();
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endExceptionSpec();
        }
    }

    public final void exceptionSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        this.match(39);
        switch (this.LA(1)) {
            case 34: {
                token = this.LT(1);
                this.match(34);
                if (this.inputState.guessing != 0) break;
                token2 = token;
                break;
            }
            case 1: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 24: 
            case 30: 
            case 31: 
            case 32: 
            case 39: 
            case 40: 
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.beginExceptionSpec(token2);
        }
        while (this.LA(1) == 40) {
            this.exceptionHandler();
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endExceptionSpec();
        }
    }

    public final void exceptionHandler() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        this.match(40);
        token = this.LT(1);
        this.match(34);
        token2 = this.LT(1);
        this.match(7);
        if (this.inputState.guessing == 0) {
            this.behavior.refExceptionHandler(token, token2);
        }
    }

    public final void elementNoOptionSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Token token6 = null;
        Token token7 = null;
        Token token8 = null;
        Token token9 = null;
        Token token10 = null;
        Token token11 = null;
        int n = 1;
        block0 : switch (this.LA(1)) {
            case 7: {
                token7 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing != 0) break;
                this.behavior.refAction(token7);
                break;
            }
            case 43: {
                token8 = this.LT(1);
                this.match(43);
                if (this.inputState.guessing != 0) break;
                this.behavior.refSemPred(token8);
                break;
            }
            case 44: {
                this.tree();
                break;
            }
            default: {
                if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 15) {
                    token10 = this.id();
                    this.match(15);
                    if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 36) {
                        token9 = this.id();
                        this.match(36);
                        if (this.inputState.guessing == 0) {
                            this.checkForMissingEndRule(token9);
                        }
                    } else if (this.LA(1) != 24 && this.LA(1) != 41 || !_tokenSet_3.member(this.LA(2))) {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    switch (this.LA(1)) {
                        case 41: {
                            token = this.LT(1);
                            this.match(41);
                            switch (this.LA(1)) {
                                case 34: {
                                    token2 = this.LT(1);
                                    this.match(34);
                                    if (this.inputState.guessing != 0) break;
                                    token11 = token2;
                                    break;
                                }
                                case 6: 
                                case 7: 
                                case 16: 
                                case 19: 
                                case 21: 
                                case 24: 
                                case 25: 
                                case 27: 
                                case 28: 
                                case 33: 
                                case 39: 
                                case 41: 
                                case 42: 
                                case 43: 
                                case 44: 
                                case 50: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            switch (this.LA(1)) {
                                case 33: {
                                    this.match(33);
                                    if (this.inputState.guessing != 0) break;
                                    n = 3;
                                    break;
                                }
                                case 6: 
                                case 7: 
                                case 16: 
                                case 19: 
                                case 21: 
                                case 24: 
                                case 25: 
                                case 27: 
                                case 28: 
                                case 39: 
                                case 41: 
                                case 42: 
                                case 43: 
                                case 44: 
                                case 50: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            if (this.inputState.guessing != 0) break block0;
                            this.behavior.refRule(token10, token, token9, token11, n);
                            break block0;
                        }
                        case 24: {
                            token3 = this.LT(1);
                            this.match(24);
                            switch (this.LA(1)) {
                                case 34: {
                                    token4 = this.LT(1);
                                    this.match(34);
                                    if (this.inputState.guessing != 0) break;
                                    token11 = token4;
                                    break;
                                }
                                case 6: 
                                case 7: 
                                case 16: 
                                case 19: 
                                case 21: 
                                case 24: 
                                case 25: 
                                case 27: 
                                case 28: 
                                case 39: 
                                case 41: 
                                case 42: 
                                case 43: 
                                case 44: 
                                case 50: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            if (this.inputState.guessing != 0) break block0;
                            this.behavior.refToken(token10, token3, token9, token11, false, n, this.lastInRule());
                            break block0;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                }
                if (_tokenSet_4.member(this.LA(1)) && _tokenSet_5.member(this.LA(2))) {
                    if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 36) {
                        token9 = this.id();
                        this.match(36);
                        if (this.inputState.guessing == 0) {
                            this.checkForMissingEndRule(token9);
                        }
                    } else if (!_tokenSet_4.member(this.LA(1)) || !_tokenSet_6.member(this.LA(2))) {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    switch (this.LA(1)) {
                        case 41: {
                            token5 = this.LT(1);
                            this.match(41);
                            switch (this.LA(1)) {
                                case 34: {
                                    token6 = this.LT(1);
                                    this.match(34);
                                    if (this.inputState.guessing != 0) break;
                                    token11 = token6;
                                    break;
                                }
                                case 6: 
                                case 7: 
                                case 16: 
                                case 19: 
                                case 21: 
                                case 24: 
                                case 25: 
                                case 27: 
                                case 28: 
                                case 33: 
                                case 39: 
                                case 41: 
                                case 42: 
                                case 43: 
                                case 44: 
                                case 50: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            switch (this.LA(1)) {
                                case 33: {
                                    this.match(33);
                                    if (this.inputState.guessing != 0) break;
                                    n = 3;
                                    break;
                                }
                                case 6: 
                                case 7: 
                                case 16: 
                                case 19: 
                                case 21: 
                                case 24: 
                                case 25: 
                                case 27: 
                                case 28: 
                                case 39: 
                                case 41: 
                                case 42: 
                                case 43: 
                                case 44: 
                                case 50: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            if (this.inputState.guessing != 0) break block0;
                            this.behavior.refRule(token10, token5, token9, token11, n);
                            break block0;
                        }
                        case 42: {
                            this.match(42);
                            switch (this.LA(1)) {
                                case 19: 
                                case 24: {
                                    this.notTerminal(token9);
                                    break block0;
                                }
                                case 27: {
                                    this.ebnf(token9, true);
                                    break block0;
                                }
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        case 27: {
                            this.ebnf(token9, false);
                            break block0;
                        }
                        default: {
                            if ((this.LA(1) == 6 || this.LA(1) == 19 || this.LA(1) == 24) && this.LA(2) == 22) {
                                this.range(token9);
                                break block0;
                            }
                            if (_tokenSet_7.member(this.LA(1)) && _tokenSet_8.member(this.LA(2))) {
                                this.terminal(token9);
                                break block0;
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void elementOptionSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        this.match(25);
        token = this.id();
        this.match(15);
        token2 = this.optionValue();
        if (this.inputState.guessing == 0) {
            this.behavior.refElementOption(token, token2);
        }
        while (this.LA(1) == 16) {
            this.match(16);
            token = this.id();
            this.match(15);
            token2 = this.optionValue();
            if (this.inputState.guessing != 0) continue;
            this.behavior.refElementOption(token, token2);
        }
        this.match(26);
    }

    public final void range(Token token) throws RecognitionException, TokenStreamException {
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Token token6 = null;
        Token token7 = null;
        Token token8 = null;
        Token token9 = null;
        int n = 1;
        switch (this.LA(1)) {
            case 19: {
                token2 = this.LT(1);
                this.match(19);
                this.match(22);
                token3 = this.LT(1);
                this.match(19);
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing != 0) break;
                        n = 3;
                        break;
                    }
                    case 6: 
                    case 7: 
                    case 16: 
                    case 19: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 27: 
                    case 28: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing != 0) break;
                this.behavior.refCharRange(token2, token3, token, n, this.lastInRule());
                break;
            }
            case 6: 
            case 24: {
                switch (this.LA(1)) {
                    case 24: {
                        token4 = this.LT(1);
                        this.match(24);
                        if (this.inputState.guessing != 0) break;
                        token8 = token4;
                        break;
                    }
                    case 6: {
                        token5 = this.LT(1);
                        this.match(6);
                        if (this.inputState.guessing != 0) break;
                        token8 = token5;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(22);
                switch (this.LA(1)) {
                    case 24: {
                        token6 = this.LT(1);
                        this.match(24);
                        if (this.inputState.guessing != 0) break;
                        token9 = token6;
                        break;
                    }
                    case 6: {
                        token7 = this.LT(1);
                        this.match(6);
                        if (this.inputState.guessing != 0) break;
                        token9 = token7;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                n = this.ast_type_spec();
                if (this.inputState.guessing != 0) break;
                this.behavior.refTokenRange(token8, token9, token, n, this.lastInRule());
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void terminal(Token token) throws RecognitionException, TokenStreamException {
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Token token6 = null;
        int n = 1;
        Token token7 = null;
        switch (this.LA(1)) {
            case 19: {
                token2 = this.LT(1);
                this.match(19);
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing != 0) break;
                        n = 3;
                        break;
                    }
                    case 6: 
                    case 7: 
                    case 16: 
                    case 19: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 27: 
                    case 28: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing != 0) break;
                this.behavior.refCharLiteral(token2, token, false, n, this.lastInRule());
                break;
            }
            case 24: {
                token3 = this.LT(1);
                this.match(24);
                n = this.ast_type_spec();
                switch (this.LA(1)) {
                    case 34: {
                        token4 = this.LT(1);
                        this.match(34);
                        if (this.inputState.guessing != 0) break;
                        token7 = token4;
                        break;
                    }
                    case 6: 
                    case 7: 
                    case 16: 
                    case 19: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 27: 
                    case 28: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing != 0) break;
                this.behavior.refToken(null, token3, token, token7, false, n, this.lastInRule());
                break;
            }
            case 6: {
                token5 = this.LT(1);
                this.match(6);
                n = this.ast_type_spec();
                if (this.inputState.guessing != 0) break;
                this.behavior.refStringLiteral(token5, token, n, this.lastInRule());
                break;
            }
            case 50: {
                token6 = this.LT(1);
                this.match(50);
                n = this.ast_type_spec();
                if (this.inputState.guessing != 0) break;
                this.behavior.refWildcard(token6, token, n);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void notTerminal(Token token) throws RecognitionException, TokenStreamException {
        Token token2 = null;
        Token token3 = null;
        int n = 1;
        switch (this.LA(1)) {
            case 19: {
                token2 = this.LT(1);
                this.match(19);
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing != 0) break;
                        n = 3;
                        break;
                    }
                    case 6: 
                    case 7: 
                    case 16: 
                    case 19: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 27: 
                    case 28: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing != 0) break;
                this.behavior.refCharLiteral(token2, token, true, n, this.lastInRule());
                break;
            }
            case 24: {
                token3 = this.LT(1);
                this.match(24);
                n = this.ast_type_spec();
                if (this.inputState.guessing != 0) break;
                this.behavior.refToken(null, token3, token, null, true, n, this.lastInRule());
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }

    public final void ebnf(Token token, boolean bl) throws RecognitionException, TokenStreamException {
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        token2 = this.LT(1);
        this.match(27);
        if (this.inputState.guessing == 0) {
            this.behavior.beginSubRule(token, token2, bl);
        }
        if (this.LA(1) == 14) {
            this.subruleOptionsSpec();
            switch (this.LA(1)) {
                case 7: {
                    token3 = this.LT(1);
                    this.match(7);
                    if (this.inputState.guessing != 0) break;
                    this.behavior.refInitAction(token3);
                    break;
                }
                case 36: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(36);
        } else if (this.LA(1) == 7 && this.LA(2) == 36) {
            token4 = this.LT(1);
            this.match(7);
            if (this.inputState.guessing == 0) {
                this.behavior.refInitAction(token4);
            }
            this.match(36);
        } else if (!_tokenSet_9.member(this.LA(1)) || !_tokenSet_10.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.block();
        this.match(28);
        block4 : switch (this.LA(1)) {
            case 6: 
            case 7: 
            case 16: 
            case 19: 
            case 21: 
            case 24: 
            case 25: 
            case 27: 
            case 28: 
            case 33: 
            case 39: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 45: 
            case 46: 
            case 47: 
            case 50: {
                switch (this.LA(1)) {
                    case 45: {
                        this.match(45);
                        if (this.inputState.guessing != 0) break;
                        this.behavior.optionalSubRule();
                        break;
                    }
                    case 46: {
                        this.match(46);
                        if (this.inputState.guessing != 0) break;
                        this.behavior.zeroOrMoreSubRule();
                        break;
                    }
                    case 47: {
                        this.match(47);
                        if (this.inputState.guessing != 0) break;
                        this.behavior.oneOrMoreSubRule();
                        break;
                    }
                    case 6: 
                    case 7: 
                    case 16: 
                    case 19: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 27: 
                    case 28: 
                    case 33: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing != 0) break block4;
                        this.behavior.noASTSubRule();
                        break block4;
                    }
                    case 6: 
                    case 7: 
                    case 16: 
                    case 19: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 27: 
                    case 28: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: 
                    case 50: {
                        break block4;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            case 48: {
                this.match(48);
                if (this.inputState.guessing != 0) break;
                this.behavior.synPred();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endSubRule();
        }
    }

    public final void tree() throws RecognitionException, TokenStreamException {
        Token token = null;
        token = this.LT(1);
        this.match(44);
        if (this.inputState.guessing == 0) {
            this.behavior.beginTree(token);
        }
        this.rootNode();
        if (this.inputState.guessing == 0) {
            this.behavior.beginChildList();
        }
        int n = 0;
        while (true) {
            if (!_tokenSet_2.member(this.LA(1))) {
                if (n >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.element();
            ++n;
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endChildList();
        }
        this.match(28);
        if (this.inputState.guessing == 0) {
            this.behavior.endTree();
        }
    }

    public final void rootNode() throws RecognitionException, TokenStreamException {
        Token token = null;
        if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 36) {
            token = this.id();
            this.match(36);
            if (this.inputState.guessing == 0) {
                this.checkForMissingEndRule(token);
            }
        } else if (!_tokenSet_7.member(this.LA(1)) || !_tokenSet_11.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.terminal(token);
    }

    public final int ast_type_spec() throws RecognitionException, TokenStreamException {
        int n = 1;
        switch (this.LA(1)) {
            case 49: {
                this.match(49);
                if (this.inputState.guessing != 0) break;
                n = 2;
                break;
            }
            case 33: {
                this.match(33);
                if (this.inputState.guessing != 0) break;
                n = 3;
                break;
            }
            case 6: 
            case 7: 
            case 16: 
            case 19: 
            case 21: 
            case 24: 
            case 25: 
            case 27: 
            case 28: 
            case 34: 
            case 39: 
            case 41: 
            case 42: 
            case 43: 
            case 44: 
            case 50: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return n;
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[]{2206556225792L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[]{2472844214400L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_2() {
        long[] lArray = new long[]{1158885407195328L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_3() {
        long[] lArray = new long[]{1159461236965568L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_4() {
        long[] lArray = new long[]{1132497128128576L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_5() {
        long[] lArray = new long[]{1722479914074304L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_6() {
        long[] lArray = new long[]{1722411194597568L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_7() {
        long[] lArray = new long[]{1125899924144192L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_8() {
        long[] lArray = new long[]{1722411190386880L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_9() {
        long[] lArray = new long[]{1159444023476416L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_10() {
        long[] lArray = new long[]{2251345007067328L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_11() {
        long[] lArray = new long[]{1721861130420416L, 0L};
        return lArray;
    }
}

