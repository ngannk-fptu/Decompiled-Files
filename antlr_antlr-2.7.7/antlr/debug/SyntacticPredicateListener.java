/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ListenerBase;
import antlr.debug.SyntacticPredicateEvent;

public interface SyntacticPredicateListener
extends ListenerBase {
    public void syntacticPredicateFailed(SyntacticPredicateEvent var1);

    public void syntacticPredicateStarted(SyntacticPredicateEvent var1);

    public void syntacticPredicateSucceeded(SyntacticPredicateEvent var1);
}

