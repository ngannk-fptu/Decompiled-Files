/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.Transition;

public abstract class AbstractPredicateTransition
extends Transition {
    public AbstractPredicateTransition(ATNState target) {
        super(target);
    }
}

