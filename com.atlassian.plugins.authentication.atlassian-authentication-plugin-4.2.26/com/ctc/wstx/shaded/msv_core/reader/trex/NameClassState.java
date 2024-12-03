/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassOwner;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;

public abstract class NameClassState
extends SimpleState {
    public final void endSelf() {
        ((NameClassOwner)((Object)this.parentState)).onEndChild(this.makeNameClass());
        super.endSelf();
    }

    protected abstract NameClass makeNameClass();

    protected final String getPropagatedNamespace() {
        return ((TREXBaseReader)this.reader).targetNamespace;
    }
}

