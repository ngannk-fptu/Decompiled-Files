/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;

public class ElementState
extends com.ctc.wstx.shaded.msv_core.reader.trex.ElementState {
    private boolean previousDirectReference;

    protected void startSelf() {
        super.startSelf();
        this.previousDirectReference = ((RELAXNGReader)this.reader).directRefernce;
        ((RELAXNGReader)this.reader).directRefernce = false;
    }

    protected void endSelf() {
        RELAXNGReader reader = (RELAXNGReader)this.reader;
        reader.directRefernce = this.previousDirectReference;
        super.endSelf();
        reader.restrictionChecker.checkNameClass(this.nameClass);
    }
}

