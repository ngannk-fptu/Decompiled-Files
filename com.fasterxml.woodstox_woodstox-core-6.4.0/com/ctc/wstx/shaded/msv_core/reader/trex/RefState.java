/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;

public class RefState
extends ExpressionWithoutChildState {
    protected boolean parentRef;

    public RefState(boolean parentRef) {
        this.parentRef = parentRef;
    }

    protected Expression makeExpression() {
        String name = this.startTag.getCollapsedAttribute("name");
        if (name == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"ref", (Object)"name");
            return Expression.nullSet;
        }
        TREXGrammar grammar = ((TREXBaseReader)this.reader).grammar;
        if (this.parentRef && (grammar = grammar.getParentGrammar()) == null) {
            this.reader.reportError("TREXGrammarReader.NonExistentParentGrammar");
            return Expression.nullSet;
        }
        ReferenceExp r = grammar.namedPatterns.getOrCreate(name);
        this.wrapUp(r);
        return r;
    }

    protected void wrapUp(ReferenceExp r) {
        this.reader.backwardReference.memorizeLink(r);
    }
}

