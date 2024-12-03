/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.MessageListener;
import groovyjarjarantlr.debug.ParserMatchListener;
import groovyjarjarantlr.debug.ParserTokenListener;
import groovyjarjarantlr.debug.SemanticPredicateListener;
import groovyjarjarantlr.debug.SyntacticPredicateListener;
import groovyjarjarantlr.debug.TraceListener;

public interface ParserListener
extends SemanticPredicateListener,
ParserMatchListener,
MessageListener,
ParserTokenListener,
TraceListener,
SyntacticPredicateListener {
}

