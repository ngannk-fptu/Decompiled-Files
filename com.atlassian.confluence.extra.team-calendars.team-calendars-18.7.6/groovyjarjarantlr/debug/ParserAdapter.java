/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.MessageEvent;
import groovyjarjarantlr.debug.ParserListener;
import groovyjarjarantlr.debug.ParserMatchEvent;
import groovyjarjarantlr.debug.ParserTokenEvent;
import groovyjarjarantlr.debug.SemanticPredicateEvent;
import groovyjarjarantlr.debug.SyntacticPredicateEvent;
import groovyjarjarantlr.debug.TraceEvent;

public class ParserAdapter
implements ParserListener {
    public void doneParsing(TraceEvent traceEvent) {
    }

    public void enterRule(TraceEvent traceEvent) {
    }

    public void exitRule(TraceEvent traceEvent) {
    }

    public void parserConsume(ParserTokenEvent parserTokenEvent) {
    }

    public void parserLA(ParserTokenEvent parserTokenEvent) {
    }

    public void parserMatch(ParserMatchEvent parserMatchEvent) {
    }

    public void parserMatchNot(ParserMatchEvent parserMatchEvent) {
    }

    public void parserMismatch(ParserMatchEvent parserMatchEvent) {
    }

    public void parserMismatchNot(ParserMatchEvent parserMatchEvent) {
    }

    public void refresh() {
    }

    public void reportError(MessageEvent messageEvent) {
    }

    public void reportWarning(MessageEvent messageEvent) {
    }

    public void semanticPredicateEvaluated(SemanticPredicateEvent semanticPredicateEvent) {
    }

    public void syntacticPredicateFailed(SyntacticPredicateEvent syntacticPredicateEvent) {
    }

    public void syntacticPredicateStarted(SyntacticPredicateEvent syntacticPredicateEvent) {
    }

    public void syntacticPredicateSucceeded(SyntacticPredicateEvent syntacticPredicateEvent) {
    }
}

