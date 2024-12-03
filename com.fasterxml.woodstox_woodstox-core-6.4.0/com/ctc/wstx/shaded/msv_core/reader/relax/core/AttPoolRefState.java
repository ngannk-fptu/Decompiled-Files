/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;

public class AttPoolRefState
extends ExpressionWithoutChildState {
    protected Expression makeExpression() {
        String role = this.startTag.getAttribute("role");
        if (role == null) {
            this.reader.reportError("GrammarReader.MissingAttribute", (Object)"ref", (Object)"role");
            return Expression.epsilon;
        }
        String namespace = this.startTag.getAttribute("namespace");
        RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        return reader.resolveAttPoolRef(namespace, role);
    }
}

