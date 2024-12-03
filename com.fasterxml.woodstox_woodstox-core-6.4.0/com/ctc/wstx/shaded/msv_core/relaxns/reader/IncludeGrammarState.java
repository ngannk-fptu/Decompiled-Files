/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.RootGrammarMergeState;

public class IncludeGrammarState
extends ChildlessState {
    protected void startSelf() {
        super.startSelf();
        String href = this.startTag.getAttribute("grammarLocation");
        if (href == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"include", (Object)"grammarLocation");
        } else {
            try {
                this.reader.switchSource(this, href, new RootGrammarMergeState());
            }
            catch (AbortException abortException) {
                // empty catch block
            }
        }
    }
}

