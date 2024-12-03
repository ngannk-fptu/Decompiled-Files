/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.typed;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.trex.typed.TypedElementPattern;
import com.ctc.wstx.shaded.msv_core.reader.trex.ElementState;

public class TypedElementState
extends ElementState {
    protected Expression annealExpression(Expression contentModel) {
        String label = this.startTag.getAttribute("http://www.sun.com/xml/msv/trex-type", "label");
        if (label == null) {
            return super.annealExpression(contentModel);
        }
        return new TypedElementPattern(this.nameClass, contentModel, label);
    }
}

