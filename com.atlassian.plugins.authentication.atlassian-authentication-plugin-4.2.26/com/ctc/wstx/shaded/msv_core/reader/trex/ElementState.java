/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassAndExpressionState;

public class ElementState
extends NameClassAndExpressionState {
    protected Expression annealExpression(Expression contentModel) {
        ElementPattern e = new ElementPattern(this.nameClass, contentModel);
        this.reader.setDeclaredLocationOf(e);
        return e;
    }
}

