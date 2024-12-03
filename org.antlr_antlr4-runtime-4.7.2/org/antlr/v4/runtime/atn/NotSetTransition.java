/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.misc.IntervalSet;

public final class NotSetTransition
extends SetTransition {
    public NotSetTransition(ATNState target, IntervalSet set) {
        super(target, set);
    }

    @Override
    public int getSerializationType() {
        return 8;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !super.matches(symbol, minVocabSymbol, maxVocabSymbol);
    }

    @Override
    public String toString() {
        return '~' + super.toString();
    }
}

