/*
 * Decompiled with CFR 0.152.
 */
package antlr.preprocessor;

import antlr.LLkParser;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.Tool;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.IndexedVector;
import antlr.preprocessor.Grammar;
import antlr.preprocessor.Hierarchy;
import antlr.preprocessor.Option;
import antlr.preprocessor.PreprocessorTokenTypes;
import antlr.preprocessor.Rule;

public class Preprocessor
extends LLkParser
implements PreprocessorTokenTypes {
    private Tool antlrTool;
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"tokens\"", "HEADER_ACTION", "SUBRULE_BLOCK", "ACTION", "\"class\"", "ID", "\"extends\"", "SEMI", "TOKENS_SPEC", "OPTIONS_START", "ASSIGN_RHS", "RCURLY", "\"protected\"", "\"private\"", "\"public\"", "BANG", "ARG_ACTION", "\"returns\"", "RULE_BLOCK", "\"throws\"", "COMMA", "\"exception\"", "\"catch\"", "ALT", "ELEMENT", "LPAREN", "RPAREN", "ID_OR_KEYWORD", "CURLY_BLOCK_SCARF", "WS", "NEWLINE", "COMMENT", "SL_COMMENT", "ML_COMMENT", "CHAR_LITERAL", "STRING_LITERAL", "ESC", "DIGIT", "XDIGIT"};
    public static final BitSet _tokenSet_0 = new BitSet(Preprocessor.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(Preprocessor.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(Preprocessor.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(Preprocessor.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(Preprocessor.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(Preprocessor.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(Preprocessor.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(Preprocessor.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(Preprocessor.mk_tokenSet_8());

    public void setTool(Tool tool) {
        if (this.antlrTool != null) {
            throw new IllegalStateException("antlr.Tool already registered");
        }
        this.antlrTool = tool;
    }

    protected Tool getTool() {
        return this.antlrTool;
    }

    public void reportError(String string) {
        if (this.getTool() != null) {
            this.getTool().error(string, this.getFilename(), -1, -1);
        } else {
            super.reportError(string);
        }
    }

    public void reportError(RecognitionException recognitionException) {
        if (this.getTool() != null) {
            this.getTool().error(recognitionException.getErrorMessage(), recognitionException.getFilename(), recognitionException.getLine(), recognitionException.getColumn());
        } else {
            super.reportError(recognitionException);
        }
    }

    public void reportWarning(String string) {
        if (this.getTool() != null) {
            this.getTool().warning(string, this.getFilename(), -1, -1);
        } else {
            super.reportWarning(string);
        }
    }

    protected Preprocessor(TokenBuffer tokenBuffer, int n) {
        super(tokenBuffer, n);
        this.tokenNames = _tokenNames;
    }

    public Preprocessor(TokenBuffer tokenBuffer) {
        this(tokenBuffer, 1);
    }

    protected Preprocessor(TokenStream tokenStream, int n) {
        super(tokenStream, n);
        this.tokenNames = _tokenNames;
    }

    public Preprocessor(TokenStream tokenStream) {
        this(tokenStream, 1);
    }

    public Preprocessor(ParserSharedInputState parserSharedInputState) {
        super(parserSharedInputState, 1);
        this.tokenNames = _tokenNames;
    }

    public final void grammarFile(Hierarchy hierarchy, String string) throws RecognitionException, TokenStreamException {
        Token token = null;
        IndexedVector indexedVector = null;
        try {
            while (this.LA(1) == 5) {
                token = this.LT(1);
                this.match(5);
                hierarchy.getFile(string).addHeaderAction(token.getText());
            }
            switch (this.LA(1)) {
                case 13: {
                    indexedVector = this.optionSpec(null);
                    break;
                }
                case 1: 
                case 7: 
                case 8: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            while (this.LA(1) == 7 || this.LA(1) == 8) {
                Grammar grammar = this.class_def(string, hierarchy);
                if (grammar != null && indexedVector != null) {
                    hierarchy.getFile(string).setOptions(indexedVector);
                }
                if (grammar == null) continue;
                grammar.setFileName(string);
                hierarchy.addGrammar(grammar);
            }
            this.match(1);
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_0);
        }
    }

    public final IndexedVector optionSpec(Grammar grammar) throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        IndexedVector indexedVector = new IndexedVector();
        try {
            this.match(13);
            while (this.LA(1) == 9) {
                token = this.LT(1);
                this.match(9);
                token2 = this.LT(1);
                this.match(14);
                Option option = new Option(token.getText(), token2.getText(), grammar);
                indexedVector.appendElement(option.getName(), option);
                if (grammar != null && token.getText().equals("importVocab")) {
                    grammar.specifiedVocabulary = true;
                    grammar.importVocab = token2.getText();
                    continue;
                }
                if (grammar == null || !token.getText().equals("exportVocab")) continue;
                grammar.exportVocab = token2.getText().substring(0, token2.getText().length() - 1);
                grammar.exportVocab = grammar.exportVocab.trim();
            }
            this.match(15);
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_1);
        }
        return indexedVector;
    }

    public final Grammar class_def(String string, Hierarchy hierarchy) throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        Grammar grammar = null;
        IndexedVector indexedVector = new IndexedVector(100);
        IndexedVector indexedVector2 = null;
        String string2 = null;
        try {
            switch (this.LA(1)) {
                case 7: {
                    token = this.LT(1);
                    this.match(7);
                    break;
                }
                case 8: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(8);
            token2 = this.LT(1);
            this.match(9);
            this.match(10);
            token3 = this.LT(1);
            this.match(9);
            switch (this.LA(1)) {
                case 6: {
                    string2 = this.superClass();
                    break;
                }
                case 11: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(11);
            grammar = hierarchy.getGrammar(token2.getText());
            if (grammar != null) {
                grammar = null;
                throw new SemanticException("redefinition of grammar " + token2.getText(), string, token2.getLine(), token2.getColumn());
            }
            grammar = new Grammar(hierarchy.getTool(), token2.getText(), token3.getText(), indexedVector);
            grammar.superClass = string2;
            if (token != null) {
                grammar.setPreambleAction(token.getText());
            }
            switch (this.LA(1)) {
                case 13: {
                    indexedVector2 = this.optionSpec(grammar);
                    break;
                }
                case 7: 
                case 9: 
                case 12: 
                case 16: 
                case 17: 
                case 18: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            if (grammar != null) {
                grammar.setOptions(indexedVector2);
            }
            switch (this.LA(1)) {
                case 12: {
                    token4 = this.LT(1);
                    this.match(12);
                    grammar.setTokenSection(token4.getText());
                    break;
                }
                case 7: 
                case 9: 
                case 16: 
                case 17: 
                case 18: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 7: {
                    token5 = this.LT(1);
                    this.match(7);
                    grammar.setMemberAction(token5.getText());
                    break;
                }
                case 9: 
                case 16: 
                case 17: 
                case 18: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            int n = 0;
            while (true) {
                if (!_tokenSet_2.member(this.LA(1))) {
                    if (n < 1) {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    break;
                }
                this.rule(grammar);
                ++n;
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_3);
        }
        return grammar;
    }

    public final String superClass() throws RecognitionException, TokenStreamException {
        String string = this.LT(1).getText();
        try {
            this.match(6);
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_4);
        }
        return string;
    }

    public final void rule(Grammar grammar) throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        Token token3 = null;
        Token token4 = null;
        Token token5 = null;
        IndexedVector indexedVector = null;
        String string = null;
        boolean bl = false;
        String string2 = null;
        String string3 = "";
        try {
            switch (this.LA(1)) {
                case 16: {
                    this.match(16);
                    string = "protected";
                    break;
                }
                case 17: {
                    this.match(17);
                    string = "private";
                    break;
                }
                case 18: {
                    this.match(18);
                    string = "public";
                    break;
                }
                case 9: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            token = this.LT(1);
            this.match(9);
            switch (this.LA(1)) {
                case 19: {
                    this.match(19);
                    bl = true;
                    break;
                }
                case 7: 
                case 13: 
                case 20: 
                case 21: 
                case 22: 
                case 23: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 20: {
                    token2 = this.LT(1);
                    this.match(20);
                    break;
                }
                case 7: 
                case 13: 
                case 21: 
                case 22: 
                case 23: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 21: {
                    this.match(21);
                    token3 = this.LT(1);
                    this.match(20);
                    break;
                }
                case 7: 
                case 13: 
                case 22: 
                case 23: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 23: {
                    string3 = this.throwsSpec();
                    break;
                }
                case 7: 
                case 13: 
                case 22: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 13: {
                    indexedVector = this.optionSpec(null);
                    break;
                }
                case 7: 
                case 22: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 7: {
                    token4 = this.LT(1);
                    this.match(7);
                    break;
                }
                case 22: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            token5 = this.LT(1);
            this.match(22);
            string2 = this.exceptionGroup();
            String string4 = token5.getText() + string2;
            Rule rule = new Rule(token.getText(), string4, indexedVector, grammar);
            rule.setThrowsSpec(string3);
            if (token2 != null) {
                rule.setArgs(token2.getText());
            }
            if (token3 != null) {
                rule.setReturnValue(token3.getText());
            }
            if (token4 != null) {
                rule.setInitAction(token4.getText());
            }
            if (bl) {
                rule.setBang();
            }
            rule.setVisibility(string);
            if (grammar != null) {
                grammar.addRule(rule);
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_5);
        }
    }

    public final String throwsSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        String string = "throws ";
        try {
            this.match(23);
            token = this.LT(1);
            this.match(9);
            string = string + token.getText();
            while (this.LA(1) == 24) {
                this.match(24);
                token2 = this.LT(1);
                this.match(9);
                string = string + "," + token2.getText();
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_6);
        }
        return string;
    }

    public final String exceptionGroup() throws RecognitionException, TokenStreamException {
        String string = null;
        String string2 = "";
        try {
            while (this.LA(1) == 25) {
                string = this.exceptionSpec();
                string2 = string2 + string;
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_5);
        }
        return string2;
    }

    public final String exceptionSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        String string = null;
        String string2 = System.getProperty("line.separator") + "exception ";
        try {
            this.match(25);
            switch (this.LA(1)) {
                case 20: {
                    token = this.LT(1);
                    this.match(20);
                    string2 = string2 + token.getText();
                    break;
                }
                case 1: 
                case 7: 
                case 8: 
                case 9: 
                case 16: 
                case 17: 
                case 18: 
                case 25: 
                case 26: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            while (this.LA(1) == 26) {
                string = this.exceptionHandler();
                string2 = string2 + string;
            }
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_7);
        }
        return string2;
    }

    public final String exceptionHandler() throws RecognitionException, TokenStreamException {
        Token token = null;
        Token token2 = null;
        String string = null;
        try {
            this.match(26);
            token = this.LT(1);
            this.match(20);
            token2 = this.LT(1);
            this.match(7);
            string = System.getProperty("line.separator") + "catch " + token.getText() + " " + token2.getText();
        }
        catch (RecognitionException recognitionException) {
            this.reportError(recognitionException);
            this.consume();
            this.consumeUntil(_tokenSet_8);
        }
        return string;
    }

    private static final long[] mk_tokenSet_0() {
        long[] lArray = new long[]{2L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_1() {
        long[] lArray = new long[]{4658050L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_2() {
        long[] lArray = new long[]{459264L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_3() {
        long[] lArray = new long[]{386L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_4() {
        long[] lArray = new long[]{2048L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_5() {
        long[] lArray = new long[]{459650L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_6() {
        long[] lArray = new long[]{4202624L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_7() {
        long[] lArray = new long[]{34014082L, 0L};
        return lArray;
    }

    private static final long[] mk_tokenSet_8() {
        long[] lArray = new long[]{101122946L, 0L};
        return lArray;
    }
}

