/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;

public class StartState
extends SequenceState {
    protected final TREXBaseReader getReader() {
        return (TREXBaseReader)this.reader;
    }

    protected Expression annealExpression(Expression exp) {
        String name = this.startTag.getAttribute("name");
        if (name != null) {
            ReferenceExp ref = this.getReader().grammar.namedPatterns.getOrCreate(name);
            ref.exp = exp;
            exp = ref;
        }
        this.getReader().grammar.exp = exp;
        return null;
    }
}

