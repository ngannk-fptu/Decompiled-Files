/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.trex.DivInGrammarState;

public class GrammarState
extends DivInGrammarState {
    protected TREXGrammar previousGrammar;
    protected TREXGrammar newGrammar;

    protected Expression makeExpression() {
        return this.newGrammar;
    }

    protected void startSelf() {
        super.startSelf();
        this.previousGrammar = this.getReader().grammar;
        this.getReader().grammar = this.newGrammar = this.getReader().sfactory.createGrammar(this.reader.pool, this.previousGrammar);
    }

    public void endSelf() {
        TREXGrammar grammar = this.getReader().grammar;
        this.reader.detectUndefinedOnes(grammar.namedPatterns, "TREXGrammarReader.UndefinedPattern");
        if (grammar.exp == null) {
            this.reader.reportError("GrammarReader.Abstract.MissingTopLevel");
            grammar.exp = Expression.nullSet;
        }
        if (this.previousGrammar != null) {
            this.getReader().grammar = this.previousGrammar;
        }
        super.endSelf();
    }
}

