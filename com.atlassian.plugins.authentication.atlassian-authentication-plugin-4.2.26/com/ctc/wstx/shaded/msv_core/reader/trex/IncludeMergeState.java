/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class IncludeMergeState
extends SimpleState {
    protected State createChildState(StartTagInfo tag) {
        return null;
    }

    protected void endSelf() {
        String href = this.startTag.getAttribute("href");
        if (href == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"include", (Object)"href");
        } else {
            try {
                TREXBaseReader reader = (TREXBaseReader)this.reader;
                reader.switchSource(this, href, reader.sfactory.includedGrammar());
            }
            catch (AbortException abortException) {
                // empty catch block
            }
        }
        super.endSelf();
    }
}

