/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.IntervalSet;

public class SetTransition
extends Transition {
    public final IntervalSet set;

    public SetTransition(ATNState target, IntervalSet set) {
        super(target);
        if (set == null) {
            set = IntervalSet.of(0);
        }
        this.set = set;
    }

    @Override
    public int getSerializationType() {
        return 7;
    }

    @Override
    public IntervalSet label() {
        return this.set;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return this.set.contains(symbol);
    }

    public String toString() {
        return this.set.toString();
    }
}

