/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.StarLoopbackState;

public final class StarLoopEntryState
extends DecisionState {
    public StarLoopbackState loopBackState;
    public boolean isPrecedenceDecision;

    @Override
    public int getStateType() {
        return 10;
    }
}

