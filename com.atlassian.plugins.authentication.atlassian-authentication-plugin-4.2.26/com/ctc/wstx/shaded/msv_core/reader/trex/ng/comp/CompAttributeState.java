/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.AttributeState;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.RELAXNGCompReader;

public class CompAttributeState
extends AttributeState {
    protected Expression annealExpression(Expression contentModel) {
        Expression exp = super.annealExpression(contentModel);
        String defaultValue = this.startTag.getAttribute("http://relaxng.org/ns/compatibility/annotations/1.0", "defaultValue");
        if (defaultValue != null && exp instanceof AttributeExp) {
            RELAXNGCompReader reader = (RELAXNGCompReader)this.reader;
            reader.addDefaultValue((AttributeExp)exp, defaultValue);
        }
        return exp;
    }
}

