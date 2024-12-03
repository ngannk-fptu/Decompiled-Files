/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RootModuleMergeState;

public class IncludeModuleState
extends ChildlessState {
    protected void startSelf() {
        super.startSelf();
        String href = this.startTag.getAttribute("moduleLocation");
        if (href == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"include", (Object)"moduleLocation");
        } else {
            try {
                this.reader.switchSource(this, href, new RootModuleMergeState());
            }
            catch (AbortException abortException) {
                // empty catch block
            }
        }
    }
}

