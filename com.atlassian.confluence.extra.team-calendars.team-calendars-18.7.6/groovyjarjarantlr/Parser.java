/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ASTFactory;
import groovyjarjarantlr.MismatchedTokenException;
import groovyjarjarantlr.ParserSharedInputState;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenBuffer;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.debug.MessageListener;
import groovyjarjarantlr.debug.ParserListener;
import groovyjarjarantlr.debug.ParserMatchListener;
import groovyjarjarantlr.debug.ParserTokenListener;
import groovyjarjarantlr.debug.SemanticPredicateListener;
import groovyjarjarantlr.debug.SyntacticPredicateListener;
import groovyjarjarantlr.debug.TraceListener;
import java.util.Hashtable;

public abstract class Parser {
    protected ParserSharedInputState inputState;
    protected String[] tokenNames;
    protected AST returnAST;
    protected ASTFactory astFactory = null;
    protected Hashtable tokenTypeToASTClassMap = null;
    private boolean ignoreInvalidDebugCalls = false;
    protected int traceDepth = 0;

    public Parser() {
        this(new ParserSharedInputState());
    }

    public Parser(ParserSharedInputState parserSharedInputState) {
        this.inputState = parserSharedInputState;
    }

    public Hashtable getTokenTypeToASTClassMap() {
        return this.tokenTypeToASTClassMap;
    }

    public void addMessageListener(MessageListener messageListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addMessageListener() is only valid if parser built for debugging");
        }
    }

    public void addParserListener(ParserListener parserListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addParserListener() is only valid if parser built for debugging");
        }
    }

    public void addParserMatchListener(ParserMatchListener parserMatchListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addParserMatchListener() is only valid if parser built for debugging");
        }
    }

    public void addParserTokenListener(ParserTokenListener parserTokenListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addParserTokenListener() is only valid if parser built for debugging");
        }
    }

    public void addSemanticPredicateListener(SemanticPredicateListener semanticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addSemanticPredicateListener() is only valid if parser built for debugging");
        }
    }

    public void addSyntacticPredicateListener(SyntacticPredicateListener syntacticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addSyntacticPredicateListener() is only valid if parser built for debugging");
        }
    }

    public void addTraceListener(TraceListener traceListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addTraceListener() is only valid if parser built for debugging");
        }
    }

    public abstract void consume() throws TokenStreamException;

    public void consumeUntil(int n) throws TokenStreamException {
        while (this.LA(1) != 1 && this.LA(1) != n) {
            this.consume();
        }
    }

    public void consumeUntil(BitSet bitSet) throws TokenStreamException {
        while (this.LA(1) != 1 && !bitSet.member(this.LA(1))) {
            this.consume();
        }
    }

    protected void defaultDebuggingSetup(TokenStream tokenStream, TokenBuffer tokenBuffer) {
    }

    public AST getAST() {
        return this.returnAST;
    }

    public ASTFactory getASTFactory() {
        return this.astFactory;
    }

    public String getFilename() {
        return this.inputState.filename;
    }

    public ParserSharedInputState getInputState() {
        return this.inputState;
    }

    public void setInputState(ParserSharedInputState parserSharedInputState) {
        this.inputState = parserSharedInputState;
    }

    public String getTokenName(int n) {
        return this.tokenNames[n];
    }

    public String[] getTokenNames() {
        return this.tokenNames;
    }

    public boolean isDebugMode() {
        return false;
    }

    public abstract int LA(int var1) throws TokenStreamException;

    public abstract Token LT(int var1) throws TokenStreamException;

    public int mark() {
        return this.inputState.input.mark();
    }

    public void match(int n) throws MismatchedTokenException, TokenStreamException {
        if (this.LA(1) != n) {
            throw new MismatchedTokenException(this.tokenNames, this.LT(1), n, false, this.getFilename());
        }
        this.consume();
    }

    public void match(BitSet bitSet) throws MismatchedTokenException, TokenStreamException {
        if (!bitSet.member(this.LA(1))) {
            throw new MismatchedTokenException(this.tokenNames, this.LT(1), bitSet, false, this.getFilename());
        }
        this.consume();
    }

    public void matchNot(int n) throws MismatchedTokenException, TokenStreamException {
        if (this.LA(1) == n) {
            throw new MismatchedTokenException(this.tokenNames, this.LT(1), n, true, this.getFilename());
        }
        this.consume();
    }

    public static void panic() {
        System.err.println("Parser: panic");
        System.exit(1);
    }

    public void removeMessageListener(MessageListener messageListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeMessageListener() is only valid if parser built for debugging");
        }
    }

    public void removeParserListener(ParserListener parserListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeParserListener() is only valid if parser built for debugging");
        }
    }

    public void removeParserMatchListener(ParserMatchListener parserMatchListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeParserMatchListener() is only valid if parser built for debugging");
        }
    }

    public void removeParserTokenListener(ParserTokenListener parserTokenListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeParserTokenListener() is only valid if parser built for debugging");
        }
    }

    public void removeSemanticPredicateListener(SemanticPredicateListener semanticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("removeSemanticPredicateListener() is only valid if parser built for debugging");
        }
    }

    public void removeSyntacticPredicateListener(SyntacticPredicateListener syntacticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("removeSyntacticPredicateListener() is only valid if parser built for debugging");
        }
    }

    public void removeTraceListener(TraceListener traceListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeTraceListener() is only valid if parser built for debugging");
        }
    }

    public void reportError(RecognitionException recognitionException) {
        System.err.println(recognitionException);
    }

    public void reportError(String string) {
        if (this.getFilename() == null) {
            System.err.println("error: " + string);
        } else {
            System.err.println(this.getFilename() + ": error: " + string);
        }
    }

    public void reportWarning(String string) {
        if (this.getFilename() == null) {
            System.err.println("warning: " + string);
        } else {
            System.err.println(this.getFilename() + ": warning: " + string);
        }
    }

    public void recover(RecognitionException recognitionException, BitSet bitSet) throws TokenStreamException {
        this.consume();
        this.consumeUntil(bitSet);
    }

    public void rewind(int n) {
        this.inputState.input.rewind(n);
    }

    public void setASTFactory(ASTFactory aSTFactory) {
        this.astFactory = aSTFactory;
    }

    public void setASTNodeClass(String string) {
        this.astFactory.setASTNodeType(string);
    }

    public void setASTNodeType(String string) {
        this.setASTNodeClass(string);
    }

    public void setDebugMode(boolean bl) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("setDebugMode() only valid if parser built for debugging");
        }
    }

    public void setFilename(String string) {
        this.inputState.filename = string;
    }

    public void setIgnoreInvalidDebugCalls(boolean bl) {
        this.ignoreInvalidDebugCalls = bl;
    }

    public void setTokenBuffer(TokenBuffer tokenBuffer) {
        this.inputState.input = tokenBuffer;
    }

    public void traceIndent() {
        for (int i = 0; i < this.traceDepth; ++i) {
            System.out.print(" ");
        }
    }

    public void traceIn(String string) throws TokenStreamException {
        ++this.traceDepth;
        this.traceIndent();
        System.out.println("> " + string + "; LA(1)==" + this.LT(1).getText() + (this.inputState.guessing > 0 ? " [guessing]" : ""));
    }

    public void traceOut(String string) throws TokenStreamException {
        this.traceIndent();
        System.out.println("< " + string + "; LA(1)==" + this.LT(1).getText() + (this.inputState.guessing > 0 ? " [guessing]" : ""));
        --this.traceDepth;
    }
}

