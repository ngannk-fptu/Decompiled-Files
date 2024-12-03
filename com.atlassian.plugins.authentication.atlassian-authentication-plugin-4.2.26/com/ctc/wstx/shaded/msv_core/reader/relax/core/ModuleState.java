/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.ModuleMergeState;

public class ModuleState
extends ModuleMergeState {
    protected ModuleState(String expectedNamespace) {
        super(expectedNamespace);
    }

    protected void startSelf() {
        super.startSelf();
        this.getReader().module = new RELAXModule(this.reader.pool, this.targetNamespace);
    }
}

