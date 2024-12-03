/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.SyntacticPredicateEvent;

public interface SyntacticPredicateListener
extends ListenerBase {
    public void syntacticPredicateFailed(SyntacticPredicateEvent var1);

    public void syntacticPredicateStarted(SyntacticPredicateEvent var1);

    public void syntacticPredicateSucceeded(SyntacticPredicateEvent var1);
}

