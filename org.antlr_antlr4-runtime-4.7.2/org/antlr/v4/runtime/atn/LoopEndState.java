/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;

public final class LoopEndState
extends ATNState {
    public ATNState loopBackState;

    @Override
    public int getStateType() {
        return 12;
    }
}

