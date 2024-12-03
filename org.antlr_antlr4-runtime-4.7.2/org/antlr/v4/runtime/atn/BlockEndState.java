/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.BlockStartState;

public final class BlockEndState
extends ATNState {
    public BlockStartState startState;

    @Override
    public int getStateType() {
        return 8;
    }
}

