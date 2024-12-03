/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.relax.LabelRefState;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;

public class HedgeRefState
extends LabelRefState {
    protected final Expression resolve(String namespace, String label) {
        return ((RELAXReader)this.reader).resolveHedgeRef(namespace, label);
    }
}

