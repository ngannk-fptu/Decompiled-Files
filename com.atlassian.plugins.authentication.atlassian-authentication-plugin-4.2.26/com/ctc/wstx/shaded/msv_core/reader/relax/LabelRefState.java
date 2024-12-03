/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;

abstract class LabelRefState
extends ExpressionWithoutChildState {
    LabelRefState() {
    }

    protected Expression makeExpression() {
        String label = this.startTag.getAttribute("label");
        String namespace = this.startTag.getAttribute("namespace");
        RELAXReader reader = (RELAXReader)this.reader;
        if (label == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"label");
            return Expression.nullSet;
        }
        return this.resolve(namespace, label);
    }

    protected abstract Expression resolve(String var1, String var2);
}

