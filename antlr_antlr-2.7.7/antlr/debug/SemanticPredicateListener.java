/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ListenerBase;
import antlr.debug.SemanticPredicateEvent;

public interface SemanticPredicateListener
extends ListenerBase {
    public void semanticPredicateEvaluated(SemanticPredicateEvent var1);
}

