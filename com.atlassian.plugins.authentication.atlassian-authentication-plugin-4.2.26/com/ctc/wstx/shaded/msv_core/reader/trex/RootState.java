/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.RootIncludedPatternState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class RootState
extends RootIncludedPatternState {
    private boolean simple = false;

    public RootState() {
        super(null);
    }

    protected State createChildState(StartTagInfo tag) {
        TREXBaseReader reader = (TREXBaseReader)this.reader;
        if (tag.localName.equals("grammar")) {
            return reader.sfactory.grammar(null, tag);
        }
        State s = super.createChildState(tag);
        if (s != null) {
            reader.grammar = reader.sfactory.createGrammar(reader.pool, null);
            this.simple = true;
        }
        return s;
    }

    public void onEndChild(Expression exp) {
        super.onEndChild(exp);
        TREXBaseReader reader = (TREXBaseReader)this.reader;
        if (this.simple) {
            reader.grammar.exp = exp;
        }
        reader.wrapUp();
    }
}

