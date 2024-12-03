/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithoutChildState;

public class NameClassNsNameState
extends NameClassWithoutChildState {
    protected NameClass makeNameClass() {
        return new NamespaceNameClass(this.getPropagatedNamespace());
    }
}

