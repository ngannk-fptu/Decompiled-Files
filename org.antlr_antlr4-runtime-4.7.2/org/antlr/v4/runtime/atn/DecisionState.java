/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.atn.ATNState;

public abstract class DecisionState
extends ATNState {
    public int decision = -1;
    public boolean nonGreedy;
}

