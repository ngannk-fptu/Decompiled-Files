/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.DivInGrammarState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class RootMergedGrammarState
extends SimpleState
implements ExpressionOwner {
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("grammar")) {
            return new DivInGrammarState();
        }
        return null;
    }

    public void onEndChild(Expression exp) {
    }
}

