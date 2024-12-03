/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.relaxns.reader.GrammarState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

class RootGrammarMergeState
extends SimpleState {
    RootGrammarMergeState() {
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("grammar")) {
            return new GrammarState();
        }
        return null;
    }
}

