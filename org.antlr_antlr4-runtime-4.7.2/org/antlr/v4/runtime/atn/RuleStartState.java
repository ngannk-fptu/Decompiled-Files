/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.RuleStopState;

public final class RuleStartState
extends ATNState {
    public RuleStopState stopState;
    public boolean isLeftRecursiveRule;

    @Override
    public int getStateType() {
        return 2;
    }
}

