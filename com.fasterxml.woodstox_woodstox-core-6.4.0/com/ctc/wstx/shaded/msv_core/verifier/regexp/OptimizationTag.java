/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import java.util.Hashtable;
import java.util.Map;

final class OptimizationTag {
    int stringCareLevel = -1;
    public static final int STRING_NOTCOMPUTED = -1;
    final Map simpleElementTokenResidual = new Hashtable();
    final Map transitions = new Hashtable();
    Expression attributePrunedExpression;

    OptimizationTag() {
    }

    protected static final class OwnerAndCont {
        final ElementExp owner;
        final Expression continuation;

        public OwnerAndCont(ElementExp owner, Expression cont) {
            this.owner = owner;
            this.continuation = cont;
        }
    }
}

