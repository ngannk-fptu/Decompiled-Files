/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;

public class GrammarState
extends com.ctc.wstx.shaded.msv_core.reader.trex.GrammarState {
    protected void startSelf() {
        super.startSelf();
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        if (reader.currentNamedPattern != null) {
            if (reader.directRefernce) {
                reader.currentNamedPattern.directRefs.add(this.newGrammar);
            } else {
                reader.currentNamedPattern.indirectRefs.add(this.newGrammar);
            }
        }
    }
}

