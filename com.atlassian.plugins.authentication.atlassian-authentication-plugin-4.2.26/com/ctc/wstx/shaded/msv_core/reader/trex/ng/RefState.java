/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;

public class RefState
extends com.ctc.wstx.shaded.msv_core.reader.trex.RefState {
    public RefState(boolean parentRef) {
        super(parentRef);
    }

    protected void wrapUp(ReferenceExp r) {
        super.wrapUp(r);
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        if (reader.currentNamedPattern != null) {
            if (reader.directRefernce) {
                reader.currentNamedPattern.directRefs.add(r);
            } else {
                reader.currentNamedPattern.indirectRefs.add(r);
            }
        }
    }
}

