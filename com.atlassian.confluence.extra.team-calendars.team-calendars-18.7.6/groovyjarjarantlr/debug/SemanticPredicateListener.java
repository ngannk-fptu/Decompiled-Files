/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.SemanticPredicateEvent;

public interface SemanticPredicateListener
extends ListenerBase {
    public void semanticPredicateEvaluated(SemanticPredicateEvent var1);
}

