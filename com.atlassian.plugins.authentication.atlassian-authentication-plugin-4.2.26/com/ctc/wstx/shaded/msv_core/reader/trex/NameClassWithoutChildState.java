/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class NameClassWithoutChildState
extends NameClassState {
    protected final State createChildState(StartTagInfo tag) {
        return null;
    }
}

