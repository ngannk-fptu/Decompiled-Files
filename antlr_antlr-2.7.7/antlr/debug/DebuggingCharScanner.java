/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.CharScanner;
import antlr.CharStreamException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.MismatchedCharException;
import antlr.Token;
import antlr.collections.impl.BitSet;
import antlr.debug.DebuggingParser;
import antlr.debug.MessageListener;
import antlr.debug.NewLineListener;
import antlr.debug.ParserEventSupport;
import antlr.debug.ParserListener;
import antlr.debug.ParserMatchListener;
import antlr.debug.ParserTokenListener;
import antlr.debug.SemanticPredicateListener;
import antlr.debug.SyntacticPredicateListener;
import antlr.debug.TraceListener;

public abstract class DebuggingCharScanner
extends CharScanner
implements DebuggingParser {
    private ParserEventSupport parserEventSupport = new ParserEventSupport(this);
    private boolean _notDebugMode = false;
    protected String[] ruleNames;
    protected String[] semPredNames;

    public DebuggingCharScanner(InputBuffer inputBuffer) {
        super(inputBuffer);
    }

    public DebuggingCharScanner(LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
    }

    public void addMessageListener(MessageListener messageListener) {
        this.parserEventSupport.addMessageListener(messageListener);
    }

    public void addNewLineListener(NewLineListener newLineListener) {
        this.parserEventSupport.addNewLineListener(newLineListener);
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

    public void consume() throws CharStreamException {
        int n = -99;
        try {
            n = this.LA(1);
        }
        catch (CharStreamException charStreamException) {
            // empty catch block
        }
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

    public char LA(int n) throws CharStreamException {
        char c = super.LA(n);
        this.parserEventSupport.fireLA(n, c);
        return c;
    }

    protected Token makeToken(int n) {
        return super.makeToken(n);
    }

    public void match(char c) throws MismatchedCharException, CharStreamException {
        char c2 = this.LA(1);
        try {
            super.match(c);
            this.parserEventSupport.fireMatch(c, this.inputState.guessing);
        }
        catch (MismatchedCharException mismatchedCharException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(c2, c, this.inputState.guessing);
            }
            throw mismatchedCharException;
        }
    }

    public void match(BitSet bitSet) throws MismatchedCharException, CharStreamException {
        String string = this.text.toString();
        char c = this.LA(1);
        try {
            super.match(bitSet);
            this.parserEventSupport.fireMatch(c, bitSet, string, this.inputState.guessing);
        }
        catch (MismatchedCharException mismatchedCharException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch((int)c, bitSet, string, this.inputState.guessing);
            }
            throw mismatchedCharException;
        }
    }

    public void match(String string) throws MismatchedCharException, CharStreamException {
        StringBuffer stringBuffer = new StringBuffer("");
        int n = string.length();
        try {
            for (int i = 1; i <= n; ++i) {
                stringBuffer.append(super.LA(i));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            super.match(string);
            this.parserEventSupport.fireMatch(string, this.inputState.guessing);
        }
        catch (MismatchedCharException mismatchedCharException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(stringBuffer.toString(), string, this.inputState.guessing);
            }
            throw mismatchedCharException;
        }
    }

    public void matchNot(char c) throws MismatchedCharException, CharStreamException {
        char c2 = this.LA(1);
        try {
            super.matchNot(c);
            this.parserEventSupport.fireMatchNot(c2, c, this.inputState.guessing);
        }
        catch (MismatchedCharException mismatchedCharException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatchNot(c2, c, this.inputState.guessing);
            }
            throw mismatchedCharException;
        }
    }

    public void matchRange(char c, char c2) throws MismatchedCharException, CharStreamException {
        char c3 = this.LA(1);
        try {
            super.matchRange(c, c2);
            this.parserEventSupport.fireMatch(c3, "" + c + c2, this.inputState.guessing);
        }
        catch (MismatchedCharException mismatchedCharException) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(c3, "" + c + c2, this.inputState.guessing);
            }
            throw mismatchedCharException;
        }
    }

    public void newline() {
        super.newline();
        this.parserEventSupport.fireNewLine(this.getLine());
    }

    public void removeMessageListener(MessageListener messageListener) {
        this.parserEventSupport.removeMessageListener(messageListener);
    }

    public void removeNewLineListener(NewLineListener newLineListener) {
        this.parserEventSupport.removeNewLineListener(newLineListener);
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

    public void reportError(MismatchedCharException mismatchedCharException) {
        this.parserEventSupport.fireReportError(mismatchedCharException);
        super.reportError(mismatchedCharException);
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

    public void setupDebugging() {
    }

    public synchronized void wakeUp() {
        this.notify();
    }
}

