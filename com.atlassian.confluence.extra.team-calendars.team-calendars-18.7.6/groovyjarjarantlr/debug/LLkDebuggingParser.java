/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.LLkParser;
import groovyjarjarantlr.MismatchedTokenException;
import groovyjarjarantlr.ParserSharedInputState;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenBuffer;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.Utils;
import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.debug.DebuggingParser;
import groovyjarjarantlr.debug.MessageListener;
import groovyjarjarantlr.debug.ParserEventSupport;
import groovyjarjarantlr.debug.ParserListener;
import groovyjarjarantlr.debug.ParserMatchListener;
import groovyjarjarantlr.debug.ParserTokenListener;
import groovyjarjarantlr.debug.SemanticPredicateListener;
import groovyjarjarantlr.debug.SyntacticPredicateListener;
import groovyjarjarantlr.debug.TraceListener;
import java.lang.reflect.Constructor;

public class LLkDebuggingParser
extends LLkParser
implements DebuggingParser {
    protected ParserEventSupport parserEventSupport = new ParserEventSupport(this);
    private boolean _notDebugMode = false;
    protected String[] ruleNames;
    protected String[] semPredNames;
    static /* synthetic */ Class class$antlr$debug$LLkDebuggingParser;
    static /* synthetic */ Class class$antlr$TokenStream;
    static /* synthetic */ Class class$antlr$TokenBuffer;

    public LLkDebuggingParser(int n) {
        super(n);
    }

    public LLkDebuggingParser(ParserSharedInputState parserSharedInputState, int n) {
        super(parserSharedInputState, n);
    }

    public LLkDebuggingParser(TokenBuffer tokenBuffer, int n) {
        super(tokenBuffer, n);
    }

    public LLkDebuggingParser(TokenStream tokenStream, int n) {
        super(tokenStream, n);
    }

    public void addMessageListener(MessageListener messageListener) {
        this.parserEventSupport.addMessageListener(messageListener);
    }

    public void addParserListener(ParserListener parserListener) {
        this.parserEventSupport.addParserListener(parserListener);
    }

    public void addParserMatchListener(ParserMatchListener parserMatchListener) {
        this.parserEventSupport.addParserMatchListener(parserMatchListener);
    }

    public void addParserTokenListener(ParserTokenListener parserTokenListener) {
        this.parserEventSupport.addParserTokenListener(parserTokenListener);
    }

    public void addSemanticPredicateListener(SemanticPredicateListener semanticPredicateListener) {
        this.parserEventSupport.addSemanticPredicateListener(semanticPredicateListener);
    }

    public void addSyntacticPredicateListener(SyntacticPredicateListener syntacticPredicateListener) {
        this.parserEventSupport.addSyntacticPredicateListener(syntacticPredicateListener);
    }

    public void addTraceListener(TraceListener traceListener) {
        this.parserEventSupport.addTraceListener(traceListener);
    }

    public void consume() throws TokenStreamException {
        int n = -99;
        n = this.LA(1);
        super.consume();
        this.parserEventSupport.fireConsume(n);
    }

    protected void fireEnterRule(int n, int n2) {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireEnterRule(n, this.inputState.guessing, n2);
        }
    }

    protected void fireExitRule(int n, int n2) {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireExitRule(n, this.inputState.guessing, n2);
        }
    }

    protected boolean fireSemanticPredicateEvaluated(int n, int n2, boolean bl) {
        if (this.isDebugMode()) {
            return this.parserEventSupport.fireSemanticPredicateEvaluated(n, n2, bl, this.inputState.guessing);
        }
        return bl;
    }

    protected void fireSyntacticPredicateFailed() {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireSyntacticPredicateFailed(this.inputState.guessing);
        }
    }

    protected void fireSyntacticPredicateStarted() {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireSyntacticPredicateStarted(this.inputState.guessing);
        }
    }

    protected void fireSyntacticPredicateSucceeded() {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireSyntacticPredicateSucceeded(this.inputState.guessing);
        }
    }

    public String getRuleName(int n) {
        return this.ruleNames[n];
    }

    public String getSemPredName(int n) {
        return this.semPredNames[n];
    }

    public synchronized void goToSleep() {
        try {
            this.wait();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public boolean isDebugMode() {
        return !this._notDebugMode;
    }

    public boolean isGuessing() {
        return this.inputState.guessing > 0;
    }

    public int LA(int n) throws TokenStreamException {
        int n2 = super.LA(n);
        this.parserEventSupport.fireLA(n, n2);
        return n2;
    }

    public void match(int n) throws MismatchedTokenException, TokenStreamException {
        String string = this.LT(1).getText();
        int n2 = this.LA(1);
        try {
            super.match(n);
            this.parserEventSupport.fireMatch(n, string, this.inputState.guessing);
        }
        catch (MismatchedTokenException mismatchedTokenException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(n2, n, string, this.inputState.guessing);
            }
            throw mismatchedTokenException;
        }
    }

    public void match(BitSet bitSet) throws MismatchedTokenException, TokenStreamException {
        String string = this.LT(1).getText();
        int n = this.LA(1);
        try {
            super.match(bitSet);
            this.parserEventSupport.fireMatch(n, bitSet, string, this.inputState.guessing);
        }
        catch (MismatchedTokenException mismatchedTokenException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(n, bitSet, string, this.inputState.guessing);
            }
            throw mismatchedTokenException;
        }
    }

    public void matchNot(int n) throws MismatchedTokenException, TokenStreamException {
        String string = this.LT(1).getText();
        int n2 = this.LA(1);
        try {
            super.matchNot(n);
            this.parserEventSupport.fireMatchNot(n2, n, string, this.inputState.guessing);
        }
        catch (MismatchedTokenException mismatchedTokenException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatchNot(n2, n, string, this.inputState.guessing);
            }
            throw mismatchedTokenException;
        }
    }

    public void removeMessageListener(MessageListener messageListener) {
        this.parserEventSupport.removeMessageListener(messageListener);
    }

    public void removeParserListener(ParserListener parserListener) {
        this.parserEventSupport.removeParserListener(parserListener);
    }

    public void removeParserMatchListener(ParserMatchListener parserMatchListener) {
        this.parserEventSupport.removeParserMatchListener(parserMatchListener);
    }

    public void removeParserTokenListener(ParserTokenListener parserTokenListener) {
        this.parserEventSupport.removeParserTokenListener(parserTokenListener);
    }

    public void removeSemanticPredicateListener(SemanticPredicateListener semanticPredicateListener) {
        this.parserEventSupport.removeSemanticPredicateListener(semanticPredicateListener);
    }

    public void removeSyntacticPredicateListener(SyntacticPredicateListener syntacticPredicateListener) {
        this.parserEventSupport.removeSyntacticPredicateListener(syntacticPredicateListener);
    }

    public void removeTraceListener(TraceListener traceListener) {
        this.parserEventSupport.removeTraceListener(traceListener);
    }

    public void reportError(RecognitionException recognitionException) {
        this.parserEventSupport.fireReportError(recognitionException);
        super.reportError(recognitionException);
    }

    public void reportError(String string) {
        this.parserEventSupport.fireReportError(string);
        super.reportError(string);
    }

    public void reportWarning(String string) {
        this.parserEventSupport.fireReportWarning(string);
        super.reportWarning(string);
    }

    public void setDebugMode(boolean bl) {
        this._notDebugMode = !bl;
    }

    public void setupDebugging(TokenBuffer tokenBuffer) {
        this.setupDebugging(null, tokenBuffer);
    }

    public void setupDebugging(TokenStream tokenStream) {
        this.setupDebugging(tokenStream, null);
    }

    protected void setupDebugging(TokenStream tokenStream, TokenBuffer tokenBuffer) {
        this.setDebugMode(true);
        try {
            try {
                Utils.loadClass("javax.swing.JButton");
            }
            catch (ClassNotFoundException classNotFoundException) {
                System.err.println("Swing is required to use ParseView, but is not present in your CLASSPATH");
                System.exit(1);
            }
            Class clazz = Utils.loadClass("groovyjarjarantlr.parseview.ParseView");
            Constructor constructor = clazz.getConstructor(class$antlr$debug$LLkDebuggingParser == null ? (class$antlr$debug$LLkDebuggingParser = LLkDebuggingParser.class$("groovyjarjarantlr.debug.LLkDebuggingParser")) : class$antlr$debug$LLkDebuggingParser, class$antlr$TokenStream == null ? (class$antlr$TokenStream = LLkDebuggingParser.class$("groovyjarjarantlr.TokenStream")) : class$antlr$TokenStream, class$antlr$TokenBuffer == null ? (class$antlr$TokenBuffer = LLkDebuggingParser.class$("groovyjarjarantlr.TokenBuffer")) : class$antlr$TokenBuffer);
            constructor.newInstance(this, tokenStream, tokenBuffer);
        }
        catch (Exception exception) {
            System.err.println("Error initializing ParseView: " + exception);
            System.err.println("Please report this to Scott Stanchfield, thetick@magelang.com");
            System.exit(1);
        }
    }

    public synchronized void wakeUp() {
        this.notify();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

