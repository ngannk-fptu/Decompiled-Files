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
import groovyjarjarantlr.debug.Tracer;

public class ParserReporter
extends Tracer
implements ParserListener {
    public void parserConsume(ParserTokenEvent parserTokenEvent) {
        System.out.println(this.indent + parserTokenEvent);
    }

    public void parserLA(ParserTokenEvent parserTokenEvent) {
        System.out.println(this.indent + parserTokenEvent);
    }

    public void parserMatch(ParserMatchEvent parserMatchEvent) {
        System.out.println(this.indent + parserMatchEvent);
    }

    public void parserMatchNot(ParserMatchEvent parserMatchEvent) {
        System.out.println(this.indent + parserMatchEvent);
    }

    public void parserMismatch(ParserMatchEvent parserMatchEvent) {
        System.out.println(this.indent + parserMatchEvent);
    }

    public void parserMismatchNot(ParserMatchEvent parserMatchEvent) {
        System.out.println(this.indent + parserMatchEvent);
    }

    public void reportError(MessageEvent messageEvent) {
        System.out.println(this.indent + messageEvent);
    }

    public void reportWarning(MessageEvent messageEvent) {
        System.out.println(this.indent + messageEvent);
    }

    public void semanticPredicateEvaluated(SemanticPredicateEvent semanticPredicateEvent) {
        System.out.println(this.indent + semanticPredicateEvent);
    }

    public void syntacticPredicateFailed(SyntacticPredicateEvent syntacticPredicateEvent) {
        System.out.println(this.indent + syntacticPredicateEvent);
    }

    public void syntacticPredicateStarted(SyntacticPredicateEvent syntacticPredicateEvent) {
        System.out.println(this.indent + syntacticPredicateEvent);
    }

    public void syntacticPredicateSucceeded(SyntacticPredicateEvent syntacticPredicateEvent) {
        System.out.println(this.indent + syntacticPredicateEvent);
    }
}

