/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.MessageListener;
import antlr.debug.ParserMatchListener;
import antlr.debug.ParserTokenListener;
import antlr.debug.SemanticPredicateListener;
import antlr.debug.SyntacticPredicateListener;
import antlr.debug.TraceListener;

public interface ParserListener
extends SemanticPredicateListener,
ParserMatchListener,
MessageListener,
ParserTokenListener,
TraceListener,
SyntacticPredicateListener {
}

