/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class DivInGrammarState
extends ExpressionState
implements ExpressionOwner {
    protected final TREXBaseReader getReader() {
        return (TREXBaseReader)this.reader;
    }

    protected Expression makeExpression() {
        return null;
    }

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("start")) {
            return this.getReader().sfactory.start(this, tag);
        }
        if (tag.localName.equals("define")) {
            return this.getReader().sfactory.define(this, tag);
        }
        if (tag.localName.equals("include")) {
            return this.getReader().sfactory.includeGrammar(this, tag);
        }
        if (tag.localName.equals("div")) {
            return this.getReader().sfactory.divInGrammar(this, tag);
        }
        return null;
    }

    public void onEndChild(Expression exp) {
    }
}

